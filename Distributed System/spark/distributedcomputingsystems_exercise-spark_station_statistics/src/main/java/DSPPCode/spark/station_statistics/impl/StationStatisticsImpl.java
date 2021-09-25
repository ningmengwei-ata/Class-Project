package DSPPCode.spark.station_statistics.impl;

import DSPPCode.spark.station_statistics.question.StationStatistics;
import com.google.common.collect.Lists;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import scala.Tuple3;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StationStatisticsImpl extends StationStatistics {

  @Override
  public JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines) {

    JavaPairRDD<Tuple2<String,String>,String> times=lines.mapToPair(
        new PairFunction<String, Tuple2<String, String>, String>() {
          @Override
          public Tuple2<Tuple2<String, String>, String> call(String s) throws Exception {
            String [] times=s.split(" ");


            Tuple2<String,String>key=new Tuple2<>(times[0],times[1]);
            return new Tuple2<Tuple2<String, String>, String>(key,times[2]);
          }
        }
    );
    JavaPairRDD<Tuple2<String,String>,Integer>timeCal=times.groupByKey().mapValues(
        new Function<Iterable<String>, Integer>() {
          @Override
          public Integer call(Iterable<String> strings) throws Exception {
            Long sum=0L;
            List<String>timeList=new ArrayList<>();
            for(String s:strings){
              timeList.add(s);
            }
            // ArrayList<String> timeList= Lists.newArrayList(strings);
            //对字符串排序
            Collections.sort(timeList);
            for(int i=0;i<timeList.size();i=i+2){
              //多次出入基站 累加停留时间
              // 不是timeList.indexOf(k)这不是获取第k个位置的值
              sum+=(new SimpleDateFormat("hh:mm:ss").parse(timeList.get(i+1)).getTime()-new SimpleDateFormat("hh:mm:ss").parse(timeList.get(i)).getTime())/1000;
            }
            return sum.intValue();
          }


        }
    );
    JavaRDD<Tuple3<String, String, Integer>>result=timeCal.map(
        new Function<Tuple2<Tuple2<String, String>, Integer>, Tuple3<String, String, Integer>>() {
          @Override
          public Tuple3<String, String, Integer> call(
              Tuple2<Tuple2<String, String>, Integer> tuple2IntegerTuple2) throws Exception {
            System.out.println(new Tuple3<>(tuple2IntegerTuple2._1._1,tuple2IntegerTuple2._1._2,tuple2IntegerTuple2._2));
            return new Tuple3<>(tuple2IntegerTuple2._1._1,tuple2IntegerTuple2._1._2,tuple2IntegerTuple2._2);
          }
        }
    );

    return result;


    // JavaPairRDD<Tuple2<String, String>, Integer> ids =
    //     lines.mapToPair(
    //         new PairFunction<String, Tuple2<String, String>, Integer>() {
    //           @Override
    //           public Tuple2<Tuple2<String, String>, Integer> call(String s) throws Exception {
    //
    //             String[] tokens = s.split(" ");
    //             Tuple2<String, String> re = new Tuple2<>(tokens[0], tokens[1]);
    //             // List<String> list = new ArrayList<>();
    //             String[]list=tokens[2].split(":");
    //             Integer time=Integer.valueOf(list[0])*3600+Integer.valueOf(list[1])*60+Integer.valueOf(list[2]);
    //
    //             return new Tuple2<Tuple2<String, String>, Integer>(re, time);
    //           }
    //         }
    //     ).cache();
    // JavaPairRDD<Tuple2<String, String>, Integer> reduce_res = ids.reduceByKey(
    //     new Function2<Integer, Integer, Integer>() {
    //       @Override
    //       public Integer call(Integer a, Integer b) throws Exception {
    //         return b - a;
    //       }
    //     }
    // );
    // JavaRDD<Tuple3<String, String, Integer>> result = reduce_res.map(
    //     new Function<Tuple2<Tuple2<String, String>, Integer>, Tuple3<String, String, Integer>>() {
    //       @Override
    //       public Tuple3<String, String, Integer> call(
    //           Tuple2<Tuple2<String, String>, Integer> tuple2DoubleTuple2) throws Exception {
    //         return new Tuple3<>(tuple2DoubleTuple2._1._1, tuple2DoubleTuple2._1._2,
    //             tuple2DoubleTuple2._2);
    //       }
    //     });
    // return result;
  }
}


