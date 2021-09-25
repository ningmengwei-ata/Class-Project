package DSPPCode.spark.broadcast_k_means.question;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class BroadcastKMeans implements Serializable {

  private static final String MODE = "local";



  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建RDD
    JavaRDD<String> allPoints = sc.textFile(args[0]);
    JavaRDD<String> initKPoints = sc.textFile(args[1]);

    // 循环次数
    int iterateNum = 20;

    // 从输入数据中获取所有的点
    JavaRDD<List<Integer>> points = allPoints
        .map(line -> Arrays.stream(line.split(",")).map(Integer::valueOf).collect(toList()));

    // 从输入数据中获取k个距离中心点的初始位置
    List<List<Double>> tmpKPoints = initKPoints
        .map(line -> Arrays.stream(line.split(",")).map(Double::valueOf).collect(toList()))
        .collect();

    List<List<Double>> kPoints = new ArrayList<>(tmpKPoints);

    // 执行iterateNum次迭代计算
    for (int iter = 1; iter <= iterateNum - 1; iter++) {
      // 把聚类中心点设为广播变量
      Broadcast<List<List<Double>>> broadcastKPoints = createBroadcastVariable(sc, kPoints);

      // 计算距离最近的聚类中心
      JavaPairRDD<Integer, Tuple2<List<Integer>, Integer>> closest = getClosestPoint(points,
          broadcastKPoints);

      // 按类别号标识聚合，并计算新的聚类中心
      List<List<Double>> newPoints = getNewPoints(closest);

      // 将旧的聚类中心替换为新的聚类中心
      for (int i = 0; i < kPoints.size(); i++) {
        kPoints.set(i, newPoints.get(i));
      }
    }

    // 计算最终的聚类结果
    Broadcast<List<List<Double>>> broadcastKPoints = createBroadcastVariable(sc, kPoints);
    JavaPairRDD<List<Integer>, Integer> clusterResult = points
        .mapToPair(p -> new Tuple2<>(p, closestPoint(p, broadcastKPoints)));

    // 保存结果
    clusterResult.map(p -> p._1.get(0) + "," + p._1.get(1) + " " + p._2).saveAsTextFile(args[2]);

    sc.close();
  }

  // 计算距离最近的聚类中心
  public JavaPairRDD<Integer, Tuple2<List<Integer>, Integer>> getClosestPoint(
      JavaRDD<List<Integer>> points, Broadcast<List<List<Double>>> broadcastKPoints) {
    return points
        .mapToPair(p -> new Tuple2<>(closestPoint(p, broadcastKPoints), new Tuple2<>(p, 1)));
  }

  // 按类别号标识聚合，并计算新的聚类中心
  public List<List<Double>> getNewPoints(
      JavaPairRDD<Integer, Tuple2<List<Integer>, Integer>> closest) {
    return closest
        .reduceByKey((t1, t2) -> {
          // 计算两个点的和，并累加数据点个数
          return new Tuple2<>(addPoints(t1._1, t2._1), t1._2 + t2._2);
        })
        .map(t -> {
          List<Double> newPoint = new ArrayList<>();
          // 每个维度的和值除以数据点个数得到每个维度的均值
          for (int i = 0; i < t._2._1.size(); i++) {
            newPoint.add(t._2._1.get(i).doubleValue() / t._2._2);
          }
          return newPoint;
        })
        .collect();
  }

  // 计算两个的点距离的平方
  public Double distanceSquared(List<Integer> p1, List<Double> p2) {
    double sum = 0.0;
    for (int i = 0; i < p1.size(); i++) {
      sum += Math.pow(p1.get(i).doubleValue() - p2.get(i), 2);
    }
    return sum;
  }

  // 计算两个点的和
  public List<Integer> addPoints(List<Integer> p1, List<Integer> p2) {
    List<Integer> newPoint = new ArrayList<>();
    for (int i = 0; i < p1.size(); i++) {
      newPoint.add(p1.get(i) + p2.get(i));
    }
    return newPoint;
  }

  /**
   * TODO 请完成该方法
   * <p>
   * 请在此方法中实现选择距离p点最近的聚类中心点的功能
   *
   * @param p       p点
   * @param kPoints 聚类中心点
   * @return 距离中心点中，距离p最近的那个点的下标
   */
  public abstract Integer closestPoint(List<Integer> p,
      Broadcast<List<List<Double>>> kPoints);

  /**
   * TODO 请完成该方法
   * <p>
   * 请在次方法中创建广播变量
   *
   * @param localVariable 本地变量
   * @return 广播变量
   */
  public abstract Broadcast<List<List<Double>>> createBroadcastVariable(JavaSparkContext sc,
      List<List<Double>> localVariable);
}
