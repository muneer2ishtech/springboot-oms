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
import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.payload.ProductVo;

/**
 * Mapper for {@link ProductVo} to {@link Product} entity and vice-versa<br>
 *
 * @author Muneer Ahmed Syed
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseStandardMapper {

	/**
	 *
	 * @param entity {@link Product}
	 * @return {@link ProductVo}
	 */
	@BriefMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBaseStandardVo")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "unitPrice", target = "unitPrice")
	ProductVo toBriefVo(Product entity);

	/**
	 *
	 * @param entity {@link Product}
	 * @return {@link ProductVo}
	 */
	@SemiDetailMapping
	@BeanMapping(ignoreByDefault = true)
	@InheritConfiguration(name = "toBriefVo")
	ProductVo toSemiDetailVo(Product entity);

	/**
	 *
	 * @param vo     {@link ProductVo}
	 * @param entity {@link Product}
	 * @return {@link Product} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	Product toExistingEntity(ProductVo vo, @MappingTarget Product entity);

	/**
	 *
	 * @param vo {@link ProductVo}
	 * @return new {@link Product} entity
	 */
	@BeanMapping(ignoreByDefault = true)
	@InheritInverseConfiguration(name = "toBriefVo")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", constant = "true")
	Product toNewEntity(ProductVo vo);

}