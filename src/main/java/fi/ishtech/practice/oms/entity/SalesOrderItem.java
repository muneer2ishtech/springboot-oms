package fi.ishtech.practice.oms.entity;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.ishtech.base.entity.BaseStandardEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Entity
@Table(name = "t_sales_order_item",
		uniqueConstraints = { @UniqueConstraint(name = "uk_sales_order_item_sales_order_id_product_id",
				columnNames = { "sales_order_id", "product_id" }) })
@DynamicInsert
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SalesOrderItem extends BaseStandardEntity {

	@Serial
	private static final long serialVersionUID = 3971866180515025837L;

	@Column(name = "sales_order_id", nullable = false, insertable = true, updatable = false)
	private Long salesOrderId;

	@Column(name = "product_id", nullable = false, insertable = true, updatable = false)
	private Long productId;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false, insertable = true, updatable = false)
	private BigDecimal unitPrice;

	@Column(nullable = false)
	private BigDecimal origLineAmount;

	@Column
	private BigDecimal discountPercent;

	@Column
	private BigDecimal discountAmount;

	@Column(nullable = false)
	private BigDecimal lineAmount;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "sales_order_id", referencedColumnName = "id", nullable = true, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "fk_sales_order_item_sales_order"))
	private SalesOrder salesOrder;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "fk_sales_order_item_product"))
	private Product product;

}