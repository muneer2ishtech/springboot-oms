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

# Customer Discount

```
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

```
