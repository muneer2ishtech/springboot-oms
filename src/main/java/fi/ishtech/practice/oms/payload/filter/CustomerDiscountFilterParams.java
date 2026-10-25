package fi.ishtech.practice.oms.payload.filter;

import java.io.Serial;
import java.math.BigDecimal;

import fi.ishtech.base.payload.filter.BaseStandardEntityFilterParams;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
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
public class CustomerDiscountFilterParams extends BaseStandardEntityFilterParams {

	@Serial
	private static final long serialVersionUID = 7680517489065442021L;

	private Long customerId;

	private Long productId;

	private DiscountTypeEnum[] discountType;

	private BigDecimal minDiscountPercent;
	private BigDecimal maxDiscountPercent;

	private Integer minBuyQuantity;
	private Integer maxBuyQuantity;

	private Integer minPayQuantity;
	private Integer maxPayQuantity;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private UserProfileFilterParams customer;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private ProductFilterParams product;

}