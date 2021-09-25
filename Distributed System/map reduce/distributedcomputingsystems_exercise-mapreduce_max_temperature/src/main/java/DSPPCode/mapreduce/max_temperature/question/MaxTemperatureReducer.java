package DSPPCode.mapreduce.max_temperature.question;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

abstract public class MaxTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输出:
   * <p>
   * 每个键值对代表一个年份的最高温度。
   * <p>
   * 如 1990 25 代表 1990 年份的最高温度为 25。
   */
  @Override
  abstract public void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException;
}