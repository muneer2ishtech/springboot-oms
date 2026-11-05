package fi.ishtech.practice.oms.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import fi.ishtech.base.annotations.mapstruct.BriefMapping;
import fi.ishtech.base.annotations.mapstruct.DetailMapping;
import fi.ishtech.base.annotations.mapstruct.SemiDetailMapping;
import fi.ishtech.base.mapper.BaseStandardMapper;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.payload.SalesOrderItemVo;
import fi.ishtech.practice.oms.payload.SalesOrderVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Mapper(componentModel = "spring", uses = ProductMapper.class)
public abstract class SalesOrderItemMapper implements BaseStandardMapper {

	/**
	 * {@link Lazy} breaks the bean cycle with {@link SalesOrderMapper}, which uses this mapper for sales order items
	 */
	@Lazy
	@Autowired
	protected SalesOrderMapper salesOrderMapper;

	/**
	 *
	 * @param entity {@link SalesOrderItem}
	 * @return {@link SalesOrderItemVo}
	 */
	@BriefMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBaseStandardVo")
	@Mapping(source = "salesOrderId", target = "salesOrderId")
	@Mapping(source = "productId", target = "productId")
	@Mapping(source = "quantity", target = "quantity")
	@Mapping(source = "unitPrice", target = "unitPrice")
	@Mapping(source = "origLineAmount", target = "origLineAmount")
	@Mapping(source = "discountPercent", target = "discountPercent")
	@Mapping(source = "discountAmount", target = "discountAmount")
	@Mapping(source = "lineAmount", target = "lineAmount")
	public abstract SalesOrderItemVo toBriefVo(SalesOrderItem entity);

	/**
	 *
	 * @param entity {@link SalesOrderItem}
	 * @return {@link SalesOrderItemVo}
	 */
	@SemiDetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBriefVo")
	@Mapping(source = "product", target = "product", qualifiedBy = BriefMapping.class)
	public abstract SalesOrderItemVo toSemiDetailVo(SalesOrderItem entity);

	/**
	 *
	 * @param entity {@link SalesOrderItem}
	 * @return {@link SalesOrderItemVo}
	 */
	@DetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toSemiDetailVo")
	@Mapping(source = "salesOrder", target = "salesOrder", qualifiedBy = SemiDetailMapping.class)
	public abstract SalesOrderItemVo toDetailVo(SalesOrderItem entity);

	/**
	 *
	 * @param vo     - {@link SalesOrderItemVo}
	 * @param entity - {@link SalesOrderItem}
	 * @return {@link SalesOrderItem} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	public abstract SalesOrderItem toExistingEntity(SalesOrderItemVo vo, @MappingTarget SalesOrderItem entity);

	/**
	 *
	 * @param vo - {@link SalesOrderItemVo}
	 * @return new {@link SalesOrderItem} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", constant = "true")
	@Mapping(target = "unitPrice", ignore = true)
	@Mapping(target = "origLineAmount", ignore = true)
	@Mapping(target = "discountPercent", ignore = true)
	@Mapping(target = "discountAmount", ignore = true)
	@Mapping(target = "lineAmount", ignore = true)
	public abstract SalesOrderItem toNewEntity(SalesOrderItemVo vo);

	/**
	 *
	 * @param vos
	 * @return new {@link Set}&lt;{@link SalesOrderItem}&gt; entities
	 */
	public abstract Set<SalesOrderItem> toNewEntity(Set<SalesOrderItemVo> vos);

	/**
	 * Maps the sales order of an item with {@link SalesOrderMapper#toSemiDetailVo(SalesOrder)}
	 *
	 * @param salesOrder {@link SalesOrder}
	 * @return {@link SalesOrderVo}
	 */
	@SemiDetailMapping
	protected SalesOrderVo toSalesOrderSemiDetailVo(SalesOrder salesOrder) {
		return salesOrderMapper.toSemiDetailVo(salesOrder);
	}

}