package DSPPCode.spark.topk_power.question;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

public abstract class TopKPower implements Serializable {
  private static final String MODE = "local";

  public int run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> lines = sc.textFile(args[0]);
    int topKPower = topKPower(lines);
    sc.close();
    return topKPower;
  }

  /**
   * TODO 请完成该方法
   *
   * <p>请在此方法中计算出阿D能获得的最大价值
   *
   * @param lines 包含了输入文本文件数据的RDD
   * @return 包含了阿D能获得的最大价值
   */
  public abstract int topKPower(JavaRDD<String> lines);
}
