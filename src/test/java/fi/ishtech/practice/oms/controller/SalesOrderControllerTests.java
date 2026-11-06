package fi.ishtech.practice.oms.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.service.SalesOrderItemService;
import fi.ishtech.practice.oms.service.SalesOrderService;
import fi.ishtech.springboot.jwtauth.service.AuthInfoService;

/**
 *
 * @author Muneer Ahmed Syed
 */
@SpringBootTest
class SalesOrderControllerTests {

	private static final long OWNER_ID = 11L;
	private static final long OTHER_USER_ID = 12L;
	private static final long SALES_ORDER_ID = 21L;
	private static final long SALES_ORDER_ITEM_ID = 31L;

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private AuthInfoService authInfoService;

	@MockitoBean
	private SalesOrderService salesOrderService;

	@MockitoBean
	private SalesOrderItemService salesOrderItemService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		var salesOrderItem = new SalesOrderItem();
		salesOrderItem.setId(SALES_ORDER_ITEM_ID);
		salesOrderItem.setSalesOrderId(SALES_ORDER_ID);
		when(salesOrderItemService.findOneByIdOrElseThrow(SALES_ORDER_ITEM_ID)).thenReturn(salesOrderItem);

		var salesOrder = new SalesOrder();
		salesOrder.setId(SALES_ORDER_ID);
		salesOrder.setCustomerId(OWNER_ID);
		when(salesOrderService.findOneByIdOrElseThrow(SALES_ORDER_ID)).thenReturn(salesOrder);
	}

	@Test
	@WithMockUser(authorities = "ROLE_USER")
	void deleteSalesOrderItemOfOtherCustomerReturns403() throws Exception {
		when(authInfoService.getUserId()).thenReturn(OTHER_USER_ID);

		mockMvc.perform(delete("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID))
				.andExpect(status().isForbidden());

		verify(salesOrderItemService, never()).deleteById(anyLong());
	}

	@Test
	@WithMockUser(authorities = "ROLE_USER")
	void deleteSalesOrderItemOfOwnSalesOrderReturns410() throws Exception {
		when(authInfoService.getUserId()).thenReturn(OWNER_ID);

		mockMvc.perform(delete("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID))
				.andExpect(status().isGone());

		verify(salesOrderItemService).deleteById(SALES_ORDER_ITEM_ID);
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void deleteSalesOrderItemByAdminReturns410() throws Exception {
		when(authInfoService.isAdmin()).thenReturn(true);
		when(authInfoService.getUserId()).thenReturn(OTHER_USER_ID);

		mockMvc.perform(delete("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID))
				.andExpect(status().isGone());

		verify(salesOrderItemService).deleteById(SALES_ORDER_ITEM_ID);
	}

	@Test
	@WithMockUser(authorities = "ROLE_USER")
	void updateSalesOrderItemQuantityOfOtherCustomerReturns403() throws Exception {
		when(authInfoService.getUserId()).thenReturn(OTHER_USER_ID);

		mockMvc.perform(patch("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID).param("quantity", "5"))
				.andExpect(status().isForbidden());

		verify(salesOrderItemService, never()).updateQuantityById(anyLong(), anyInt());
	}

	@Test
	@WithMockUser(authorities = "ROLE_USER")
	void updateSalesOrderItemQuantityOfOwnSalesOrderReturns200() throws Exception {
		when(authInfoService.getUserId()).thenReturn(OWNER_ID);

		mockMvc.perform(patch("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID).param("quantity", "5"))
				.andExpect(status().isOk());

		verify(salesOrderItemService).updateQuantityById(SALES_ORDER_ITEM_ID, 5);
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void updateSalesOrderItemQuantityByAdminReturns200() throws Exception {
		when(authInfoService.isAdmin()).thenReturn(true);
		when(authInfoService.getUserId()).thenReturn(OTHER_USER_ID);

		mockMvc.perform(patch("/api/v1/sales-orders/sales-order-items/" + SALES_ORDER_ITEM_ID).param("quantity", "5"))
				.andExpect(status().isOk());

		verify(salesOrderItemService).updateQuantityById(SALES_ORDER_ITEM_ID, 5);
	}

}
