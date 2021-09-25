package DSPPCode.spark.single_table_association.impl;

import DSPPCode.spark.single_table_association.question.SingleTableAssociation;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SingleTableAssociationImpl extends SingleTableAssociation {



  @Override
  public JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines) {
    JavaPairRDD<String,String> childParent=lines.mapToPair(
        new PairFunction<String, String, String>() {
          @Override
          public Tuple2<String, String> call(String s) throws Exception {
            if (s.matches("child parent")){
              return null;
            }
              String [] data=s.split(" ");
            System.out.println(data);
            //必加 不然会出现null pointer
            if(data.length!=2){
              return null;
            }
            System.out.println(data[0]);
            return new Tuple2<>(data[0],data[1]);
          }
        }
        //必加 除去空的
    ).filter(Objects::nonNull).cache();
    JavaPairRDD<String,String> parentChild=childParent.mapToPair(
        new PairFunction<Tuple2<String, String>, String, String>() {
          @Override
          public Tuple2<String, String> call(Tuple2<String, String> stringStringTuple2)
              throws Exception {
            return new Tuple2<>(stringStringTuple2._2,stringStringTuple2._1);
          }
        }
    );
    JavaPairRDD<String, Tuple2<String, String>> childGrand=parentChild.join(childParent);
    JavaPairRDD<String, Tuple2<String, String>> grandChild=childParent.join(parentChild);
    JavaRDD<Tuple2<String,String>>result=grandChild.map(
        new Function<Tuple2<String, Tuple2<String, String>>, Tuple2<String, String>>() {
          @Override
          public Tuple2<String, String> call(
              Tuple2<String, Tuple2<String, String>> stringTuple2Tuple2) throws Exception {
            System.out.println(new Tuple2<>(stringTuple2Tuple2._2._1,stringTuple2Tuple2._2._2));
            return new Tuple2<>(stringTuple2Tuple2._2._2,stringTuple2Tuple2._2._1);
          }
        }
    );
    // 简化版
    // System.out.println(childGrand.values());
    JavaRDD<Tuple2<String, String>> res=childGrand.values();
    return res;
     // return result;

    // JavaPairRDD<String,String> childParent=lines.mapToPair(
    //     v->{
    //       if(v.matches("child parent")){
    //         return null;
    //       }
    //       String []data=v.split(" ");
    //       if (data.length != 2) {
    //         return null;
    //       }
    //       return new Tuple2<>(data[0],data[1]);
    //
    //     }
    // ).filter(v -> v != null).cache();
    // //反过来 作为某人和他的儿子
    //
    // JavaPairRDD<String,String>parentChild=childParent.mapToPair(v->new Tuple2(v._2, v._1));
    // JavaPairRDD<String, Tuple2<String, String> >  joinRdd = parentChild.join(childParent);
    // //取第二个值的第一个作为孙子
    // //取第二个值的第二个作为爷爷
    // JavaRDD <Tuple2<String, String>> result = joinRdd.map(
    //     new Function<Tuple2<String, Tuple2<String, String>>,Tuple2<String, String>>() {
    //       @Override
    //       public Tuple2<String, String> call(
    //           Tuple2<String, Tuple2<String, String>> joinRdd) throws Exception {
    //         String key = joinRdd._1();
    //         String value1 = joinRdd._2._1;
    //         String value2 = joinRdd._2._2;
    //         return new Tuple2<>(value1,value2);
    //       }
    //
    //   //     @Override
    //   // public String call(Tuple2<String, String> joinRdd) throws Exception {
    //   //
    //   // }
    // });
    //
    // //     joinRdd.map(v->new Tuple2<>(v._2._1, v._2._2))
    // //     .sortByKey(true)
    // //     .collect();
    // //
    // // // JavaRDD<Tuple2<String, String>> result=sc.parallelize(childGrand);
    // // return childGrand.forEach(v -> (v._1 + " " + v._2));
    //
    //
    // return result;
  }
}
