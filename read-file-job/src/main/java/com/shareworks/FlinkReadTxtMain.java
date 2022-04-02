//package com.shareworks;
//
//import org.apache.flink.api.java.ExecutionEnvironment;
//
///**
// * @author martin.peng
// */
//public class FlinkReadTxtMain {
//
//    public static void main(String[] args) throws Exception {
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//
//        String filePath = "classpath:file.txt";
//        env.readTextFile(filePath)
//                .s
//                .fieldDelimiter(",").ignoreFirstLine().pojoType(SensorDTO.class,"id","timestamp","temperature").print();
//        env.execute("read file");
//    }
//}
