package fi.ishtech.practice.oms.spec;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import fi.ishtech.base.spec.BaseStandardSpec;
import fi.ishtech.practice.oms.entity.Product;
import fi.ishtech.practice.oms.entity.Product_;
import fi.ishtech.practice.oms.entity.SalesOrder;
import fi.ishtech.practice.oms.entity.SalesOrderItem;
import fi.ishtech.practice.oms.entity.SalesOrderItem_;
import fi.ishtech.practice.oms.entity.SalesOrder_;
import fi.ishtech.practice.oms.payload.filter.ProductFilterParams;
import fi.ishtech.practice.oms.payload.filter.SalesOrderFilterParams;
import fi.ishtech.practice.oms.payload.filter.SalesOrderItemFilterParams;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class SalesOrderItemSpec extends BaseStandardSpec<SalesOrderItem, SalesOrderItemFilterParams> {

	@Serial
	private static final long serialVersionUID = -1373909197677613927L;

	public SalesOrderItemSpec(SalesOrderItemFilterParams params) {
		super(params);
	}

	@Override
	protected List<Predicate> toPredicateList(Root<SalesOrderItem> root, CriteriaBuilder cb) {
		List<Predicate> predicates = super.toPredicateList(root, cb);

		addPredicateLike(predicates, root, cb, getParams().getSalesOrderId(), SalesOrderItem_.SALES_ORDER_ID);
		addPredicateLike(predicates, root, cb, getParams().getProductId(), SalesOrderItem_.PRODUCT_ID);

		addPredicateGE(predicates, root, cb, getParams().getMinQuantity(), SalesOrderItem_.QUANTITY);
		addPredicateLE(predicates, root, cb, getParams().getMaxQuantity(), SalesOrderItem_.QUANTITY);

		addPredicateGE(predicates, root, cb, getParams().getMinUnitPrice(), SalesOrderItem_.UNIT_PRICE);
		addPredicateLE(predicates, root, cb, getParams().getMaxUnitPrice(), SalesOrderItem_.UNIT_PRICE);

		addPredicateGE(predicates, root, cb, getParams().getMinOrigLineAmount(), SalesOrderItem_.ORIG_LINE_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxOrigLineAmount(), SalesOrderItem_.ORIG_LINE_AMOUNT);

		addPredicateGE(predicates, root, cb, getParams().getMinDiscountPercent(), SalesOrderItem_.DISCOUNT_PERCENT);
		addPredicateLE(predicates, root, cb, getParams().getMaxDiscountPercent(), SalesOrderItem_.DISCOUNT_PERCENT);

		addPredicateGE(predicates, root, cb, getParams().getMinDiscountAmount(), SalesOrderItem_.DISCOUNT_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxDiscountAmount(), SalesOrderItem_.DISCOUNT_AMOUNT);

		addPredicateGE(predicates, root, cb, getParams().getMinLineAmount(), SalesOrderItem_.LINE_AMOUNT);
		addPredicateLE(predicates, root, cb, getParams().getMaxLineAmount(), SalesOrderItem_.LINE_AMOUNT);

		addProductPredicatesToPredicateList(root, cb, predicates);
		addSalesOrderPredicatesToPredicateList(root, cb, predicates);

		return predicates;
	}

	private void addProductPredicatesToPredicateList(Root<SalesOrderItem> root, CriteriaBuilder cb,
			List<Predicate> predicates) {
		if (getParams().getProduct() == null) {
			return;
		}

		ProductFilterParams productFilterParams = getParams().getProduct();
		Join<SalesOrderItem, Product> joinProduct = getJoin(null, root, SalesOrderItem_.PRODUCT);

		addPredicateLike(predicates, joinProduct, cb, productFilterParams.getName(), Product_.NAME);
	}

	private void addSalesOrderPredicatesToPredicateList(Root<SalesOrderItem> root, CriteriaBuilder cb,
			List<Predicate> predicates) {
		if (getParams().getSalesOrder() == null) {
			return;
		}

		SalesOrderFilterParams salesOrderFilterParams = getParams().getSalesOrder();
		Join<SalesOrderItem, SalesOrder> joinSalesOrder = getJoin(null, root, SalesOrderItem_.SALES_ORDER);

		addPredicateLike(predicates, joinSalesOrder, cb, salesOrderFilterParams.getCustomerId(),
				SalesOrder_.CUSTOMER_ID);
	}

}