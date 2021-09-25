package DSPPCode.spark.topk_power.impl;

import DSPPCode.spark.topk_power.question.TopKPower;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TopKPowerImpl extends TopKPower {

  @Override
  public int topKPower(JavaRDD<String> lines) {

    JavaPairRDD<String,Integer>pairs=lines.flatMapToPair(
        new PairFlatMapFunction<String, String, Integer>() {
          @Override
          public Iterator<Tuple2<String, Integer>> call(String s) throws Exception {
            String [] list=s.split(" ");
            List<Tuple2<String,Integer>>list1=new ArrayList<>();
            for(String si:list){
              list1.add(new Tuple2<>(si,1));
            }

            return list1.iterator();
          }
        }
    );
    // JavaRDD<String> ids=lines.flatMap(
    //     new FlatMapFunction<String, String>() {
    //       @Override
    //       public Iterator<String> call(String s) throws Exception {
    //
    //         return Arrays.asList(s.split(" ")).iterator();
    //       }
    //     }
    // ) ;
    // JavaPairRDD<String,Integer>pairs=ids.mapToPair(
    //     new PairFunction<String, String, Integer>() {
    //       @Override
    //       public Tuple2<String, Integer> call(String s) throws Exception {
    //         return new Tuple2<>(s,1);
    //       }
    //     }
    // );
    JavaPairRDD<Integer,String>idCounts=pairs.groupByKey().mapToPair(
        new PairFunction<Tuple2<String, Iterable<Integer>>, Integer,String>() {
          @Override
          public Tuple2<Integer,String> call(
              Tuple2<String, Iterable<Integer>> stringIterableTuple2) throws Exception {
            Integer sum=0;
            for(Integer i: stringIterableTuple2._2()){
              sum+=i;
            }
            return new Tuple2<Integer, String>(sum*sum,stringIterableTuple2._1);
          }
        }
    );
    List<Tuple2<Integer,String>>list=idCounts.sortByKey(false).take(5);
    int result=0;
    for(Tuple2<Integer,String>l:list){
      System.out.println(l._1);
      result+=l._1;
    }
    return result;

    // // 将lines中的每一个文本行按空格分割成单个单词
    // JavaRDD<String> ids =
    //     lines.flatMap(
    //         new FlatMapFunction<String, String>() {
    //           @Override
    //           public Iterator<String> call(String line) throws Exception {
    //             return Arrays.asList(line.split(" ")).iterator();
    //           }
    //         });
    // // 将每个id的频数设置为1，即将每个单词映射为[单词, 1]
    // JavaPairRDD<String, Integer> pairs =
    //     ids.mapToPair(
    //         new PairFunction<String, String, Integer>() {
    //           @Override
    //           public Tuple2<String, Integer> call(String id) throws Exception {
    //             return new Tuple2<String, Integer>(id, 1);
    //           }
    //         });
    // // 按单词聚合，并对相同单词的频数使用sum进行累计
    // JavaPairRDD<String, Integer> idCounts =
    //     pairs
    //         .groupByKey()
    //         .mapToPair(
    //             new PairFunction<Tuple2<String, Iterable<Integer>>, String, Integer>() {
    //               @Override
    //               public Tuple2<String, Integer> call(Tuple2<String, Iterable<Integer>> t)
    //                   throws Exception {
    //                 Integer sum = Integer.valueOf(0);
    //                 for (Integer i : t._2) {
    //                   sum += i;
    //                 }
    //                 return new Tuple2<String, Integer>(t._1, sum);
    //               }
    //             });
    //
    // //降序排列
    // JavaPairRDD<Integer,String> id1= idCounts.mapToPair(v->new Tuple2<>(v._2, v._1)).sortByKey(false);
    // List<Tuple2<Integer, String>> re=id1.take(5);
    //
    // int result=0;
    // for (Tuple2<Integer, String> tuple : re) {
    //         result+=Math.pow(tuple._1(),2);
    //        }
    // System.out.println(result);
    // return result;
  }
}
