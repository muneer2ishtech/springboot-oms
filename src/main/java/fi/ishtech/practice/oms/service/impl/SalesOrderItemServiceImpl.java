package fi.ishtech.practice.oms.service.impl;

import java.math.BigDecimal;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fi.ishtech.practice.oms.consts.OmsConstants;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.mapper.SalesOrderItemMapper;
import fi.ishtech.practice.oms.payload.SalesOrderItemVo;
import fi.ishtech.practice.oms.repo.SalesOrderItemRepo;
import fi.ishtech.practice.oms.service.ProductService;
import fi.ishtech.practice.oms.service.SalesOrderItemService;
import fi.ishtech.practice.oms.service.SalesOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Service
@Slf4j
@Transactional
public class SalesOrderItemServiceImpl implements SalesOrderItemService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private SalesOrderItemRepo salesOrderItemRepo;

	@Autowired
	private SalesOrderItemMapper salesOrderItemMapper;

	@Autowired
	private SalesOrderService salesOrderService;

	@Autowired
	private ProductService productService;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public SalesOrderItemRepo getRepo() {
		return salesOrderItemRepo;
	}

	@Override
	public SalesOrderItemMapper getMapper() {
		return salesOrderItemMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public SalesOrderItem create(SalesOrderItemVo salesOrderItemVo) {
		Assert.isTrue(salesOrderItemVo.getSalesOrderId() != null, "salesOrderId cannot be null");

		SalesOrderItem salesOrderItem = salesOrderItemMapper.toNewEntity(salesOrderItemVo);

		var product = productService.findOneByIdOrElseThrow(salesOrderItem.getProductId());

		var unitPrice = product.getUnitPrice();
		var lineAmount = unitPrice.multiply(BigDecimal.valueOf(salesOrderItem.getQuantity()))
				.setScale(OmsConstants.DEFAULT_SCALE, OmsConstants.DEFAULT_ROUNDING_MODE);

		salesOrderItem.setUnitPrice(unitPrice);
		salesOrderItem.setOrigLineAmount(lineAmount);
		salesOrderItem.setLineAmount(lineAmount);

		salesOrderItem = salesOrderItemRepo.saveAndFlush(salesOrderItem);
		log.info("New SalesOrderItem({}) created", salesOrderItem.getId());

		refresh(salesOrderItem);

		// calculating discounts and updating discounts and amounts in SalesOrder
		salesOrderService.updateSalesOrderAmountsAndSave(salesOrderItem.getSalesOrder());

		return salesOrderItem;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void deleteById(Long id) {
		SalesOrderItem salesOrderItem = this.findOneByIdOrElseThrow(id);

		Long salesOrderId = salesOrderItem.getSalesOrderId();

		salesOrderItemRepo.delete(salesOrderItem);
		log.info("Hard deleted SalesOrderItem({})", id);

		salesOrderItemRepo.flush();

		salesOrderService.updateSalesOrderAmountsAndSave(salesOrderId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void updateQuantityById(Long id, Integer quantity) {
		SalesOrderItem salesOrderItem = this.findOneByIdOrElseThrow(id);

		salesOrderItem.setQuantity(quantity);
		salesOrderItem = salesOrderItemRepo.saveAndFlush(salesOrderItem);
		log.info("Updated SalesOrderItem({})", id);

		salesOrderService.updateSalesOrderAmountsAndSave(salesOrderItem.getSalesOrder());
	}

}