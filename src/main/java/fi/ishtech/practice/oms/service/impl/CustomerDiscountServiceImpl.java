package fi.ishtech.practice.oms.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
import fi.ishtech.practice.oms.mapper.CustomerDiscountMapper;
import fi.ishtech.practice.oms.payload.CustomerDiscountVo;
import fi.ishtech.practice.oms.repo.CustomerDiscountRepo;
import fi.ishtech.practice.oms.service.CustomerDiscountService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Service
@Slf4j
@Transactional
public class CustomerDiscountServiceImpl implements CustomerDiscountService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CustomerDiscountRepo customerDiscountRepo;

	@Autowired
	private CustomerDiscountMapper customerDiscountMapper;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public CustomerDiscountRepo getRepo() {
		return customerDiscountRepo;
	}

	@Override
	public CustomerDiscountMapper getMapper() {
		return customerDiscountMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public CustomerDiscount create(@Valid CustomerDiscountVo customerDiscountVo) {
		CustomerDiscount customerDiscount = customerDiscountMapper.toNewEntity(customerDiscountVo);

		customerDiscount = customerDiscountRepo.saveAndFlush(customerDiscount);
		log.info("New CustomerDiscount({}) created", customerDiscount.getId());

		return customerDiscount;
	}

	@Override
	public CustomerDiscountVo updateAndMapToVo(@Valid CustomerDiscountVo customerDiscountVo) {
		CustomerDiscount customerDiscount = this.findOneByIdOrElseThrow(customerDiscountVo.getId());

		customerDiscount = customerDiscountMapper.toExistingEntity(customerDiscountVo, customerDiscount);
		customerDiscount = customerDiscountRepo.saveAndFlush(customerDiscount);

		refresh(customerDiscount);

		return customerDiscountMapper.toSemiDetailVo(customerDiscount);
	}

	@Override
	public CustomerDiscount findActiveOneByDiscountTypeSalesOrderPercent(final Long customerId) {
		return this.findOneByCustomerIdAndProductIdIsNullAndDiscountTypeAndIsActiveTrue(customerId,
				DiscountTypeEnum.SALES_ORDER_PERCENT);
	}

	@Override
	public CustomerDiscount findActiveOneByDiscountTypeProduct(final Long customerId, final Long productId,
			final DiscountTypeEnum discountType) {
		Assert.isTrue(discountType == DiscountTypeEnum.PRODUCT_PERCENT
				|| discountType == DiscountTypeEnum.PRODUCT_BUY_X_PAY_Y, "Invalid discountType");

		return this.findOneByCustomerIdAndProductIdAndDiscountTypeAndIsActiveTrue(customerId, productId, discountType);
	}

	private CustomerDiscount findOneByCustomerIdAndProductIdIsNullAndDiscountTypeAndIsActiveTrue(final Long customerId,
			final DiscountTypeEnum discountType) {
		return customerDiscountRepo.findOneByCustomerIdAndProductIdIsNullAndDiscountTypeAndIsActiveTrue(customerId,
				discountType);
	}

	private CustomerDiscount findOneByCustomerIdAndProductIdAndDiscountTypeAndIsActiveTrue(final Long customerId,
			final Long productId, final DiscountTypeEnum discountType) {
		return customerDiscountRepo.findOneByCustomerIdAndProductIdAndDiscountTypeAndIsActiveTrue(customerId, productId,
				discountType);
	}

}