package fi.ishtech.practice.oms.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fi.ishtech.springboot.jwtauth.repo.UserProfileRepo;

/**
 *
 * @author Muneer Ahmed Syed
 */
@SpringBootTest
class CustomerDiscountControllerTests {

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private UserProfileRepo userProfileRepo;

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void createWithUnknownCustomerIdReturns400() throws Exception {
		var mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		// @formatter:off
		String body = mockMvc.perform(post("/api/v1/customer-discounts")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"customerId\": 999, \"discountType\": \"SALES_ORDER_PERCENT\", \"discountPercent\": 0.02}"))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		// @formatter:on

		assertThat(body).isEqualTo("Customer(999) does not exist");
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void createWithUnknownProductIdReturns400() throws Exception {
		var mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		when(userProfileRepo.existsById(999L)).thenReturn(true);

		// @formatter:off
		String body = mockMvc.perform(post("/api/v1/customer-discounts")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"customerId\": 999, \"productId\": 999, \"discountType\": \"PRODUCT_PERCENT\", \"discountPercent\": 0.02}"))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		// @formatter:on

		assertThat(body).isEqualTo("Product(999) does not exist");
	}

}
