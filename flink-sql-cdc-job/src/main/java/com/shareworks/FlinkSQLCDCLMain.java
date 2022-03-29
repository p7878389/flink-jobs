package com.shareworks;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author martin.peng
 */
public class FlinkSQLCDCLMain {


    public static void main(String[] args) {
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, fsSettings);

        tableEnv.executeSql(OrderSourceDDL.SOURCE_DDL);
        tableEnv.executeSql(OrderSinkDDL.ORDER_SINK_DDL);
        String sql = " select * from " + OrderSourceDDL.TABLE_NAME;
        Table resultTable = tableEnv.sqlQuery(sql);
//        tableEnv.executeSql("insert into tb_sink select * from  " + resultTable);
    }
}
