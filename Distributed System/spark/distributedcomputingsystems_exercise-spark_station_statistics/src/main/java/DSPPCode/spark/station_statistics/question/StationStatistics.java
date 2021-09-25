package DSPPCode.spark.station_statistics.question;

import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple3;

public abstract class StationStatistics implements Serializable {

  private static final String MODE = "local";

  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> lines = sc.textFile(args[0]);
    JavaRDD<Tuple3<String, String, Integer>> statistics = stationStatistics(lines);
    statistics.saveAsTextFile(args[1]);
    sc.close();
  }

  /**
   * TODO 请完成该方法
   * <p>
   * 请在此方法中计算出每个人在每个基站停留的总时间（以秒为单位）
   *
   * @param lines 包含了输入文本文件数据的RDD
   * @return 包含了每个人在每个基站停留的总时间的RDD
   */
  public abstract JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines);
}
