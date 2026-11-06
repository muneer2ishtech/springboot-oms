package fi.ishtech.practice.oms.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import fi.ishtech.base.annotations.mapstruct.BriefMapping;
import fi.ishtech.base.annotations.mapstruct.DetailMapping;
import fi.ishtech.base.annotations.mapstruct.SemiDetailMapping;
import fi.ishtech.base.mapper.BaseStandardMapper;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.payload.SalesOrderVo;
import fi.ishtech.springboot.jwtauth.mapper.UserProfileMapper;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Mapper(componentModel = "spring", uses = { UserProfileMapper.class, SalesOrderItemMapper.class })
public interface SalesOrderMapper extends BaseStandardMapper {

	/**
	 *
	 * @param entity {@link SalesOrder}
	 * @return {@link SalesOrderVo}
	 */
	@BriefMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBaseStandardVo")
	@Mapping(source = "customerId", target = "customerId")
	@Mapping(source = "origTotalAmount", target = "origTotalAmount")
	@Mapping(source = "totalAmount", target = "totalAmount")
	@Mapping(source = "discountPercent", target = "discountPercent")
	@Mapping(source = "discountAmount", target = "discountAmount")
	@Mapping(source = "netAmount", target = "netAmount")
	SalesOrderVo toBriefVo(SalesOrder entity);

	/**
	 *
	 * @param entity {@link SalesOrder}
	 * @return {@link SalesOrderVo}
	 */
	@SemiDetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBriefVo")
	@Mapping(source = "customer", target = "customer", qualifiedBy = BriefMapping.class)
	SalesOrderVo toSemiDetailVo(SalesOrder entity);

	/**
	 *
	 * @param entity {@link SalesOrder}
	 * @return {@link SalesOrderVo}
	 */
	@DetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toSemiDetailVo")
	@Mapping(source = "salesOrderItems", target = "salesOrderItems", qualifiedBy = SemiDetailMapping.class)
	SalesOrderVo toDetailVo(SalesOrder entity);

	/**
	 *
	 * @param vo     {@link SalesOrderVo}
	 * @param entity {@link SalesOrder}
	 * @return {@link SalesOrder} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	SalesOrder toExistingEntity(SalesOrderVo vo, @MappingTarget SalesOrder entity);

	/**
	 *
	 * @param vo {@link SalesOrderVo}
	 * @return new {@link SalesOrder} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", constant = "true")
	@Mapping(target = "origTotalAmount", constant = "0")
	@Mapping(target = "totalAmount", constant = "0")
	@Mapping(target = "netAmount", constant = "0")
	SalesOrder toNewEntity(SalesOrderVo vo);

}