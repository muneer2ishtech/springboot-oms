package fi.ishtech.practice.oms.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.mapper.ProductMapper;
import fi.ishtech.practice.oms.payload.ProductVo;
import fi.ishtech.practice.oms.repo.ProductRepo;
import fi.ishtech.practice.oms.service.ProductService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Muneer Ahmed Syed
 */
@Service
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private ProductMapper productMapper;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public ProductRepo getRepo() {
		return productRepo;
	}

	@Override
	public ProductMapper getMapper() {
		return productMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Product create(ProductVo productVo) {
		Product product = productMapper.toNewEntity(productVo);

		product = productRepo.saveAndFlush(product);
		log.info("New Product({}) created", product.getId());

		return product;
	}

	@Override
	public ProductVo updateAndMapToVo(@Valid ProductVo productVo) {
		Assert.notNull(productVo.getId(), "Product id cannot be null");

		Product product = this.findOneByIdOrElseThrow(productVo.getId());

		product = productMapper.toExistingEntity(productVo, product);
		product = productRepo.saveAndFlush(product);

		refresh(product);

		return productMapper.toSemiDetailVo(product);
	}

	@Override
	public void deactivateById(Long id) {
		Product product = this.findOneByIdOrElseThrow(id);

		product.setActive(false);

		product = productRepo.saveAndFlush(product);

		log.info("Soft Deleted Product({})", id);
	}

}