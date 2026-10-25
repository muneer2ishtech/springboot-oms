package fi.ishtech.practice.oms.entity;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "t_product",
		uniqueConstraints = { @UniqueConstraint(name = "uk_product_name", columnNames = { "name" }) })
@DynamicInsert
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Product extends BaseStandardEntity {

	@Serial
	private static final long serialVersionUID = 5200300290570821077L;

	@Column(nullable = false, insertable = true, updatable = false, unique = true)
	private String name;

	@Column(nullable = false, insertable = true, updatable = true)
	private BigDecimal unitPrice;

}