package DSPPCode.spark.moving_averages.impl;

import DSPPCode.spark.moving_averages.question.MovingAverages;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MovingAveragesImpl extends MovingAverages {
//计算每个时间的移动平均值就需要获取到其它的时刻的值
//   每次的输入只有一个时刻的值
//   这题的矛盾就在这，因为你只能拿到一个时刻的值
// 把这个值分散到需要该值的时刻上去，然后再进行汇总

  @Override
  public JavaRDD<String> movingAverages(JavaRDD<String> lines) {
    //获取时间序列中时间点的最大值
    long length = lines.count();
    JavaPairRDD<Integer,Integer>list=lines.flatMapToPair(
        new PairFlatMapFunction<String, Integer, Integer>() {
          @Override
          public Iterator<Tuple2<Integer, Integer>> call(String s) throws Exception {
            //去掉前后的[和]
            String[] array=s.substring(1,s.length()-1).split(",");
            Integer time=Integer.parseInt(array[0]);
            Integer value=Integer.parseInt(array[1]);
            List<Tuple2<Integer,Integer>> timeValue=new ArrayList<>();
            timeValue.add(new Tuple2<>(time,value));
            //要有移动 k从1开始 可以移动两步
            for(int k=1;k<=2;k++){
              int left=time-k;
              int right=time+k;
              if(left>=1){
                timeValue.add(new Tuple2<>(time,left));
                // timeValue.add(new Tuple2<>(left,value));
              }
              if(right<=length){
                timeValue.add(new Tuple2<>(time,right));
                // timeValue.add(new Tuple2<>(right,value));
              }

            }
            System.out.println(timeValue);
            return timeValue.iterator();
          }
        }
    );
    JavaRDD<String>result=list.groupByKey().map(
        new Function<Tuple2<Integer, Iterable<Integer>>, String>() {
          @Override
          public String call(Tuple2<Integer, Iterable<Integer>> integerIterableTuple2)
              throws Exception {
            int sum=0;
            int num=0;
            int avg=0;
           System.out.println(integerIterableTuple2);
            for(int i:integerIterableTuple2._2){
              sum+=i;
              num++;

            }
            avg=sum/num;
            String s="["+integerIterableTuple2._1+","+avg+"]";
            System.out.println(s);
            return s;
          }
        }
    );
    return result;

    //法二
    // int maxline=(int)lines.count();
    // int window_size=5;
    // JavaPairRDD<String,Integer> ids =
    //     lines.mapToPair(
    //         new PairFunction<String,String ,Integer>(){
    //
    //
    //           @Override
    //           public Tuple2<String, Integer> call(String s) throws Exception {
    //             s=s.replace("[","");
    //             s=s.replace("]","");
    //             String[]tokens=s.split(",");
    //
    //             return new Tuple2<>(tokens[0],Integer.valueOf(tokens[1]));
    //           }
    //         }
    //     ).cache();
    // JavaPairRDD <Integer,Integer> counts=lines.flatMapToPair(
    //     new PairFlatMapFunction<String, Integer, Integer>() {
    //       @Override
    //       public Iterator<Tuple2<Integer, Integer>> call(String s) throws Exception {
    //         s=s.replace("[","");
    //         s=s.replace("]","");
    //         String[]tokens=s.split(",");
    //         ArrayList<Tuple2<Integer, Integer>> list = new ArrayList<Tuple2<Integer, Integer>>();
    //         for(int i=0;i<window_size;i++){
    //           if((i-2+Integer.parseInt(tokens[0]))>=1&&(i-2+Integer.parseInt(tokens[0]))<=5)
    //           {
    //             list.add(new Tuple2<>(i-2+Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1])));
    //           }
    //
    //           // if((i-2+Integer.parseInt(tokens[0]))<1)
    //           //   break;
    //         //比如如果当前的tokens[0]为4 那么就从4-2=2算起一直算到5所对应的value
    //
    //         }
    //         return list.iterator();
    //       }
    //     }
    // ).cache();
    // int count=0;
    // JavaRDD<String> res=counts.groupByKey().flatMap(
    //     new FlatMapFunction<Tuple2<Integer, Iterable<Integer>>, String>() {
    //       @Override
    //       public Iterator<String> call(Tuple2<Integer, Iterable<Integer>> s)
    //           throws Exception {
    //         int sum=0;
    //         int count=0;
    //
    //
    //         for (int i:s._2){
    //           sum+=i;
    //           count++;
    //         }
    //         String []result=new String[]{String.format("[%d,%d]",s._1,sum/count)};
    //         // if (s._1<1||s._1>5)
    //         // {
    //         //   result=new String[]{};
    //         // }
    //         return Arrays.asList(result).iterator();
    //       }
    //     }
    // );
    //
    // return res;
  }
}
