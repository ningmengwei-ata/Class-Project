package DSPPCode.mapreduce.pagerank.question;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

abstract public class PageRankMapper extends Mapper<LongWritable, Text, Text, ReducePageRankWritable> {

  /**
   * TODO 请完成该抽象方法
   * -
   * 输入：
   * 输入数据由两个文件构成，分别是网页链接关系和网页排名，文本中的第一列都为网页名，列与列之间用空格分隔
   * <p>
   * 网页链接关系文本中的其他列为出站链接
   * 如A B D 表示网页A链向网页B和D
   * <p>
   * 网页排名文本第二列为该网页的排名值
   * 如 A 1 表示网页A的排名为1
   * <p>
   * 可借助ReducePageRankWritable类来实现
   */
  abstract public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException;
}