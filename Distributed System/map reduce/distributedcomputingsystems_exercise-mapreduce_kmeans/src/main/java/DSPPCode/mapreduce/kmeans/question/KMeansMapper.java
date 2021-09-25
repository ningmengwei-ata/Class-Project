package DSPPCode.mapreduce.kmeans.question;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

abstract public class KMeansMapper extends Mapper<LongWritable, Text, Text, Text> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输入:
   * <p>
   * 输入由数据集和聚类中心集组成
   * 数据集每行由一个n维的数据点和数据点所属类别标签构成，数据点和数据点所属类别标签之间由制表符分隔
   * <p>
   * 如 0,0  1 表示二维数据点（0,0）所属类别为1
   * <p>
   * 聚类中心集中每行仅由一个n维的聚类中心构成
   * 如 1,2 表示聚类中心坐标为(1,2)
   */
  @Override
  abstract public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException;
}
