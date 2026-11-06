package fi.ishtech.practice.oms.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fi.ishtech.practice.oms.service.ProductService;

/**
 *
 * @author Muneer Ahmed Syed
 */
@SpringBootTest
class ProductControllerTests {

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private ProductService productService;

	@Test
	@WithMockUser(authorities = "ROLE_USER")
	void deleteByUserReturns403() throws Exception {
		var mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		mockMvc.perform(delete("/api/v1/products/1")).andExpect(status().isForbidden());

		verify(productService, never()).deactivateById(anyLong());
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void deleteByAdminReturns410() throws Exception {
		var mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		mockMvc.perform(delete("/api/v1/products/1")).andExpect(status().isGone());

		verify(productService).deactivateById(1L);
	}

}
