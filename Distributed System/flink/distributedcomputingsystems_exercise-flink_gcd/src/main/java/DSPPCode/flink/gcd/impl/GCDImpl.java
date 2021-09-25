package DSPPCode.flink.gcd.impl;

import DSPPCode.flink.gcd.question.GCD;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.util.Collector;

public class GCDImpl extends GCD {

  @Override
  public DataStream<Tuple3<String, Integer, Integer>> calGCD(
      IterativeStream<Tuple3<String, Integer, Integer>> iteration) {
    //实现每一轮迭代计算的逻辑
    DataStream<Tuple3<String, Integer, Integer>> cal=iteration.flatMap(
        new FlatMapFunction<Tuple3<String, Integer, Integer>, Tuple3<String, Integer, Integer>>() {
          @Override
          public void flatMap(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3,
              Collector<Tuple3<String, Integer, Integer>> collector) throws Exception {
            int a=stringIntegerIntegerTuple3.f1;
            int b=stringIntegerIntegerTuple3.f2;
            if(a<b){
              int temp=a;
              a=b;
              b=temp;
            }
            int r=a%b;
           collector.collect(new Tuple3<>(stringIntegerIntegerTuple3.f0,b,r));
          }
        }
    );
    //创建反馈流
    DataStream<Tuple3<String,Integer, Integer>> feedback =
        cal.filter(
            new FilterFunction<Tuple3<String, Integer, Integer>>() {
              @Override
              public boolean filter(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3)
                  throws Exception {
                return stringIntegerIntegerTuple3.f2!=0;
              }
            }
        );
    //筛选出向前反馈作为下一轮输出的datastream 如果不符合筛选条件就停止反馈
    iteration.closeWith(feedback);
    //创建输出流
    DataStream<Tuple3<String,Integer, Integer>> outputStream=cal;
    outputStream.print();
    return outputStream;
  }
}
