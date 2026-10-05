package fi.ishtech.practice.oms.payload;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import fi.ishtech.base.vo.BaseStandardEntityVo;

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
public class SalesOrderItemVo extends BaseStandardEntityVo {

	@Serial
	private static final long serialVersionUID = 4376030409419814860L;

	private Long salesOrderId;

	@NotNull
	private Long productId;

	@NotNull
	@Positive
	private Integer quantity;

	private BigDecimal unitPrice;

	private BigDecimal origLineAmount;

	private BigDecimal discountPercent;

	private BigDecimal discountAmount;

	private BigDecimal lineAmount;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private SalesOrderVo salesOrder;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private ProductVo product;

}