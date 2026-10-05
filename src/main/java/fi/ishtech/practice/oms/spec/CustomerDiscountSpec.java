package fi.ishtech.practice.oms.spec;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import fi.ishtech.base.spec.BaseStandardSpec;
import fi.ishtech.practice.oms.entity.CustomerDiscount;
import fi.ishtech.practice.oms.entity.CustomerDiscount_;
import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.entity.Product_;
import fi.ishtech.practice.oms.payload.filter.CustomerDiscountFilterParams;
import fi.ishtech.practice.oms.payload.filter.ProductFilterParams;
import fi.ishtech.springboot.jwtauth.entity.UserProfile;
import fi.ishtech.springboot.jwtauth.entity.UserProfile_;
import fi.ishtech.springboot.jwtauth.payload.params.UserProfileFilterParams;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class CustomerDiscountSpec extends BaseStandardSpec<CustomerDiscount, CustomerDiscountFilterParams> {

	@Serial
	private static final long serialVersionUID = -2693167671940681473L;

	public CustomerDiscountSpec(CustomerDiscountFilterParams params) {
		super(params);
	}

	@Override
	protected List<Predicate> toPredicateList(Root<CustomerDiscount> root, CriteriaBuilder cb) {
		List<Predicate> predicates = super.toPredicateList(root, cb);

		addPredicateLike(predicates, root, cb, getParams().getCustomerId(), CustomerDiscount_.CUSTOMER_ID);
		addPredicateLike(predicates, root, cb, getParams().getProductId(), CustomerDiscount_.PRODUCT_ID);

		addPredicateIn(predicates, root, cb, getParams().getDiscountType(), CustomerDiscount_.DISCOUNT_TYPE);

		addPredicateGE(predicates, root, cb, getParams().getMinDiscountPercent(), CustomerDiscount_.DISCOUNT_PERCENT);
		addPredicateLE(predicates, root, cb, getParams().getMaxDiscountPercent(), CustomerDiscount_.DISCOUNT_PERCENT);

		addPredicateGE(predicates, root, cb, getParams().getMinBuyQuantity(), CustomerDiscount_.BUY_QUANTITY);
		addPredicateLE(predicates, root, cb, getParams().getMaxBuyQuantity(), CustomerDiscount_.BUY_QUANTITY);

		addPredicateGE(predicates, root, cb, getParams().getMinPayQuantity(), CustomerDiscount_.PAY_QUANTITY);
		addPredicateLE(predicates, root, cb, getParams().getMaxPayQuantity(), CustomerDiscount_.PAY_QUANTITY);

		addProductPredicatesToPredicateList(root, cb, predicates);
		addCustomerPredicatesToPredicateList(root, cb, predicates);

		return predicates;
	}

	private void addProductPredicatesToPredicateList(Root<CustomerDiscount> root, CriteriaBuilder cb,
			List<Predicate> predicates) {
		if (getParams().getProduct() == null) {
			return;
		}

		ProductFilterParams productFilterParams = getParams().getProduct();
		Join<CustomerDiscount, Product> joinProduct = getJoin(null, root, CustomerDiscount_.PRODUCT);

		addPredicateLike(predicates, joinProduct, cb, productFilterParams.getName(), Product_.NAME);
	}

	private void addCustomerPredicatesToPredicateList(Root<CustomerDiscount> root, CriteriaBuilder cb,
			List<Predicate> predicates) {
		if (getParams().getCustomer() == null) {
			return;
		}

		UserProfileFilterParams customerFilterParams = getParams().getCustomer();
		Join<CustomerDiscount, UserProfile> joinCustomer = getJoin(null, root, CustomerDiscount_.CUSTOMER);

		addPredicateLike(predicates, joinCustomer, cb, customerFilterParams.getFirstName(), UserProfile_.FIRST_NAME);
	}

}