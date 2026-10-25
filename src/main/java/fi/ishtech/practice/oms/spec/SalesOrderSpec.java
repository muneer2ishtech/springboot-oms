package fi.ishtech.practice.oms.spec;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import fi.ishtech.base.spec.BaseStandardSpec;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrder_;
import fi.ishtech.practice.oms.payload.filter.SalesOrderFilterParams;
import fi.ishtech.springboot.jwtauth.entity.UserProfile;
import fi.ishtech.springboot.jwtauth.entity.UserProfile_;
import fi.ishtech.springboot.jwtauth.payload.params.UserProfileFilterParams;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class SalesOrderSpec extends BaseStandardSpec<SalesOrder, SalesOrderFilterParams> {

	@Serial
	private static final long serialVersionUID = -4027068875044076203L;

	public SalesOrderSpec(SalesOrderFilterParams params) {
		super(params);
	}

	@Override
	protected List<Predicate> toPredicateList(Root<SalesOrder> root, CriteriaBuilder cb) {
		List<Predicate> predicates = super.toPredicateList(root, cb);

		addPredicateLike(predicates, root, cb, getParams().getCustomerId(), SalesOrder_.CUSTOMER_ID);

		addPredicateGE(predicates, root, cb, getParams().getMinTotalAmount(), SalesOrder_.TOTAL_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxTotalAmount(), SalesOrder_.TOTAL_AMOUNT);

		addPredicateGE(predicates, root, cb, getParams().getMinDiscountPercent(), SalesOrder_.DISCOUNT_PERCENT);
		addPredicateLE(predicates, root, cb, getParams().getMaxDiscountPercent(), SalesOrder_.DISCOUNT_PERCENT);

		addPredicateGE(predicates, root, cb, getParams().getMinDiscountAmount(), SalesOrder_.DISCOUNT_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxDiscountAmount(), SalesOrder_.DISCOUNT_AMOUNT);

		addPredicateGE(predicates, root, cb, getParams().getMinNetAmount(), SalesOrder_.NET_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxNetAmount(), SalesOrder_.NET_AMOUNT);

		addCustomerPredicatesToPredicateList(root, cb, predicates);

		return predicates;
	}

	private void addCustomerPredicatesToPredicateList(Root<SalesOrder> root, CriteriaBuilder cb,
			List<Predicate> predicates) {
		if (getParams().getCustomer() == null) {
			return;
		}

		UserProfileFilterParams customerFilterParams = getParams().getCustomer();
		Join<SalesOrder, UserProfile> joinCustomer = getJoin(null, root, SalesOrder_.CUSTOMER);

		addPredicateLike(predicates, joinCustomer, cb, customerFilterParams.getFirstName(), UserProfile_.FIRST_NAME);
	}

}