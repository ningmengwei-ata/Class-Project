package DSPPCode.spark.moving_averages.question;

import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public abstract class MovingAverages implements Serializable {

  private static final String MODE = "local";

  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> lines = sc.textFile(args[0]);
    JavaRDD<String> timeSeries = movingAverages(lines);
    // 输出转换后的时间序列到文本文件中
    timeSeries.saveAsTextFile(args[1]);
    sc.close();
  }

  /**
   * TODO 请完成该方法
   *
   * <p>请在此方法中完成平滑移动平均
   */
  public abstract JavaRDD<String> movingAverages(JavaRDD<String> lines);
}
