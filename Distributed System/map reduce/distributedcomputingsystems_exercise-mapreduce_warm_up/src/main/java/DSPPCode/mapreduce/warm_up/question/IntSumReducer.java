package DSPPCode.mapreduce.warm_up.question;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

abstract public class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输出：单词和频数的键值对
   */
  @Override
  abstract public void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException;

}
