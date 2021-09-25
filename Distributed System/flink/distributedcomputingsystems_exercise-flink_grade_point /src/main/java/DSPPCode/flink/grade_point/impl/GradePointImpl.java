package DSPPCode.flink.grade_point.impl;

import DSPPCode.flink.grade_point.question.GradePoint;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import java.math.BigDecimal;

public class GradePointImpl extends GradePoint {

  @Override
  public DataStream<Tuple3<String, Integer, Float>> calculate(DataStream<String> text) {
    //将成绩信息映射成[学生id,课程学分,课程成绩]
    DataStream <Tuple3<String,Integer, Float>> words=text.map(
        new MapFunction<String, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(String s) throws Exception {
            String[] list = s.split(" ");
            Float grade = Float.parseFloat(list[2]);
            if (grade != -1) {
              return new Tuple3<>(list[0], Integer.parseInt(list[1]), grade);
            }
            return new Tuple3<>(list[0], Integer.parseInt(list[1]),0F);
          }
        }
    );
    //按学生id进行分组 计算总的学分和平均绩点
    DataStream <Tuple3<String,Integer, Float>> process=words.keyBy(0).reduce(
        new ReduceFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> reduce(
              Tuple3<String, Integer, Float> Tuple3,
              Tuple3<String, Integer, Float> t1) throws Exception {
            //计算结果四舍五入保留1位小数
            float avgScore=(float)Math.round(
                ((Tuple3.f1*Tuple3.f2+t1.f1*t1.f2)/(Tuple3.f1+t1.f1))*10)/10;
            BigDecimal b = new BigDecimal((Tuple3.f1*Tuple3.f2+t1.f1*t1.f2)/(Tuple3.f1+t1.f1));
            float avgS=b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            //没有进行保留一位小数 四舍五入操作的测试
            // float test=(Tuple3.f1*Tuple3.f2+t1.f1*t1.f2)/(Tuple3.f1+t1.f1);
            return new Tuple3<>(Tuple3.f0,Tuple3.f1+t1.f1,avgScore);
          }
        }
    );
    //筛选总学分为10的记录
    DataStream <Tuple3<String,Integer, Float>> result=process.filter(
        new FilterFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public boolean filter(Tuple3<String, Integer, Float> stringIntegerFloatTuple3)
              throws Exception {
            return stringIntegerFloatTuple3.f1==10;
          }
        }
    );
    result.print();
    //返回计算结果
    return result;
  }
}
