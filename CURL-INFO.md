#

- Check and use the correct port for the API calls.

- All values in the requests below, whether in the URL or in the request body, are only examples.
    - Change the ids in the URL, and the ids and other values in the request body, to the ones you want to test with.
    - Any id used must be of a record that already exists, e.g. `customerId` and `productId`.

- For API names and descriptions:
    - See [API-INFO.md](./API-INFO.md)

# Auth APIs
- For Authentication & Authorization APIs:
    - See [ishtech-springboot-jwtauth/CURL-INFO.md](https://github.com/IshTech/ishtech-springboot-jwtauth/blob/main/CURL-INFO.md)


# Preconditions

- An `ADMIN` token is needed for the calls below that create or update a Product or a Customer Discount:
    - `POST /api/v1/products`, `PUT /api/v1/products`
    - `POST /api/v1/customer-discounts`, `PUT /api/v1/customer-discounts`
- Signup grants only the `USER` role, so these calls return `403 - Forbidden` with a signup token. Change the role of the user to `ADMIN` first:
    - See [ishtech-springboot-jwtauth-web/DB-SETUP.md / Change role to admin for an user](https://github.com/IshTech/ishtech-springboot-jwtauth/blob/main/ishtech-springboot-jwtauth-web/DB-SETUP.md#change-role-to-admin-for-an-user)
    - Use the database, schema and table names of this application; see [DB-SETUP.md](./DB-SETUP.md)
- Use the `ADMIN` user's `access_token` from signin as `TODO_JWT` in the calls below.


# Products

## Create

```sh
curl --location 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "name": "Work Table 30cmX50cm",
    "unitPrice": 100
}'

curl --location 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "name": "Chair with Wheels",
    "unitPrice": 50
}'

curl --location 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "name": "Coffee Table",
    "unitPrice": 75
}'

curl --location 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "name": "Sofa",
    "unitPrice": 150
}'

```

## Get by id

```sh
curl --location 'http://localhost:8080/api/v1/products/1' \
--header 'Authorization: Bearer TODO_JWT'

```

## List

- Optional filters: `name`, `minUnitPrice`, `maxUnitPrice`
- Optional pagination and sorting: `page` (starts from `0`), `size`, `sort`

```sh
curl --location 'http://localhost:8080/api/v1/products?minUnitPrice=60&page=0&size=20&sort=id,asc' \
--header 'Authorization: Bearer TODO_JWT'

```

## Update

- `id` in the request body is the id of the Product to update.
- `name` can't be changed: a different `name` in the request body is ignored.
- Send every field, including `"isActive": true`. A field left out of the request body is saved as empty, and a missing `isActive` deactivates the Product.

```sh
curl --location --request PUT 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "id": 4,
    "name": "Sofa",
    "unitPrice": 175,
    "isActive": true
}'

```

## Delete

- Deactivates the Product, and returns `410 - Gone`.

```sh
curl --location --request DELETE 'http://localhost:8080/api/v1/products/4' \
--header 'Authorization: Bearer TODO_JWT'

```

# Customer Discount

## Create

```sh
curl --location 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "customerId": 2,
    "productId": 1,
    "discountType": "PRODUCT_PERCENT",
    "discountPercent": 0.05
}'

curl --location 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "customerId": 3,
    "discountType": "SALES_ORDER_PERCENT",
    "discountPercent": 0.02
}'

curl --location 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "customerId": 2,
    "discountType": "SALES_ORDER_PERCENT",
    "discountPercent": 0.03
}'

curl --location 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "customerId": 2,
    "productId": 2,
    "discountType": "PRODUCT_BUY_X_PAY_Y",
    "buyQuantity": 3,
    "payQuantity": 2
}'

```

## Get by id

```sh
curl --location 'http://localhost:8080/api/v1/customer-discounts/1' \
--header 'Authorization: Bearer TODO_JWT'

```

## List

- Optional filters: `customerId`, `productId`, `discountType` (one or more), `minDiscountPercent`, `maxDiscountPercent`, `minBuyQuantity`, `maxBuyQuantity`, `minPayQuantity`, `maxPayQuantity`
- Optional pagination and sorting: `page` (starts from `0`), `size`, `sort`

```sh
curl --location 'http://localhost:8080/api/v1/customer-discounts?customerId=2&discountType=PRODUCT_PERCENT&discountType=SALES_ORDER_PERCENT&page=0&size=20' \
--header 'Authorization: Bearer TODO_JWT'

```

## Update

- `id` in the request body is the id of the Customer Discount to update.
- Send every field, including `"isActive": true`. A field left out of the request body is saved as empty, and a missing `isActive` deactivates the Customer Discount.

```sh
curl --location --request PUT 'http://localhost:8080/api/v1/customer-discounts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "id": 1,
    "customerId": 2,
    "productId": 1,
    "discountType": "PRODUCT_PERCENT",
    "discountPercent": 0.10,
    "isActive": true
}'

```

# Sales Order

- A `USER` can create Sales Orders only for themselves: `customerId` must be the user's own id.
- For a `USER`, the list returns only the user's own Sales Orders. An `ADMIN` gets the Sales Orders of all customers.
- Amounts and discounts of the Sales Order and its items are calculated by the application. Don't send them in the request body.

## Create

- At least one item is needed.

```sh
curl --location 'http://localhost:8080/api/v1/sales-orders' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "customerId": 2,
    "salesOrderItems": [
        {
            "productId": 1,
            "quantity": 2
        },
        {
            "productId": 2,
            "quantity": 3
        }
    ]
}'

```

## Get by id

- Returns the Sales Order with its customer and its items.

```sh
curl --location 'http://localhost:8080/api/v1/sales-orders/1' \
--header 'Authorization: Bearer TODO_JWT'

```

## List

- Optional filters: `customerId` (used only for an `ADMIN`), `minTotalAmount`, `maxTotalAmount`, `minDiscountPercent`, `maxDiscountPercent`, `minDiscountAmount`, `maxDiscountAmount`, `minNetAmount`, `maxNetAmount`
- Optional pagination and sorting: `page` (starts from `0`), `size`, `sort`

```sh
curl --location 'http://localhost:8080/api/v1/sales-orders?page=0&size=20&sort=id,desc' \
--header 'Authorization: Bearer TODO_JWT'

```

## Add item

- Adds a Product to an existing Sales Order, and recalculates the amounts and discounts of the Sales Order.
- A Product that is already in the Sales Order can't be added again; change the quantity of its item instead.

```sh
curl --location 'http://localhost:8080/api/v1/sales-orders/sales-order-items' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer TODO_JWT' \
--data '{
    "salesOrderId": 1,
    "productId": 3,
    "quantity": 1
}'

```

## Update item quantity

- The id in the URL is the id of the Sales Order Item, and `quantity` is its new quantity.
- Recalculates the amounts and discounts of the Sales Order.

```sh
curl --location --request PATCH 'http://localhost:8080/api/v1/sales-orders/sales-order-items/1?quantity=5' \
--header 'Authorization: Bearer TODO_JWT'

```

## Delete item

- The id in the URL is the id of the Sales Order Item. Deletes the item, recalculates the amounts and discounts of the Sales Order, and returns `410 - Gone`.

```sh
curl --location --request DELETE 'http://localhost:8080/api/v1/sales-orders/sales-order-items/3' \
--header 'Authorization: Bearer TODO_JWT'

```
