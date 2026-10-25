package fi.ishtech.practice.oms.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import fi.ishtech.practice.oms.consts.OmsConstants;
import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
import fi.ishtech.practice.oms.mapper.SalesOrderItemMapper;
import fi.ishtech.practice.oms.mapper.SalesOrderMapper;
import fi.ishtech.practice.oms.payload.SalesOrderVo;
import fi.ishtech.practice.oms.repo.SalesOrderItemRepo;
import fi.ishtech.practice.oms.repo.SalesOrderRepo;
import fi.ishtech.practice.oms.service.CustomerDiscountService;
import fi.ishtech.practice.oms.service.ProductService;
import fi.ishtech.practice.oms.service.SalesOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Service
@Slf4j
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private SalesOrderRepo salesOrderRepo;

	@Autowired
	private SalesOrderMapper salesOrderMapper;

	@Autowired
	private SalesOrderItemRepo salesOrderItemRepo;

	@Autowired
	private SalesOrderItemMapper salesOrderItemMapper;

	@Autowired
	private CustomerDiscountService customerDiscountService;

	@Autowired
	private ProductService productService;

	@Value("${ishtech-oms.discounts.apply-all:false}")
	private boolean isApplyAllDiscounts;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public SalesOrderRepo getRepo() {
		return salesOrderRepo;
	}

	@Override
	public SalesOrderMapper getMapper() {
		return salesOrderMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public SalesOrder create(final SalesOrderVo salesOrderVo) {
		Assert.isTrue(salesOrderVo.getCustomerId() != null, "salesOrderId cannot be null");

		SalesOrder salesOrder = salesOrderMapper.toNewEntity(salesOrderVo);

		salesOrder = salesOrderRepo.saveAndFlush(salesOrder);
		Long salesOrderId = salesOrder.getId();
		log.info("New SalesOrder({}) created", salesOrderId);

		if (!CollectionUtils.isEmpty(salesOrderVo.getSalesOrderItems())) {
			Set<SalesOrderItem> salesOrderItems = salesOrderItemMapper.toNewEntity(salesOrderVo.getSalesOrderItems());
			salesOrderItems.forEach(salesOrderItem -> {
				salesOrderItem.setSalesOrderId(salesOrderId);

				var product = productService.findOneByIdOrElseThrow(salesOrderItem.getProductId());

				var unitPrice = product.getUnitPrice();
				var lineAmount = unitPrice.multiply(BigDecimal.valueOf(salesOrderItem.getQuantity()))
						.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);

				salesOrderItem.setUnitPrice(unitPrice);
				salesOrderItem.setOrigLineAmount(lineAmount);
				salesOrderItem.setLineAmount(lineAmount);
			});

			salesOrderItemRepo.saveAllAndFlush(salesOrderItems);
			log.info("Created {} new SalesOrderItem(s) for SalesOrder({})", salesOrderItems.size(), salesOrderId);

			refresh(salesOrder);
		}

		updateSalesOrderAmountsAndSave(salesOrder);

		return salesOrder;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateSalesOrderAmountsAndSave(Long salesOrderId) {
		SalesOrder salesOrder = this.findOneByIdOrElseThrow(salesOrderId);
		this.updateSalesOrderAmountsAndSave(salesOrder);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void updateSalesOrderAmountsAndSave(final SalesOrder salesOrder) {
		updateSalesOrderAmounts(salesOrder);

		salesOrderRepo.saveAndFlush(salesOrder);
		log.info("Updated amounts of SalesOrder({})", salesOrder.getId());
	}

	private void updateSalesOrderAmounts(final SalesOrder salesOrder) {
		calcItemDiscounts(salesOrder);

		BigDecimal origTotalAmount = salesOrder.getOrigTotalAmount() != null ? salesOrder.getOrigTotalAmount()
				: BigDecimal.ZERO;
		BigDecimal totalAmount = salesOrder.getTotalAmount() != null ? salesOrder.getTotalAmount() : BigDecimal.ZERO;
		BigDecimal discountPercent = salesOrder.getDiscountPercent() != null ? salesOrder.getDiscountPercent()
				: BigDecimal.ZERO;
		BigDecimal discountAmount = salesOrder.getDiscountAmount() != null ? salesOrder.getDiscountAmount()
				: BigDecimal.ZERO;
		BigDecimal netAmount = salesOrder.getNetAmount() != null ? salesOrder.getNetAmount() : BigDecimal.ZERO;

		// @formatter:off
		BigDecimal discountAmountForProduct = salesOrder.getSalesOrderItems() != null
				? salesOrder.getSalesOrderItems()
						.stream()
						.filter(SalesOrderItem::isActive)
						.map(salesOrderItem -> Optional.ofNullable(salesOrderItem.getDiscountAmount()).orElse(BigDecimal.ZERO))
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;
		// @formatter:on

		// DiscountType SALES_ORDER_PERCENT
		CustomerDiscount customerDiscountForSalesOrderPercent = customerDiscountService
				.findActiveOneByDiscountTypeSalesOrderPercent(salesOrder.getCustomerId());

		if (customerDiscountForSalesOrderPercent != null) {
			if (!isApplyAllDiscounts) {
				BigDecimal discountAmountForSalesOrderPercent = origTotalAmount
						.multiply(customerDiscountForSalesOrderPercent.getDiscountPercent())
						.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);

				// apply best discount between salesOrder discount vs product discount
				if (discountAmountForSalesOrderPercent.compareTo(discountAmountForProduct) >= 0) {
					discountPercent = customerDiscountForSalesOrderPercent.getDiscountPercent();
					discountAmount = discountAmountForSalesOrderPercent;

					// if salesOrder discount is applied we need to reverse product discount
					totalAmount = origTotalAmount;

					if (salesOrder.getSalesOrderItems() != null) {
						for (SalesOrderItem salesOrderItem : salesOrder.getSalesOrderItems()) {
							if (!salesOrderItem.isActive()) {
								continue;
							}
							salesOrderItem.setLineAmount(salesOrderItem.getOrigLineAmount());
							salesOrderItem.setDiscountPercent(BigDecimal.ZERO);
							salesOrderItem.setDiscountAmount(BigDecimal.ZERO);
						}
					}

				} else {
					discountPercent = BigDecimal.ZERO;
					discountAmount = BigDecimal.ZERO;
				}
			} else {
				BigDecimal discountAmountForSalesOrderPercent = totalAmount.multiply(discountPercent)
						.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);
				discountPercent = customerDiscountForSalesOrderPercent.getDiscountPercent();
				discountAmount = discountAmountForSalesOrderPercent;
			}
		}

		netAmount = totalAmount.subtract(discountAmount);

		salesOrder.setDiscountPercent(discountPercent);
		salesOrder.setDiscountAmount(discountAmount);
		salesOrder.setOrigTotalAmount(origTotalAmount);
		salesOrder.setTotalAmount(totalAmount);
		salesOrder.setNetAmount(netAmount);
	}

	/**
	 * Calculates
	 * 
	 * @param salesOrder
	 */
	private void calcItemDiscounts(final SalesOrder salesOrder) {
		BigDecimal origTotalAmount = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal discountAmount = BigDecimal.ZERO;

		if (salesOrder.getSalesOrderItems() != null) {
			for (SalesOrderItem salesOrderItem : salesOrder.getSalesOrderItems()) {
				if (!salesOrderItem.isActive()) {
					continue;
				}
				BigDecimal origLineAmount = salesOrderItem.getUnitPrice()
						.multiply(BigDecimal.valueOf(salesOrderItem.getQuantity()))
						.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);
				origTotalAmount = origTotalAmount.add(origLineAmount);
				salesOrderItem.setOrigLineAmount(origLineAmount);

				BigDecimal lineDiscountPercent = BigDecimal.ZERO;
				BigDecimal lineDiscountAmount;
				BigDecimal discountAmountForProductPercent = BigDecimal.ZERO;
				BigDecimal discountAmountForProductBuyXPayY = BigDecimal.ZERO;

				// DiscountType PRODUCT_PERCENT
				CustomerDiscount customerDiscountForProductPercent = customerDiscountService
						.findActiveOneByDiscountTypeProduct(salesOrder.getCustomerId(), salesOrderItem.getProductId(),
								DiscountTypeEnum.PRODUCT_PERCENT);

				if (customerDiscountForProductPercent != null) {
					Assert.notNull(customerDiscountForProductPercent.getDiscountPercent(), "Invalid Customer Discount");

					lineDiscountPercent = customerDiscountForProductPercent.getDiscountPercent();
					discountAmountForProductPercent = origLineAmount.multiply(lineDiscountPercent)
							.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);
				}

				// DiscountType PRODUCT_BUY_X_PAY_Y
				CustomerDiscount customerDiscountForProductBuyXPayY = customerDiscountService
						.findActiveOneByDiscountTypeProduct(salesOrder.getCustomerId(), salesOrderItem.getProductId(),
								DiscountTypeEnum.PRODUCT_BUY_X_PAY_Y);

				if (customerDiscountForProductBuyXPayY != null) {
					Assert.isTrue(customerDiscountForProductBuyXPayY.getBuyQuantity() != null
							&& customerDiscountForProductBuyXPayY.getBuyQuantity() != null
							&& customerDiscountForProductBuyXPayY.getBuyQuantity()
									.intValue() >= customerDiscountForProductBuyXPayY.getPayQuantity().intValue(),
							"Invalid Customer Discount");

					int discountQuantity = calcDiscountQuantityForBuyXPayY(salesOrderItem.getQuantity(),
							customerDiscountForProductBuyXPayY.getBuyQuantity(),
							customerDiscountForProductBuyXPayY.getPayQuantity());

					discountAmountForProductBuyXPayY = salesOrderItem.getUnitPrice()
							.multiply(BigDecimal.valueOf(discountQuantity))
							.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);
				}

				if (isApplyAllDiscounts) {
					lineDiscountAmount = discountAmountForProductPercent.add(discountAmountForProductBuyXPayY);
				} else {
					// Only best discount
					if (discountAmountForProductPercent.compareTo(discountAmountForProductBuyXPayY) >= 0) {
						lineDiscountAmount = discountAmountForProductPercent;
					} else {
						lineDiscountAmount = discountAmountForProductBuyXPayY;
						lineDiscountPercent = BigDecimal.ZERO;
					}
				}

				salesOrderItem.setDiscountPercent(lineDiscountPercent);
				salesOrderItem.setDiscountAmount(lineDiscountAmount);

				salesOrderItem = salesOrderItemRepo.saveAndFlush(salesOrderItem);
				log.info("Updated amounts of SalesOrderItem({})", salesOrderItem.getId());

				BigDecimal lineAmount = origLineAmount.subtract(lineDiscountAmount);
				salesOrderItem.setLineAmount(origLineAmount.subtract(lineDiscountAmount));
				totalAmount = totalAmount.add(lineAmount);

				discountAmount = discountAmount.add(salesOrderItem.getDiscountAmount());
			}
		}

		salesOrder.setOrigTotalAmount(origTotalAmount);
		salesOrder.setTotalAmount(totalAmount);
	}

	/**
	 * Calculates the discount quantity for "Buy X, Pay Y".<br>
	 *
	 * If buy 3 pay 2 and order has 4 or 5 items, then they will get discount of 1 unit only.<br>
	 * If buy 5 pay 3 and order has 6, 7, 8 or 9 items, then they will get discount of 2 units only.<br>
	 * If buy 3 pay 2 and order has 7 items, then it is like buy 6 pay 4, so for 7 items they will get discount of 2
	 * units only.<br>
	 * If buy 6 pay 4 and order has 3 items, then it will not be like buy 3 pay 2, they will not get any discount.<br>
	 *
	 * @param quantity - SalesOrderItem quantity
	 * @param buyX     - CustomerDiscount buyQuantity
	 * @param payY     - CustomerDiscount sellQuantity
	 *
	 * @return int - discountedQuantity - how many items they will get free
	 */
	private int calcDiscountQuantityForBuyXPayY(final int quantity, final int buyX, final int payY) {
		log.trace("uantity={}, buyX={}, payY={}", quantity, buyX, payY);

		int freeUnitsPerSet = buyX - payY;
		int fullSets = quantity / buyX;

		int discountQuantity = freeUnitsPerSet * fullSets;
		log.trace("discountQuantity={}", discountQuantity);

		return discountQuantity;
	}

}