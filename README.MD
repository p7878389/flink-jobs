## Flink demo

- flink-version:1.13.2
- flink-connection-mysql-cdc:2.0.1
- flink-connector-elasticsearch7_2.1
- mysql-version:8.X

### mysql操作

1. mysql开启binlog日志，binlog-format=row
2. **创建新用户**,mysql用户需要以下特殊权限
    ~~~mysql
     CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
     GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';
     FLUSH PRIVILEGES;
   ~~~
3. 创建相关源表信息
    ~~~sql
    CREATE DATABASE mydb;
    USE mydb;
    CREATE TABLE products (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512)
    );
    ALTER TABLE products AUTO_INCREMENT = 101;
    
    INSERT INTO products
    VALUES (default,"scooter","Small 2-wheel scooter"),
    (default,"car battery","12V car battery"),
    (default,"12-pack drill bits","12-pack of drill bits with sizes ranging from #40 to #3"),
    (default,"hammer","12oz carpenter's hammer"),
    (default,"hammer","14oz carpenter's hammer"),
    (default,"hammer","16oz carpenter's hammer"),
    (default,"rocks","box of assorted rocks"),
    (default,"jacket","water resistent black wind breaker"),
    (default,"spare tire","24 inch spare tire");
    
    CREATE TABLE orders (
    order_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 5) NOT NULL,
    product_id INTEGER NOT NULL,
    order_status BOOLEAN NOT NULL -- Whether order has been placed
    ) AUTO_INCREMENT = 10001;
    
    INSERT INTO orders
    VALUES (default, '2020-07-30 10:08:22', 'Jark', 50.50, 102, false),
    (default, '2020-07-30 10:11:09', 'Sally', 15.00, 105, false),
    (default, '2020-07-30 12:00:30', 'Edward', 25.25, 106, false);
    ~~~

### flink操作

1. 将**flink-connection-mysql-cdc、flink-sql-connector-elasticsearch7_2.11-1.13.2**这个两个jar包放到lib目录下，如果是其他数据源下载对于jar放到lib下
2. 启动flink，进入bin目录执行 **./start-cluster.sh**本地集群模式，停止flink命令**stop-cluster.sh**
3. bin目录中创建相关维表信息（可能认为是复制数据源表结构），**源表（mysql表）对应的维表（flink）都需要设置主键**，flink根据主键值来执行crud
   > 进入bin目录执行 ./sql-client.sh，将下面的sql复制到命令行执行
   ~~~mysql
   -- mysql 产品表
    CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    PRIMARY KEY (id) NOT ENFORCED
    ) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'localhost',
    'port' = '3306',
    'username' = 'flink-cdc',
    'password' = 'BhkboKVMO7p5',
    'database-name' = 'mydb',
    'table-name' = 'products'
    );
    
    -- mysql订单表
    CREATE TABLE orders (
    order_id INT,
    order_date TIMESTAMP(0),
    customer_name STRING,
    price DECIMAL(10, 5),
    product_id INT,
    order_status BOOLEAN,
    PRIMARY KEY (order_id) NOT ENFORCED
    ) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'localhost',
    'port' = '3306',
    'username' = 'flink-cdc',
    'password' = 'BhkboKVMO7p5',
    'database-name' = 'mydb',
    'table-name' = 'orders'
    );
    
    -- es宽表
    CREATE TABLE enriched_orders (
    order_id INT,
    order_date TIMESTAMP(0),
    customer_name STRING,
    price DECIMAL(10, 5),
    product_id INT,
    order_status BOOLEAN,
    product_name STRING,
    product_description STRING,
    PRIMARY KEY (order_id) NOT ENFORCED
    ) WITH (
    'connector' = 'elasticsearch-7',
    'hosts' = 'http://localhost:9200',
    'index' = 'enriched_orders'
    );
    ~~~
4. 提交job（同步数据），还是在上面的sql-client中执行下面sql，**将的结果集写入enriched_orders维表并将结果集同步到es中**
    ~~~mysql
   INSERT INTO enriched_orders
    SELECT o.*, p.name, p.description
    FROM orders AS o
    LEFT JOIN products AS p ON o.product_id = p.id
   ~~~
5. 访问 http://localhost:8081 就可以看到flink web页面，在Running Jobs菜单中查看正在运行的任务
6. 访问 http://localhost:5601 通过kibana查看es中的数据

### es、kibana docker-compose

~~~docker
version: '2.1'
services:
  elasticsearch:
    image: elastic/elasticsearch:7.6.0
    environment:
      - cluster.name=docker-cluster
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - discovery.type=single-node
    ports:
      - "9200:9200"
      - "9300:9300"
    ulimits:
      memlock:
        soft: -1
        hard: -1
      nofile:
        soft: 65536
        hard: 65536
  kibana:
    image: elastic/kibana:7.6.0
    ports:
      - "5601:5601"
~~~