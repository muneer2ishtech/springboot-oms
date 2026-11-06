package fi.ishtech.practice.oms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
import fi.ishtech.practice.oms.repo.SalesOrderItemRepo;
import fi.ishtech.practice.oms.repo.SalesOrderRepo;
import fi.ishtech.practice.oms.service.CustomerDiscountService;

/**
 *
 * @author Muneer Ahmed Syed
 */
@ExtendWith(MockitoExtension.class)
class SalesOrderServiceImplTests {

	private static final long CUSTOMER_ID = 11L;
	private static final long PRODUCT_ID = 41L;

	@Mock
	private SalesOrderRepo salesOrderRepo;

	@Mock
	private SalesOrderItemRepo salesOrderItemRepo;

	@Mock
	private CustomerDiscountService customerDiscountService;

	@InjectMocks
	private SalesOrderServiceImpl salesOrderService;

	@Test
	void buyXPayYDiscountWithoutPayQuantityThrowsInvalidCustomerDiscount() {
		var salesOrder = newSalesOrderWithItem(3, "50.00");
		mockBuyXPayYDiscount(3, null);

		assertThatThrownBy(() -> salesOrderService.updateSalesOrderAmountsAndSave(salesOrder))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid Customer Discount");

		verify(salesOrderRepo, never()).saveAndFlush(any());
	}

	@Test
	void buyXPayYDiscountGivesFreeUnits() {
		var salesOrder = newSalesOrderWithItem(3, "50.00");
		mockBuyXPayYDiscount(3, 2);
		when(salesOrderItemRepo.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		salesOrderService.updateSalesOrderAmountsAndSave(salesOrder);

		var salesOrderItem = salesOrder.getSalesOrderItems().iterator().next();
		assertThat(salesOrderItem.getOrigLineAmount()).isEqualByComparingTo("150.00");
		assertThat(salesOrderItem.getDiscountAmount()).isEqualByComparingTo("50.00");
		assertThat(salesOrderItem.getLineAmount()).isEqualByComparingTo("100.00");
		assertThat(salesOrder.getOrigTotalAmount()).isEqualByComparingTo("150.00");
		assertThat(salesOrder.getTotalAmount()).isEqualByComparingTo("100.00");
		assertThat(salesOrder.getNetAmount()).isEqualByComparingTo("100.00");
	}

	private SalesOrder newSalesOrderWithItem(int quantity, String unitPrice) {
		var salesOrderItem = new SalesOrderItem();
		salesOrderItem.setActive(true);
		salesOrderItem.setProductId(PRODUCT_ID);
		salesOrderItem.setQuantity(quantity);
		salesOrderItem.setUnitPrice(new BigDecimal(unitPrice));

		var salesOrder = new SalesOrder();
		salesOrder.setCustomerId(CUSTOMER_ID);
		salesOrder.setSalesOrderItems(Set.of(salesOrderItem));

		return salesOrder;
	}

	private void mockBuyXPayYDiscount(Integer buyQuantity, Integer payQuantity) {
		var customerDiscount = new CustomerDiscount();
		customerDiscount.setActive(true);
		customerDiscount.setCustomerId(CUSTOMER_ID);
		customerDiscount.setProductId(PRODUCT_ID);
		customerDiscount.setDiscountType(DiscountTypeEnum.PRODUCT_BUY_X_PAY_Y);
		customerDiscount.setBuyQuantity(buyQuantity);
		customerDiscount.setPayQuantity(payQuantity);

		when(customerDiscountService.findActiveOneByDiscountTypeProduct(CUSTOMER_ID, PRODUCT_ID,
				DiscountTypeEnum.PRODUCT_PERCENT)).thenReturn(null);
		when(customerDiscountService.findActiveOneByDiscountTypeProduct(CUSTOMER_ID, PRODUCT_ID,
				DiscountTypeEnum.PRODUCT_BUY_X_PAY_Y)).thenReturn(customerDiscount);
	}

}
