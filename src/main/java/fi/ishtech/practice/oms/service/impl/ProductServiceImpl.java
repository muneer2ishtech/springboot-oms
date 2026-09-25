package fi.ishtech.practice.oms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.entity.ProductDocument;
import fi.ishtech.practice.oms.entity.Product_;
import fi.ishtech.practice.oms.mapper.ProductMapper;
import fi.ishtech.practice.oms.payload.ProductVo;
import fi.ishtech.practice.oms.payload.filter.ProductFilterParams;
import fi.ishtech.practice.oms.repo.ProductDocumentRepo;
import fi.ishtech.practice.oms.repo.ProductRepo;
import fi.ishtech.practice.oms.service.ProductService;

import lombok.extern.slf4j.Slf4j;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;

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

	@Autowired
	private ProductDocumentRepo productDocumentRepo;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

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

		// Sync to Elasticsearch
		ProductDocument document = productMapper.toDocument(product);
		productDocumentRepo.save(document);

		return product;
	}

	@Override
	public ProductVo updateAndMapToVo(@Valid ProductVo productVo) {
		Assert.notNull(productVo.getId(), "Product id cannot be null");

		Product product = this.findOneByIdOrElseThrow(productVo.getId());

		product = productMapper.toExistingEntity(productVo, product);
		product = productRepo.saveAndFlush(product);

		// Sync to Elasticsearch
		ProductDocument document = productMapper.toDocument(product);
		productDocumentRepo.save(document);

		refresh(product);

		return productMapper.toSemiDetailVo(product);
	}

	@Override
	public void deactivateById(Long id) {
		Product product = this.findOneByIdOrElseThrow(id);

		product.setActive(false);

		product = productRepo.saveAndFlush(product);

		// Sync to Elasticsearch (soft delete by setting active=false)
		ProductDocument document = productMapper.toDocument(product);
		productDocumentRepo.save(document);

		log.info("Soft Deleted Product({})", id);
	}

	@Override
	public Page<ProductVo> searchFromElasticsearch(ProductFilterParams params, Pageable pageable) {
		// Build Elasticsearch query based on filter params
		Query.Builder queryBuilder = new Query.Builder();

		if (StringUtils.hasText(params.getName())) {
			queryBuilder.match(m -> m.field(Product_.NAME).query(params.getName()));
		}

		if (params.getMinUnitPrice() != null) {
			queryBuilder.range(RangeQuery
					.of(r -> r.number(f -> f.field(Product_.UNIT_PRICE).gte(params.getMinUnitPrice().doubleValue()))));
		}

		if (params.getMaxUnitPrice() != null) {
			queryBuilder.range(RangeQuery
					.of(r -> r.number(f -> f.field(Product_.UNIT_PRICE).lte(params.getMaxUnitPrice().doubleValue()))));
		}

		// Only active products
		if (params.getIsActive() != null) {
			TermQuery activeQuery = TermQuery.of(t -> t.field(Product_.IS_ACTIVE).value(params.getIsActive()));
			queryBuilder.term(activeQuery);
		}

		if (StringUtils.hasText(params.getDescription())) {
			queryBuilder.match(m -> m.field(Product_.DESCRIPTION).query(params.getDescription()));
		}

		NativeQuery nativeQuery = NativeQuery.builder().withQuery(queryBuilder.build()).withPageable(pageable).build();

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		List<ProductVo> productVos = searchHits.getSearchHits().stream().map(SearchHit::getContent)
				.map(productMapper::toBriefVo).collect(Collectors.toList());

		return new PageImpl<>(productVos, pageable, searchHits.getTotalHits());
	}

}