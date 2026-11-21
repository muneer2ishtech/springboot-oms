package fi.ishtech.practice.oms.service;

import fi.ishtech.springboot.base.service.BaseStandardService;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.payload.SalesOrderItemVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
public interface SalesOrderItemService extends BaseStandardService<SalesOrderItem, SalesOrderItemVo> {

	/**
	 * Creates a new SalesOrderItem.<br>
	 * And then updates parent SalesOrder amounts and discounts
	 *
	 * @param salesOrderItemVo - {@link SalesOrderItemVo}
	 * @return {@link SalesOrderItem}
	 */
	SalesOrderItem create(SalesOrderItemVo salesOrderItemVo);

	/**
	 * Hard deletes SalesOrderItem.<br>
	 * And then updates parent SalesOrder amounts and discounts
	 *
	 * @param id
	 */
	void deleteById(Long id);

	/**
	 * Updates quantity of SalesOrderItem.<br>
	 * And then updates parent SalesOrder amounts and discounts
	 *
	 * @param id
	 * @param quantity
	 */
	void updateQuantityById(Long id, Integer quantity);

}