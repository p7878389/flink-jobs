package com.shareworks;

import org.apache.flink.api.java.ExecutionEnvironment;

/**
 * @author martin.peng
 */
public class FileReadCsvMain {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.readCsvFile(FileReadCsvMain.class.getClassLoader().getResource("file.csv").getPath())
                .fieldDelimiter(",").ignoreFirstLine().pojoType(SensorDTO.class, "id", "timestamp", "temperature").print();
    }


}
