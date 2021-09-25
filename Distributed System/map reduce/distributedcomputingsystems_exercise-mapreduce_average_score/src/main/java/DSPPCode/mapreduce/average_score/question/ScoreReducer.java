package DSPPCode.mapreduce.average_score.question;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

abstract public class ScoreReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输出:
   * <p>
   * 每个键值对代表一门课的平均成绩.
   * <p>
   * 如 { Mathematical analysis => 80 } 代表数学分析的平均成绩为80分.
   */
  @Override
  abstract public void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException;

}
