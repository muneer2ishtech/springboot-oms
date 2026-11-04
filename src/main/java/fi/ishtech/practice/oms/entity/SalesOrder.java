package fi.ishtech.practice.oms.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.ishtech.base.entity.BaseStandardEntity;
import fi.ishtech.springboot.jwtauth.entity.UserProfile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Entity
@Table(name = "t_sales_order")
@DynamicInsert
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SalesOrder extends BaseStandardEntity {

	@Serial
	private static final long serialVersionUID = -6465037573617072828L;

	@Column(name = "customer_id", nullable = false, insertable = true, updatable = false)
	private Long customerId;

	@Column(nullable = false)
	private BigDecimal origTotalAmount;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@Column
	private BigDecimal discountPercent;

	@Column
	private BigDecimal discountAmount;

	@Column(nullable = false)
	private BigDecimal netAmount;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = true, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "fk_sales_order_customer_id_user_profile"))
	private UserProfile customer;

	@OneToMany(mappedBy = "salesOrder", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<SalesOrderItem> salesOrderItems;

}