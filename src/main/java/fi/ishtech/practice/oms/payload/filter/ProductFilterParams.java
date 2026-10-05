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
public class ProductFilterParams extends BaseStandardEntityFilterParams {

	@Serial
	private static final long serialVersionUID = -7408110904655548548L;

	private String name;

	private BigDecimal minUnitPrice;
	private BigDecimal maxUnitPrice;

}