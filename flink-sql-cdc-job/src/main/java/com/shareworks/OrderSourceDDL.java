package com.shareworks;

/**
 * @author martin.peng
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public interface OrderSourceDDL {

    String TABLE_NAME = "tb_products_flink_cdc";

    String SOURCE_DDL =
            "CREATE TABLE " + TABLE_NAME + " (\n" +
                    " id INT NOT NULL,\n" +
                    " name STRING,\n" +
                    " description STRING ,\n" +
                    " PRIMARY KEY (id) NOT ENFORCED \n" +
                    ") WITH (\n" +
                    " 'connector' = 'mysql-cdc',\n" +
                    " 'hostname' = 'localhost',\n" +
                    " 'port' = '3306',\n" +
                    " 'username' = 'flink-cdc',\n" +
                    " 'password' = 'BhkboKVMO7p5',\n" +
                    " 'database-name' = 'flink-cdc',\n" +
                    " 'table-name' = 'tb_products_cdc'\n" +
                    ")";

}
