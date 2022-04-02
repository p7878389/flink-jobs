productDDL=CREATE TABLE products
(
    id          INT,
    name        STRING,
    description STRING,
    PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'mysql-cdc',
      'hostname' = '%s',
      'port' = '%s',
      'username' = '%s',
      'password' = '%s',
      'database-name' = '%s',
      'table-name' = '%s'
      )
----
orderDDL = CREATE TABLE orders
(
    order_id      INT,
    order_date    TIMESTAMP(0),
    customer_name STRING,
    price         DECIMAL(10, 5),
    product_id    INT,
    order_status  BOOLEAN,
    PRIMARY KEY (order_id) NOT ENFORCED
) WITH (
      'connector' = 'mysql-cdc',
      'hostname' = '%s',
      'port' = '%s',
      'username' = '%s',
      'password' = '%s',
      'database-name' = '%s',
      'table-name' = '%s'
      )
----
esOrder=CREATE TABLE es_orders
(
    order_id            INT,
    order_date          TIMESTAMP(0),
    customer_name       STRING,
    price               DECIMAL(10, 5),
    product_id          INT,
    order_status        BOOLEAN,
    product_name        STRING,
    product_description STRING,
    PRIMARY KEY (order_id) NOT ENFORCED
) WITH (
      'connector' = 'elasticsearch-7',
      'hosts' = '%s',
      'index' = '%s'
      )