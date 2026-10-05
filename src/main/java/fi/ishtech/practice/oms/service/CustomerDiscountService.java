package fi.ishtech.practice.oms.service;

import jakarta.validation.Valid;

import fi.ishtech.base.service.BaseStandardService;
import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
import fi.ishtech.practice.oms.payload.CustomerDiscountVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
public interface CustomerDiscountService extends BaseStandardService<CustomerDiscount, CustomerDiscountVo> {

	/**
	 * Creates a new CustomerDiscount
	 *
	 * @param productVo - {@link CustomerDiscountVo}
	 * @return {@link CustomerDiscount}
	 */
	CustomerDiscount create(CustomerDiscountVo productVo);

	CustomerDiscountVo updateAndMapToVo(@Valid CustomerDiscountVo productVo);

	/**
	 *
	 * @param customerId
	 * @return {@link CustomerDiscount}
	 */
	CustomerDiscount findActiveOneByDiscountTypeSalesOrderPercent(Long customerId);

	/**
	 *
	 * @param customerId
	 * @param productId
	 * @param discountType
	 * @return {@link CustomerDiscount}
	 */
	CustomerDiscount findActiveOneByDiscountTypeProduct(Long customerId, Long productId, DiscountTypeEnum discountType);

}