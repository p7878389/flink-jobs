package com.shareworks;

import lombok.Getter;
import org.apache.flink.api.java.utils.ParameterTool;

/**
 * @author martin.peng
 */

@Getter
public enum DDLFormatEnums {
    MYSQL_ORDER_DDL("orderDDL") {
        @Override
        public String apply(ParameterTool parameterTool, String sql) {
            return String.format(sql, parameterTool.get(SysConstant.MYSQL_HOST)
                    , parameterTool.get(SysConstant.MYSQL_PORT)
                    , parameterTool.get(SysConstant.MYSQL_NAME)
                    , parameterTool.get(SysConstant.MYSQL_PASSWORD)
                    , parameterTool.get(SysConstant.MYSQL_DATABASE)
                    , parameterTool.get(SysConstant.MYSQL_TABLE_NAME)
            );
        }
    },
    MYSQL_PRODUCT_DDL("productDDL") {
        @Override
        public String apply(ParameterTool parameterTool, String sql) {
            return String.format(sql, parameterTool.get(SysConstant.MYSQL_HOST)
                    , parameterTool.get(SysConstant.MYSQL_PORT)
                    , parameterTool.get(SysConstant.MYSQL_NAME)
                    , parameterTool.get(SysConstant.MYSQL_PASSWORD)
                    , parameterTool.get(SysConstant.MYSQL_DATABASE)
                    , parameterTool.get(SysConstant.MYSQL_TABLE_NAME)
            );
        }
    },
    ES_ORDER("esOrder") {
        @Override
        public String apply(ParameterTool parameterTool, String sql) {
            return String.format(sql, parameterTool.get(SysConstant.ES_HOST)
                    , parameterTool.get(SysConstant.ES_INDEX)
            );
        }
    };

    private final String key;

    public abstract String apply(ParameterTool parameterTool, String sql);

    DDLFormatEnums(String key) {
        this.key = key;
    }

    public static void main(String[] args) {
    }
}
