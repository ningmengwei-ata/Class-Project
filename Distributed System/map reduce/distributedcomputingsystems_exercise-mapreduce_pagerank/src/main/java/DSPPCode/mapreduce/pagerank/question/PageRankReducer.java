package DSPPCode.mapreduce.pagerank.question;


import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

abstract public class PageRankReducer extends Reducer<Text, ReducePageRankWritable, Text, NullWritable> {

  /**
   * TODO 请完成该抽象方法
   * <p>
   * 输出：
   * 输出文本仅包含网页名以及排名值
   * 如A 0.21436
   * <p>
   * 可借助ReducePageRankWritable类来实现
   */
  abstract public void reduce(Text key, Iterable<ReducePageRankWritable> values, Context context)
      throws IOException, InterruptedException;
}