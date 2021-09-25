package DSPPCode.spark.pagerank_top3.question;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.io.Serializable;

public abstract class PageRankTop3 implements Serializable {

  private static final String MODE = "local";

  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> text = sc.textFile(args[0]);
    JavaPairRDD<String, Double> top3 = getTop3(text, 100);
    top3.saveAsTextFile(args[1]);
    sc.close();
  }

  /**
   * TODO 请完成该方法
   * <p>
   * 请在此方法中计算出按rank值排名前三名的(id,rank)键值对
   *
   * @param text       包含了输入文本文件数据的RDD
   * @param iterateNum 迭代轮数
   * @return 前三名节点的  (网页名称, 该网页 rank 值)  键值对
   */
  public abstract JavaPairRDD<String, Double> getTop3(JavaRDD<String> text, int iterateNum);
}
