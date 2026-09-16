package fi.ishtech.practice.oms.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.ishtech.practice.oms.payload.ProductVo;
import fi.ishtech.practice.oms.payload.filter.ProductFilterParams;
import fi.ishtech.practice.oms.service.ProductService;
import fi.ishtech.practice.oms.spec.ProductSpec;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controller for Product
 *
 * @author Muneer Ahmed Syed
 */
@RestController
@Slf4j
public class ProductController {

	@Autowired
	private ProductService productService;

	/**
	 * Finds Product(s) by search filters and pagination
	 *
	 * @param params   - {@link ProductFilterParams}
	 * @param pageable - {@link Pageable}
	 *
	 * @return {@link ResponseEntity}&lt;{@link Page}&lt;{@link ProductVo}&gt;&gt;
	 */
	@GetMapping(path = "/api/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProductVo>> searchProducts(@Valid ProductFilterParams params, Pageable pageable) {
		log.trace("ProductFilterParams:{}", params);

		var productSpec = new ProductSpec(params);

		var result = productService.findAllAndMapToVo(productSpec, pageable);

		return ResponseEntity.ok(result);
	}

	/**
	 * Find Product by id
	 *
	 * @param productId
	 *
	 * @return {@link ResponseEntity}&lt;{@link ProductVo}&gt;
	 */
	@GetMapping(path = "/api/v1/products/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductVo> fetchProduct(@PathVariable("productId") Long productId) {
		log.info("productId:{}", productId);

		var result = productService.findOneByIdAndMapToVoOrElseThrow(productId);

		return ResponseEntity.ok(result);
	}

	/**
	 * Creates new Product
	 *
	 * @param productVo - ProductVo
	 *
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PostMapping(path = "/api/v1/products",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	public ResponseEntity<Long> createProduct(@Valid @RequestBody ProductVo productVo) {
		log.trace("productVo:{}", productVo);

		var result = productService.create(productVo);

		// @formatter:off
		URI uri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/api/v1/products/{productId}")
					.buildAndExpand(result.getId())
					.toUri();
		// @formatter:on

		return ResponseEntity.created(uri).body(result.getId());
	}

	/**
	 * Creates new Product
	 *
	 * @param productVo - ProductVo
	 *
	 * @return {@link ResponseEntity}&lt;{@link Long}&gt;
	 */
	// @formatter:off
	@PutMapping(path = "/api/v1/products",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	// @formatter:on
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductVo> updateProduct(@Valid @RequestBody ProductVo productVo) {
		log.trace("productVo:{}", productVo);

		var result = productService.updateAndMapToVo(productVo);

		return ResponseEntity.ok(result);
	}

	/**
	 * Delete Product by id
	 *
	 * @param productVo - ProductVo
	 *
	 * @return {@link ResponseEntity}&lt;{@link Void}&gt;
	 */
	// @formatter:off
	@Operation(summary = "Delete existing Produt")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "410", description = "GONE",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Not Found",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ErrorResponse.class)))
	})
	// @formatter:on
	@DeleteMapping(path = "/api/v1/products/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		log.debug("Deleting Product({})", id);

		productService.deactivateById(id);

		return new ResponseEntity<Void>(HttpStatus.GONE);
	}

}