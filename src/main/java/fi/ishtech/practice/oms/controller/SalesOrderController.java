package fi.ishtech.practice.oms.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.ishtech.practice.oms.payload.SalesOrderItemVo;
import fi.ishtech.practice.oms.payload.SalesOrderVo;
import fi.ishtech.practice.oms.payload.filter.SalesOrderFilterParams;
import fi.ishtech.practice.oms.service.SalesOrderItemService;
import fi.ishtech.practice.oms.service.SalesOrderService;
import fi.ishtech.practice.oms.spec.SalesOrderSpec;
import fi.ishtech.springboot.jwtauth.service.AuthInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controller for SalesOrder
 *
 * @author Muneer Ahmed Syed
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SalesOrderController {

	private final AuthInfoService authInfoService;
	private final SalesOrderService salesOrderService;
	private final SalesOrderItemService salesOrderItemService;

	/**
	 * Finds SalesOrder(s) by search filters and pagination
	 *
	 * @param params   - {@link SalesOrderFilterParams}
	 * @param pageable - {@link Pageable}
	 *
	 * @return {@link ResponseEntity}&lt;{@link Page}&lt;{@link SalesOrderVo}&gt;&gt;
	 */
	@GetMapping(path = "/api/v1/sales-orders", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<SalesOrderVo>> searchSalesOrders(@Valid SalesOrderFilterParams params,
			Pageable pageable) {
		log.trace("SalesOrderFilterParams:{}", params);

		if (!authInfoService.isAdmin()) {
			params.setCustomerId(authInfoService.getUserId());
		}

		var salesOrderSpec = new SalesOrderSpec(params);

		var result = salesOrderService.findAllAndMapToVo(salesOrderSpec, pageable);

		return ResponseEntity.ok(result);
	}

	/**
	 * Find SaleOrder by id
	 *
	 * @param salesOrderId
	 *
	 * @return {@link ResponseEntity}&lt;{@link SalesOrderVo}&gt;
	 */
	@GetMapping(path = "/api/v1/sales-orders/{salesOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PostAuthorize("returnObject.body == null || returnObject.body.customerId == authentication.principal.id")
	public ResponseEntity<SalesOrderVo> fetchSalesOrder(@PathVariable("salesOrderId") Long salesOrderId) {
		log.info("salesOrderId:{}", salesOrderId);

		var result = salesOrderService.findOneByIdAndMapToVoOrElseThrow(salesOrderId);

		return ResponseEntity.ok(result);
	}

	/**
	 * Creates new SalesOrder
	 *
	 * @param salesOrderVo - SalesOrderVo
	 *
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PostMapping(path = "/api/v1/sales-orders",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN') || #salesOrderVo.customerId == authentication.principal.id")
	public ResponseEntity<Long> createSalesOrder(@Valid @RequestBody SalesOrderVo salesOrderVo) {
		log.trace("salesOrderVo:{}", salesOrderVo);

		Assert.isNull(salesOrderVo.getId(), "id should be null");
		Assert.notEmpty(salesOrderVo.getSalesOrderItems(), "Should have at least one Sales Order Item");

		var result = salesOrderService.create(salesOrderVo);

		// @formatter:off
		URI uri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/api/v1/sales-orders/{salesOrderId}")
					.buildAndExpand(result.getId())
					.toUri();
		// @formatter:on

		return ResponseEntity.created(uri).body(result.getId());
	}

	/**
	 * Creates new SalesOrderItem
	 *
	 * @param salesOrderItemVo - SalesOrderItemVo
	 *
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PostMapping(path = "/api/v1/sales-orders/sales-order-items",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	public ResponseEntity<Long> createSalesOrderItem(@Valid @RequestBody SalesOrderItemVo salesOrderItemVo) {
		log.trace("salesOrderItemVo:{}", salesOrderItemVo);

		Assert.isNull(salesOrderItemVo.getId(), "id should be null");
		Assert.notNull(salesOrderItemVo.getSalesOrderId(), "salesOrderId cannot be null");

		var salesOderId = salesOrderItemVo.getSalesOrderId();
		var salesOrder = salesOrderService.findOneByIdOrElseThrow(salesOderId);
		// TODO: this throws 404, it is better to throw 400

		if (authInfoService.isAdmin()) {
			// ok
		} else {
			var loggedInUserId = authInfoService.getUserId();
			if (!salesOrder.getCustomerId().equals(loggedInUserId)) {
				log.error("User({}) cannot create SalesOrderItem for others", loggedInUserId);
				throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Cannot create SalesOrderItem for others");
			} else {
				// ok
			}
		}

		var result = salesOrderItemService.create(salesOrderItemVo);

		// @formatter:off
		URI uri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/api/v1/sales-orders/{salesOrderId}")
					.buildAndExpand(salesOderId)
					.toUri();
		// @formatter:on

		return ResponseEntity.status(HttpStatus.OK).location(uri).body(result.getId());
	}

	/**
	 * Delete existing SalesOrderItem and updates parent SalesOrder amounts and discounts
	 *
	 * @param salesOrderItemId
	 * @param quantity
	 *
	 * @return {@link ResponseEntity}&lt;{@link Void}&gt;
	 */
	// @formatter:off
	@Operation(summary = "Delete existing SalesOrderItem and Updates parent SalesOrder amounts and discounts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "410", description = "GONE",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Not Found",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ErrorResponse.class)))
	})
	// @formatter:on
	@DeleteMapping(path = "/api/v1/sales-orders/sales-order-items/{salesOrderItemId}")
	public ResponseEntity<Void> deleteSalesOrderItemById(@PathVariable Long salesOrderItemId) {
		log.debug("Deleting SalesOrderItem({})", salesOrderItemId);

		salesOrderItemService.deleteById(salesOrderItemId);

		return new ResponseEntity<Void>(HttpStatus.GONE);
	}

	/**
	 * Update quantity in existing SalesOrderItem and its amounts updates parent SalesOrder amounts and discounts
	 *
	 * @param salesOrderItemId
	 * @param quantity
	 *
	 * @return {@link ResponseEntity}&lt;{@link Void}&gt;
	 */
	@PatchMapping(path = "/api/v1/sales-orders/sales-order-items/{salesOrderItemId}")
	public ResponseEntity<Void> updateSalesOrderItemQuantityById(@PathVariable Long salesOrderItemId,
			@RequestParam Integer quantity) {
		log.debug("Updating SalesOrderitem({}) quantity to {}", salesOrderItemId, quantity);

		salesOrderItemService.updateQuantityById(salesOrderItemId, quantity);

		return ResponseEntity.ok().build();
	}

}