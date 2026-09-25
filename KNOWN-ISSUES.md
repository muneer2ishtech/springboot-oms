#

Known issues that have been confirmed by testing but not yet fixed. Each entry has enough detail to reproduce and fix without re-investigating.

---

## 1. Creating a Customer Discount for a non-existent customer returns `500` and leaks internal SQL

**Status:** Open
**Impact:** Medium — a client's invalid input is reported as a server fault instead of a validation error, and the response body discloses the database name, table and column names, the foreign key constraint name and the SQL statement.
**Affects:** `POST /api/v1/customer-discounts` and `PUT /api/v1/customer-discounts`

### Description

`CustomerDiscountVo.customerId` is validated for presence and format only. Nothing checks that the customer exists, so the request proceeds to the insert and the database rejects it on the foreign key `fk_customer_discount_customer_id_user_profile`.

The resulting `DataIntegrityViolationException` reaches `CustomExceptionHandler`, which returns `400 - Bad Request` only when the exception message contains the constraint name `uk_sales_order_item_sales_order_id_product_id`. Every other integrity violation, including this one, falls to its `else` branch and is returned as `500 - Internal Server Error` with `ex.getMessage()` as the response body. That message is the driver's text, so the client receives:

```
could not execute statement [(conn=5) Cannot add or update a child row: a foreign key constraint fails
(`ishtech_oms_dev_db`.`t_customer_discount`, CONSTRAINT `fk_customer_discount_customer_id_user_profile`
FOREIGN KEY (`customer_id`) REFERENCES `t_user_profile` (`id`))]
[insert into t_customer_discount (customer_id,discount_percent,discount_type,is_active) values (?,?,?,?)];
SQL [insert into t_customer_discount (customer_id,discount_percent,discount_type,is_active) values (?,?,?,?)];
constraint [fk_customer_discount_customer_id_user_profile]
```

The inserted column list follows the fields present in the request, so it also contains `product_id` when `productId` is given.

Two separate faults: the status code is wrong, and internal schema detail is disclosed to the caller.

Because the `else` branch is shared, the same response is expected from any other unmatched integrity violation in the application, for example a `productId` that does not exist. Only the `customerId` case above has been confirmed by testing.

### Steps to reproduce

Start the application and obtain an `ADMIN` token as described in [CURL-INFO.md](./CURL-INFO.md), section "Preconditions". Then post a discount for a customer id that does not exist in `t_user_profile`:

```sh
curl --location 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <ADMIN_ACCESS_TOKEN>' \
--data '{
    "customerId": 999,
    "discountType": "SALES_ORDER_PERCENT",
    "discountPercent": 0.02
}'
```

Expected: `400 - Bad Request` with a validation error identifying `customerId` as unknown.
Actual: `500 - Internal Server Error` with the driver message quoted above as the response body.

### Likely cause

`CustomExceptionHandler.handleDataIntegrityViolationException(...)` matches one hardcoded constraint name and returns `ex.getMessage()` with `HttpStatus.INTERNAL_SERVER_ERROR` for everything else, so every unanticipated integrity violation both gets the wrong status and returns the driver's raw text.

Related: that method calls `ex.getMessage().contains(...)` without a null check, which will throw a `NullPointerException` inside the handler if the message is ever `null`.

### Suggested fix

1. In `CustomerDiscountServiceImpl`, verify that the referenced customer, and the product when `productId` is given, exist before the insert, and throw so the API returns `400` with a message naming the offending field.
2. Change the handler's fallback so it never returns `ex.getMessage()` to the client: log the exception and return a generic message. `springboot-books-app` commit `ec16a98` addressed the same class of defect, by adding the missing handler rather than by changing the fallback; the fallback itself is the leak here.
3. Add tests asserting `400`, not `500`, for a non-existent `customerId`, and asserting that the response body contains no SQL, table or constraint names.
