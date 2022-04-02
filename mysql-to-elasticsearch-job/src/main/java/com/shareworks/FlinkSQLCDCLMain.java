package com.shareworks;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author martin.peng
 */
public class FlinkSQLCDCLMain {

    static final ClassLoader CLASS_LOADER = FlinkSQLCDCLMain.class.getClassLoader();

    public static void main(String[] args) throws IOException {
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(3000L);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, fsSettings);

        ParameterTool parameterTool = file2Parameter();

        Map<String, String> ddlMap = file2DDLMap();

        executeDDLSql(tableEnv, parameterTool, ddlMap);

        String querySql = "SELECT o.*, p.name, p.description\n" +
                " FROM orders AS o\n" +
                " LEFT JOIN products AS p ON o.product_id = p.id";

        tableEnv.executeSql("INSERT INTO es_orders ( " + querySql + " )");
    }

    /**
     * 读取外部文件转换成ddl语句
     *
     * @return
     * @throws IOException
     */
    private static Map<String, String> file2DDLMap() throws IOException {
        String allSqlContent = PropertiesUtils.readFile(CLASS_LOADER.getResource(SysConstant.DDL_FILE_NAME).getPath());

        Map<String, String> ddlMap = Arrays.stream(allSqlContent.split(SysConstant.FILE_SEGMENTATION)).collect(Collectors.toMap(sqlContent -> sqlContent.substring(0, sqlContent.indexOf("=")).trim()
                , sqlContent -> sqlContent.substring(sqlContent.indexOf("=") + 1)));
        return ddlMap;
    }

    /**
     * 读取外部配置文件
     *
     * @return
     * @throws IOException
     */
    private static ParameterTool file2Parameter() throws IOException {
        String datasourcePath = CLASS_LOADER.getResource(SysConstant.PROPERTIES_FILE_NAME).getPath();
        ParameterTool parameterTool = ParameterTool.fromPropertiesFile(datasourcePath);
        return parameterTool;
    }

    /**
     * 执行ddl语句
     *
     * @param tableEnv
     * @param parameterTool
     * @param ddlMap
     */
    private static void executeDDLSql(StreamTableEnvironment tableEnv, ParameterTool parameterTool, Map<String, String> ddlMap) {
        tableEnv.executeSql(DDLFormatEnums.MYSQL_ORDER_DDL.apply(parameterTool, ddlMap.get(DDLFormatEnums.MYSQL_ORDER_DDL.getKey())));
        tableEnv.executeSql(DDLFormatEnums.MYSQL_PRODUCT_DDL.apply(parameterTool, ddlMap.get(DDLFormatEnums.MYSQL_PRODUCT_DDL.getKey())));
        tableEnv.executeSql(DDLFormatEnums.ES_ORDER.apply(parameterTool, ddlMap.get(DDLFormatEnums.ES_ORDER.getKey())));
    }
}
