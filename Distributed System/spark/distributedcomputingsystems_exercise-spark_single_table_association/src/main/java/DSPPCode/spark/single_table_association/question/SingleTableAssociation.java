package DSPPCode.spark.single_table_association.question;

import java.io.Serializable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public abstract class SingleTableAssociation implements Serializable {

  private static final String MODE = "local";

  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> lines = sc.textFile(args[0]);
    JavaRDD<Tuple2<String, String>> associations = singleTableAssociation(lines);
    // 输出结果到文本文件中
    associations.saveAsTextFile(args[1]);
    sc.close();
  }

  /**
   * TODO 请完成该方法
   * <p>
   * 寻找哪些人存在祖父母关系
   *
   * @param lines 包含了输入文本文件数据的RDD
   * @return 包含了(孙子女姓名 ， 祖父母姓名)的RDD
   */
  public abstract JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines);
}
