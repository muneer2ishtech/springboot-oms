package fi.ishtech.practice.oms.spec;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import fi.ishtech.base.spec.BaseStandardSpec;
import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.entity.Product_;
import fi.ishtech.practice.oms.payload.filter.ProductFilterParams;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class ProductSpec extends BaseStandardSpec<Product, ProductFilterParams> {

	@Serial
	private static final long serialVersionUID = -5813949413189524814L;

	public ProductSpec(ProductFilterParams params) {
		super(params);
	}

	@Override
	protected List<Predicate> toPredicateList(Root<Product> root, CriteriaBuilder cb) {
		List<Predicate> predicates = super.toPredicateList(root, cb);

		addPredicateLike(predicates, root, cb, getParams().getName(), Product_.NAME);
		addPredicateGE(predicates, root, cb, getParams().getMinUnitPrice(), Product_.UNIT_PRICE);
		addPredicateLE(predicates, root, cb, getParams().getMaxUnitPrice(), Product_.UNIT_PRICE);

		return predicates;
	}

}