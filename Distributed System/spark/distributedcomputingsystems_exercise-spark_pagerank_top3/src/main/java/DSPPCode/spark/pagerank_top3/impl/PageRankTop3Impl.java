package DSPPCode.spark.pagerank_top3.impl;

import DSPPCode.spark.pagerank_top3.question.PageRankTop3;
import clojure.lang.IFn.D;
import it.unimi.dsi.fastutil.doubles.DoubleLinkedOpenCustomHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PageRankTop3Impl extends PageRankTop3 {

  @Override
  public JavaPairRDD<String, Double> getTop3(JavaRDD<String> text, int iterateNum) {

    int itr=iterateNum;
    double factor=0.85;
    System.out.println(text);
    HashSet<String> set=new HashSet<>();
    // long N = Long.parseLong(String.valueOf(text)); // 从输入中获取网页总数N
    // 将文本数据转换成[网页, {链接列表}]键值对
    //links网页链接关系
    JavaPairRDD<String, List<String>> links =text.mapToPair(
        new PairFunction<String, String, List<String>>() {
          @Override
          public Tuple2<String, List<String>> call(String s) throws Exception {
            String[] tokens=s.split(" ");
            List <String>list=new ArrayList<>();

            set.add(tokens[0]);
            for(int i=2;i<tokens.length;i=i+2){
              list.add(tokens[i]);
              set.add(tokens[i]);
            }
            return new Tuple2<>(tokens[0],list);
          }
        }).cache();//持久化到内存
    Long finalSizes=links.count();
    // int N=set.size();
   //  //计算页面数
   //  double sizes=0.0;
   //  Map<String,Long> key =links.countByKey();
   //  for (Map.Entry<String,Long> entry : key.entrySet()){
   //    sizes=sizes+1.0;
   //  }
   //  // 对排名值保留5位小数，并打印最终网页排名结果
   // Double finalSizes = sizes;


    // 初始化每个页面的排名值，得到[网页, 排名值]键值对
    JavaPairRDD<String ,Double> ranks=
        text.mapToPair(
      new PairFunction<String,String,Double>() {
        @Override
        public Tuple2<String, Double> call(String s) throws Exception {
          String[] tokens=s.split(" ");
          return new Tuple2<>(tokens[0],Double.valueOf(tokens[1]));
        }

      });
    //执行itr次迭代计算
    for(int i=0;i<itr;i++) {
      // 将links和ranks做join，得到[网页, {{链接列表}, 排名值}]
      JavaPairRDD<String, Double> contributions = links.join(ranks)
          .flatMapToPair(
              //计算每个网页对其链接网页的贡献值
              new PairFlatMapFunction<Tuple2<String, Tuple2<List<String>, Double>>, String, Double>() {
                @Override
                public Iterator<Tuple2<String, Double>> call(
                    Tuple2<String, Tuple2<List<String>, Double>> t)
                    throws Exception {
                  List<Tuple2<String, Double>> list = new ArrayList<>();
                  //{A,{{B,D},1.0}
                  //t._2._1.size()即为{B,D}的大小
                  for (int i = 0; i < t._2._1.size(); i++) {
                    // 网页排名值除以链接总数
                    //[B 0.5] [D 0.5]即为A对B和D的贡献值
                    list.add(new Tuple2<>(t._2._1.get(i), t._2._2 / t._2._1.size()));

                  }

                  return list.iterator();
                }
              });

      ranks =
          contributions
              // 聚合对相同网页的贡献值，求和得到对每个网页的总贡献值
              .reduceByKey(
                  new Function2<Double, Double, Double>() {
                    @Override
                    public Double call(Double aDouble, Double aDouble2) throws Exception {
                      return aDouble+aDouble2;
                    }
                  }
              )
              // 根据公式计算得到每个网页的新排名值
              .mapValues(
                  new Function<Double, Double>() {
                    @Override
                    public Double call(Double aDouble) throws Exception {
                      return (1-factor) * 1.0 / finalSizes + factor * aDouble;

                    }
                  }
              );
    }

    // final Double[] x = new Double[50];
    System.out.println(finalSizes);
    JavaPairRDD<Double,String>result=ranks.mapToPair(
        new PairFunction<Tuple2<String, Double>, Double, String>() {
          @Override
          public Tuple2<Double, String> call(Tuple2<String, Double> stringDoubleTuple2)
              throws Exception {
            return new Tuple2<>(stringDoubleTuple2._2,stringDoubleTuple2._1);
          }
        }
    ).sortByKey(false);

    //得到ranktop3法一

//首先得到rank排在第三位的值
     List<Tuple2<Double,String>>  res=result.take(3);
     Double compare=res.get(2)._1;
     //筛选出rank前三位
     JavaPairRDD<Double,String> top3=result.filter(
         new Function<Tuple2< Double,String>, Boolean>() {
           @Override
           public Boolean call(Tuple2< Double,String> s) throws Exception {
            if(s._1>=compare){
              return true;
            }
            else{
              return false;
            }
           }
         }
     );
     //打印
    System.out.println(compare);
    Double cm1=res.get(0)._1;
    Double cm2=res.get(1)._1;
    System.out.println(cm1);
    System.out.println(cm2);
    //对所有元组按照key(也就是rank值）降序排列 并将key(rank) 和值（点）调换进行输出
    //  return top3.sortByKey(false).mapToPair(
    //      new PairFunction<Tuple2<Double, String>, String, Double>() {
    //        @Override
    //        public Tuple2<String, Double> call(Tuple2<Double, String> doubleStringTuple2)
    //            throws Exception {
    //          return new Tuple2<>(doubleStringTuple2._2,doubleStringTuple2._1);
    //        }
    //      }
    //  );
    //sort by key 需要对值sort 而不是ABCD这样的点
    //上面一坨的简化版
     return top3.sortByKey(false).mapToPair(v->new Tuple2<>(v._2, v._1));

    //得到ranktop3法二
    // JavaPairRDD<String,Double>top3Rank=ranks
//将pairRDD转化为RDD
    //     .map(p->new Tuple2<>(p._1,p._2))
    //sortBy作用在RDD上 按网页排名降序
    //     .sortBy(tuple->tuple._2,false,ranks.getNumPartitions())
    //将排好序的排名值编号
    //     .zipWithIndex()
    //过滤取前3
    //     .filter(
    //         new Function<Tuple2<Tuple2<String, Double>, Long>, Boolean>() {
    //           @Override
    //           public Boolean call(Tuple2<Tuple2<String, Double>, Long> tuple)
    //               throws Exception {
    //             if(tuple._2<3)
    //             {
    //               return true;
    //             }else{
    //               return false;
    //             }
    //
    //           }
    //         }
    //转为PairRDD 返回
    //     ).mapToPair(
    //         new PairFunction<Tuple2<Tuple2<String, Double>, Long>, String, Double>() {
    //           @Override
    //           public Tuple2<String, Double> call(
    //               Tuple2<Tuple2<String, Double>, Long> tuple2LongTuple2) throws Exception {
    //             return new Tuple2<>(tuple2LongTuple2._1._1(),tuple2LongTuple2._1()._2());
    //           }
    //         }
    //     );
    // return top3Rank;
  }
}
