## Database

### Local
- You need local instance or docker of MariaDB / MySQL
    - MariaDB is the default, MySQL is the alternative
    - To run using MySQL instead of MariaDB, comment out MariaDB portions and uncomment MySQL portions in `pom.xml` and `application-xxx.properties`

- Default ports
    - MariaDB `23306`
    - MySQL `13306`
    - Check on which port your DB is actually running and update `spring.datasource.url` in `application-xxx.properties` accordingly

- I have customized docker for various databases
    - For MariaDB
        - See [https://github.com/IshTech/docker-db/tree/main/mariadb](https://github.com/IshTech/docker-db/tree/main/mariadb)
    - For MySQL
        - See [https://github.com/IshTech/docker-db/tree/main/mysql](https://github.com/IshTech/docker-db/tree/main/mysql)

- Login to DB as `root` and run [init_db.sql](src/test/resources/db/init_db.sql) to setup DB Schema, DB User and Grant privileges

#### DB Access
- Connect to MariaDB
    - `mariadb -u ishtech_oms_dev_user -pishtech_oms_dev_pass -D ishtech_oms_dev_db`
- Connect to MySQL
    - `mysql -u ishtech_oms_dev_user -pishtech_oms_dev_pass -D ishtech_oms_dev_db`

### Flyway migration files
- Path `src/main/resources/db/migration/`
- To create migration files with date and time in the file name
    - E.g. `V20251109_214359__create_table_product.sql`

```
touch src/main/resources/db/migration/V$(date +"%Y%m%d_%H%M%S")__create_table_TODO_PUT_TABLE_NAME_WITHOUT_PREFIX.sql

```
