# Known Issues

Known issues that have been confirmed by testing but not yet fixed. Each entry has enough detail to reproduce and fix without re-investigating.

---

## 1. `PUT /api/v1/users/{userId}` clears fields absent from the request body

See ishtech-springboot-jwtauth [`KNOWN-ISSUES.md`, issue 1](https://github.com/IshTech/ishtech-springboot-jwtauth/blob/dev/KNOWN-ISSUES.md).

---

## 2. Get by id returns `500` (`ClassCastException`) for products and customer discounts

**Status:** Open
**Impact:** High — a get by id of an existing record always fails with `500 - Internal Server Error`.
**Affects:**
- `GET /api/v1/products/{productId}` and `GET /api/v1/customer-discounts/{customerDiscountId}`: confirmed by testing.
- `GET /api/v1/sales-orders/{salesOrderId}`: same code path, not tested.
- The list APIs (`GET /api/v1/products`, `GET /api/v1/customer-discounts`, `GET /api/v1/sales-orders`) return `200`, but by the same cause their items have only the base fields. Recorded for products in the upstream issue; not checked here.

### Description

The cause is in ishtech-base-jpa: see [`KNOWN-ISSUES.md`, issue 1](https://github.com/IshTech/ishtech-base-jpa/blob/dev/KNOWN-ISSUES.md).

`ProductServiceImpl`, `CustomerDiscountServiceImpl` and `SalesOrderServiceImpl` do not override `findOneByIdAndMapToVoOrElseThrow(Long)` or `findAllAndMapToVo(spec, pageable)`, so the defaults of `BaseStandardService` are used. Those defaults map the entity with `getMapper().toBaseStandardVo(...)`, which returns a `BaseStandardEntityVo`, and cast it unchecked to the service's VO type. The controllers then fail on the implicit cast:

```
java.lang.ClassCastException: class fi.ishtech.base.vo.BaseStandardEntityVo cannot be cast to class fi.ishtech.practice.oms.payload.CustomerDiscountVo
```

Confirmed on `dev` (commit `5aa57b7`) with test Level 2 (`./mvnw spring-boot:run`, profile `dev`, MariaDB) and Level 3 (`docker compose`).

### Steps to reproduce

Start the application, obtain an `ADMIN` token and create a product and a customer discount as described in [CURL-INFO.md](./CURL-INFO.md). Then fetch them by id, using the ids returned by the create calls:

```sh
curl --location 'http://localhost:8080/api/v1/products/1' \
--header 'Authorization: Bearer <ADMIN_ACCESS_TOKEN>'

curl --location 'http://localhost:8080/api/v1/customer-discounts/1' \
--header 'Authorization: Bearer <ADMIN_ACCESS_TOKEN>'
```

Expected: `200 - OK` with the record, including its own fields, for example `name` and `unitPrice`, or `customerId` and `discountType`.
Actual: `500 - Internal Server Error`; the application log shows the `ClassCastException` above.

### Likely cause

The default mapping methods of the ishtech-base-jpa base services map to the base VO class instead of the service's VO type, and the unchecked cast hides it at compile time.

### Suggested fix

1. Preferred: fix it in ishtech-base-jpa as its issue 1 suggests, release it, and update `ishtech-springboot-jwtauth.version` here to a version that contains the fix.
2. Until then, in this application: override `findOneByIdAndMapToVoOrElseThrow(Long)` and `findAllAndMapToVo(spec, pageable)` in `ProductServiceImpl`, `CustomerDiscountServiceImpl` and `SalesOrderServiceImpl`, mapping with each service's own mapper (for example `productMapper.toSemiDetailVo(entity)`). Remove the overrides once the ishtech-base-jpa fix is in use.
3. Add tests asserting that get by id returns `200` with the VO's own fields, and that list items contain them.
