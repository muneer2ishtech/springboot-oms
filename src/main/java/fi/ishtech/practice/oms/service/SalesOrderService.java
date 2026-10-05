package fi.ishtech.practice.oms.service;

import fi.ishtech.base.service.BaseStandardService;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.payload.SalesOrderVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
public interface SalesOrderService extends BaseStandardService<SalesOrder, SalesOrderVo> {

	/**
	 * Creates a new SalesOrder
	 *
	 * @param salesOrderVo - {@link SalesOrderVo}
	 * @return {@link SalesOrder}
	 */
	SalesOrder create(SalesOrderVo salesOrderVo);

	/**
	 * Calculates amounts from child {@link fi.ishtech.practice.oms.entity.SalesOrderItem}s and discounts based on
	 * {@link fi.ishtech.practice.oms.entity.CustomerDiscount} and save.
	 *
	 * @param salesOrder - {@link SalesOrder}
	 */
	void updateSalesOrderAmountsAndSave(SalesOrder salesOrder);

	/**
	 * Calculates amounts from child {@link fi.ishtech.practice.oms.entity.SalesOrderItem}s and discounts based on
	 * {@link fi.ishtech.practice.oms.entity.CustomerDiscount} and save.
	 *
	 * @param salesOrderId
	 */
	void updateSalesOrderAmountsAndSave(Long salesOrderId);

}