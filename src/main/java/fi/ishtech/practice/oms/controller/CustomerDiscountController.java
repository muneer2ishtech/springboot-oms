package fi.ishtech.practice.oms.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.ishtech.practice.oms.payload.CustomerDiscountVo;
import fi.ishtech.practice.oms.payload.filter.CustomerDiscountFilterParams;
import fi.ishtech.practice.oms.service.CustomerDiscountService;
import fi.ishtech.practice.oms.spec.CustomerDiscountSpec;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for CustomerDiscount
 *
 * @author Muneer Ahmed Syed
 */
@RestController
@Slf4j
public class CustomerDiscountController {

	@Autowired
	private CustomerDiscountService customerDiscountService;

	/**
	 * Gets public info of companies found by filter params
	 *
	 * @param params   - {@link CustomerDiscountFilterParams}
	 * @param pageable - {@link Pageable}
	 * @return {@link ResponseEntity}&lt;{@link Page}&lt;{@link CustomerDiscountVo}&gt;&gt;
	 */
	@GetMapping(path = "/api/v1/customer-discounts", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<CustomerDiscountVo>> searchCustomerDiscounts(@Valid CustomerDiscountFilterParams params,
			Pageable pageable) {
		log.trace("CustomerDiscountFilterParams:{}", params);

		var customerDiscountSpec = new CustomerDiscountSpec(params);

		var result = customerDiscountService.findAllAndMapToVo(customerDiscountSpec, pageable);

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param customerDiscountId
	 * @return {@link ResponseEntity}&lt;{@link CustomerDiscountVo}&gt;
	 */
	@GetMapping(path = "/api/v1/customer-discounts/{customerDiscountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerDiscountVo> fetchCustomerDiscount(
			@PathVariable("customerDiscountId") Long customerDiscountId) {
		log.info("customerDiscountId:{}", customerDiscountId);

		var result = customerDiscountService.findOneByIdAndMapToVoOrElseThrow(customerDiscountId);

		return ResponseEntity.ok(result);
	}

	/**
	 * Creates new CustomerDiscount
	 *
	 * @param customerDiscountVo - CustomerDiscountVo
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PostMapping(path = "/api/v1/customer-discounts",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	public ResponseEntity<Long> createCustomerDiscount(@Valid @RequestBody CustomerDiscountVo customerDiscountVo) {
		log.trace("customerDiscountVo:{}", customerDiscountVo);

		var result = customerDiscountService.create(customerDiscountVo);

		// @formatter:off
		URI uri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/api/v1/customer-discounts/{customerDiscountId}")
					.buildAndExpand(result.getId())
					.toUri();
		// @formatter:on

		return ResponseEntity.created(uri).body(result.getId());
	}

	/**
	 * Creates new CustomerDiscount
	 *
	 * @param customerDiscountVo - CustomerDiscountVo
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PutMapping(path = "/api/v1/customer-discounts",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	public ResponseEntity<CustomerDiscountVo> updateCustomerDiscount(
			@Valid @RequestBody CustomerDiscountVo customerDiscountVo) {
		log.trace("customerDiscountVo:{}", customerDiscountVo);

		var result = customerDiscountService.updateAndMapToVo(customerDiscountVo);

		return ResponseEntity.ok(result);
	}

}