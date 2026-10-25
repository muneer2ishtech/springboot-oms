package fi.ishtech.practice.oms.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import fi.ishtech.base.annotations.mapstruct.BriefMapping;
import fi.ishtech.base.annotations.mapstruct.SemiDetailMapping;
import fi.ishtech.base.mapper.BaseStandardMapper;
import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.payload.CustomerDiscountVo;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Mapper(componentModel = "spring")
public interface CustomerDiscountMapper extends BaseStandardMapper {

	/**
	 *
	 * @param entity {@link CustomerDiscount}
	 * @return {@link CustomerDiscountVo}
	 */
	@BriefMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBaseStandardVo")
	@Mapping(source = "customerId", target = "customerId")
	@Mapping(source = "productId", target = "productId")
	@Mapping(source = "discountType", target = "discountType")
	@Mapping(source = "discountPercent", target = "discountPercent")
	@Mapping(source = "buyQuantity", target = "buyQuantity")
	@Mapping(source = "payQuantity", target = "payQuantity")
	CustomerDiscountVo toBriefVo(CustomerDiscount entity);

	/**
	 *
	 * @param entity {@link CustomerDiscount}
	 * @return {@link CustomerDiscountVo}
	 */
	@SemiDetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBriefVo")
	CustomerDiscountVo toSemiDetailVo(CustomerDiscount entity);

	/**
	 *
	 * @param vo     - {@link CustomerDiscountVo}
	 * @param entity - {@link CustomerDiscount}
	 * @return {@link CustomerDiscount} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	CustomerDiscount toExistingEntity(CustomerDiscountVo vo, @MappingTarget CustomerDiscount entity);

	/**
	 *
	 * @param vo - {@link CustomerDiscountVo}
	 * @return new {@link CustomerDiscount} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", constant = "true")
	CustomerDiscount toNewEntity(CustomerDiscountVo vo);

}