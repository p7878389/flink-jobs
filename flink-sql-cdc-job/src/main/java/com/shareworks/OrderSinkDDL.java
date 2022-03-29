package com.shareworks;

/**
 * @author martin.peng
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public interface OrderSinkDDL {

//    String ORDER_SINK_DDL =
//            "CREATE TABLE tb_sink (" +
//                    " id int," +
//                    " name STRING," +
//                    " description STRING" +
//                    ") WITH (" +
//                    " 'connector' = 'elasticsearch-7'" +
//                    " 'hosts' = 'http:localhost:9200'" +
//                    " 'index' = 'order_flink_cdc'" +
//                    " 'sink.bulk-flush.backoff.strategy' = 'EXPONENTIAL'" +
//                    ")";

    String ORDER_SINK_DDL = "CREATE TABLE  tb_sink (" +
            "id int ," +
            "name STRING ," +
            "description STRING " +
            ") " +
            "WITH (" +
            "'connector' = 'elasticsearch-7'," +
            "'hosts' = 'localhost:9200'," +
            "'format' = 'json'," +
            "'index' = 'order_flink_cdc'" +
            " )";
}
