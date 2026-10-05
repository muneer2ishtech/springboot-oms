package fi.ishtech.practice.oms.payload.filter;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;

import fi.ishtech.base.payload.filter.BaseStandardEntityFilterParams;
import fi.ishtech.springboot.jwtauth.payload.params.UserProfileFilterParams;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SalesOrderFilterParams extends BaseStandardEntityFilterParams {

	@Serial
	private static final long serialVersionUID = -8560701313589032650L;

	@Positive
	private Long customerId;

	private BigDecimal minTotalAmount;
	private BigDecimal maxTotalAmount;

	private BigDecimal minDiscountPercent;
	private BigDecimal maxDiscountPercent;

	private BigDecimal minDiscountAmount;
	private BigDecimal maxDiscountAmount;

	private BigDecimal minNetAmount;
	private BigDecimal maxNetAmount;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private UserProfileFilterParams customer;

}