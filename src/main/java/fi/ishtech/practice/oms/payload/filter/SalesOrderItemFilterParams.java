package fi.ishtech.practice.oms.payload.filter;

import java.io.Serial;
import java.math.BigDecimal;

import fi.ishtech.base.payload.filter.BaseStandardEntityFilterParams;

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
public class SalesOrderItemFilterParams extends BaseStandardEntityFilterParams {

	@Serial
	private static final long serialVersionUID = -1339008332415060992L;

	private Long salesOrderId;

	private Long productId;

	private Integer minQuantity;
	private Integer maxQuantity;

	private BigDecimal minUnitPrice;
	private BigDecimal maxUnitPrice;

	private BigDecimal minOrigLineAmount;
	private BigDecimal maxOrigLineAmount;

	private BigDecimal minDiscountPercent;
	private BigDecimal maxDiscountPercent;

	private BigDecimal minDiscountAmount;
	private BigDecimal maxDiscountAmount;

	private BigDecimal minLineAmount;
	private BigDecimal maxLineAmount;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private SalesOrderFilterParams salesOrder;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private ProductFilterParams product;

}