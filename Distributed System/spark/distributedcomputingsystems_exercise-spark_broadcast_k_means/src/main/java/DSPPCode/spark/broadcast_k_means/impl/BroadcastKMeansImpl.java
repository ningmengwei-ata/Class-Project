package DSPPCode.spark.broadcast_k_means.impl;

import DSPPCode.spark.broadcast_k_means.question.BroadcastKMeans;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static DSPPCode.spark.broadcast_k_means.question.BroadcastKMeans.*;

public class BroadcastKMeansImpl extends BroadcastKMeans {
  // 计算一群点中距离某个点最近的点的坐标

  @Override
  public Integer closestPoint(List<Integer> p, Broadcast<List<List<Double>>> kPoints) {
    Integer bestIndex=0;
    Double closest=Double.MAX_VALUE;
    for(int i=0;i<kPoints.getValue().size();i++){

      // Double dist= distanceSquared(p, kPoints.getValue().get(i));
      //.value()获取广播变量的值
      Double dist= distanceSquared(p, kPoints.value().get(i));
      if(dist<closest){
        bestIndex= i;
        closest=dist;
      }
    }
    return bestIndex;
  }

// 把聚类中心点设为广播变量
  @Override
  public Broadcast<List<List<Double>>> createBroadcastVariable(JavaSparkContext sc,
      List<List<Double>> localVariable) {
    //使用sc.broadcast创建广播变量
    return sc.broadcast(localVariable);
  }
}
