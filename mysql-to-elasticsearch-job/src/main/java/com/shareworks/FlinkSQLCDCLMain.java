package com.shareworks;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author martin.peng
 */
public class FlinkSQLCDCLMain {


    static final String MYSQL_PRODUCT_DDL = "CREATE TABLE products (\n" +
            "    id INT,\n" +
            "    name STRING,\n" +
            "    description STRING,\n" +
            "    PRIMARY KEY (id) NOT ENFORCED\n" +
            "  ) WITH (\n" +
            "    'connector' = 'mysql-cdc',\n" +
            "    'hostname' = 'localhost',\n" +
            "    'port' = '3306',\n" +
            "    'username' = 'flink-cdc',\n" +
            "    'password' = 'BhkboKVMO7p5',\n" +
            "    'database-name' = 'mydb',\n" +
            "    'table-name' = 'products'\n" +
            "  )";

    static final String MYSQL_ORDER_DDL = "CREATE TABLE orders (\n" +
            "   order_id INT,\n" +
            "   order_date TIMESTAMP(0),\n" +
            "   customer_name STRING,\n" +
            "   price DECIMAL(10, 5),\n" +
            "   product_id INT,\n" +
            "   order_status BOOLEAN,\n" +
            "   PRIMARY KEY (order_id) NOT ENFORCED\n" +
            " ) WITH (\n" +
            "   'connector' = 'mysql-cdc',\n" +
            "   'hostname' = 'localhost',\n" +
            "   'port' = '3306',\n" +
            "   'username' = 'flink-cdc',\n" +
            "   'password' = 'BhkboKVMO7p5',\n" +
            "   'database-name' = 'mydb',\n" +
            "   'table-name' = 'orders'\n" +
            " )";

    static final String ES_DDL = "CREATE TABLE es_orders (\n" +
            "   order_id INT,\n" +
            "   order_date TIMESTAMP(0),\n" +
            "   customer_name STRING,\n" +
            "   price DECIMAL(10, 5),\n" +
            "   product_id INT,\n" +
            "   order_status BOOLEAN,\n" +
            "   product_name STRING,\n" +
            "   product_description STRING,\n" +
            "   PRIMARY KEY (order_id) NOT ENFORCED\n" +
            " ) WITH (\n" +
            "     'connector' = 'elasticsearch-7',\n" +
            "     'hosts' = 'http://localhost:9200',\n" +
            "     'index' = 'es_orders'\n" +
            " )";

    public static void main(String[] args) {
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(3000L);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, fsSettings);
        tableEnv.executeSql(MYSQL_ORDER_DDL);
        tableEnv.executeSql(MYSQL_PRODUCT_DDL);
        tableEnv.executeSql(ES_DDL);

        String querySql = "SELECT o.*, p.name, p.description\n" +
                " FROM orders AS o\n" +
                " LEFT JOIN products AS p ON o.product_id = p.id";

        tableEnv.executeSql("INSERT INTO es_orders ( " + querySql + " )");
    }
}
