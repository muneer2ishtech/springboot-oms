package fi.ishtech.practice.oms.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Muneer Ahmed Syed
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

	@ExceptionHandler(UnsupportedOperationException.class)
	public ResponseEntity<String> handleUnsupportedOperationException(UnsupportedOperationException ex) {
		log.error("handle UnsupportedOperationException");
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
		log.error("handle NoSuchElementException");
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error("handle IllegalArgumentException");
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		log.error("handle DataIntegrityViolationException", ex);
		if (ex.getMessage() != null && ex.getMessage().contains("uk_sales_order_item_sales_order_id_product_id")) {
			return new ResponseEntity<>("Cannot add row of existing Product in SalesOrder. Instead edit quantity", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>("Data integrity violation", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}