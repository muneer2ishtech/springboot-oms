package fi.ishtech.practice.oms.entity;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.ishtech.base.entity.BaseStandardEntity;
import fi.ishtech.practice.oms.enums.DiscountTypeEnum;
import fi.ishtech.springboot.jwtauth.entity.UserProfile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Entity
@Table(name = "t_customer_discount",
		uniqueConstraints = { @UniqueConstraint(name = "uk_customer_discount_customer_id_discount_type_product_id",
				columnNames = { "customer_id", "discount_type", "product_id" }) })
@DynamicInsert
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomerDiscount extends BaseStandardEntity {

	@Serial
	private static final long serialVersionUID = -4957579670535741482L;

	@Column(name = "customer_id", nullable = false, insertable = true, updatable = false)
	private Long customerId;

	@Column(name = "discount_type", nullable = false, insertable = true, updatable = false,
			columnDefinition = "enum('SALES_ORDER_PERCENT', 'PRODUCT_PERCENT', 'PRODUCT_BUY_X_PAY_Y')")
	@Enumerated(EnumType.STRING)
	private DiscountTypeEnum discountType;

	@Column(name = "product_id", nullable = true, insertable = true, updatable = false)
	private Long productId;

	@Column
	private BigDecimal discountPercent;

	@Column
	private Integer buyQuantity;

	@Column
	private Integer payQuantity;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = true, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "fk_customer_discount_customer_id_user_profile"))
	private UserProfile customer;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "fk_customer_discount_product"))
	private Product product;

}