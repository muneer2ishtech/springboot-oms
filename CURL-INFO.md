#

- Check and use the correct port for the API calls.

- For API names and descriptions:
    - See [API-INFO.md](./API-INFO.md)

# Auth APIs
- For Authentication & Authorization APIs:
    - See [ishtech-springboot-jwtauth/CURL-INFO.md](https://github.com/IshTech/ishtech-springboot-jwtauth/blob/main/CURL-INFO.md)


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
