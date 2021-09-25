package DSPPCode.mapreduce.kmeans.question;


import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

abstract public class KMeansReducer extends Reducer<Text, Text, Text, NullWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输出:
   * <p>
   * 输出为聚类结果
   * 聚类结果每行由一个n维的数据点和数据点所属类别标签构成，数据点和数据点所属类别标签之间由制表符分隔
   * <p>
   * 如 0,0  1 表示二维数据点（0,0）所属类别为1
   */
  @Override
  abstract public void reduce(Text key, Iterable<Text> values, Context context)
      throws IOException, InterruptedException;
}