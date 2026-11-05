package fi.ishtech.practice.oms.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import fi.ishtech.base.annotations.mapstruct.BriefMapping;
import fi.ishtech.base.annotations.mapstruct.SemiDetailMapping;
import fi.ishtech.base.mapper.BaseStandardMapper;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.payload.SalesOrderItemVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Mapper(componentModel = "spring")
public interface SalesOrderItemMapper extends BaseStandardMapper {

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
	SalesOrderItemVo toBriefVo(SalesOrderItem entity);

	/**
	 *
	 * @param entity {@link SalesOrderItem}
	 * @return {@link SalesOrderItemVo}
	 */
	@SemiDetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBriefVo")
	SalesOrderItemVo toSemiDetailVo(SalesOrderItem entity);

	/**
	 *
	 * @param vo     - {@link SalesOrderItemVo}
	 * @param entity - {@link SalesOrderItem}
	 * @return {@link SalesOrderItem} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	SalesOrderItem toExistingEntity(SalesOrderItemVo vo, @MappingTarget SalesOrderItem entity);

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
	SalesOrderItem toNewEntity(SalesOrderItemVo vo);

	/**
	 *
	 * @param vos
	 * @return new {@link Set}&lt;{@link SalesOrderItem}&gt; entities
	 */
	Set<SalesOrderItem> toNewEntity(Set<SalesOrderItemVo> vos);

}