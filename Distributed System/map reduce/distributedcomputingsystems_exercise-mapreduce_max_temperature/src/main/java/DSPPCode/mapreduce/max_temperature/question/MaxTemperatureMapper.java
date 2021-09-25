package DSPPCode.mapreduce.max_temperature.question;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

abstract public class MaxTemperatureMapper extends Mapper<Object, Text, Text, IntWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输入:
   * <p>
   * 每个 value 包含一个年份和一个温度，年份和温度之间以空格分隔。
   * <p>
   * 如 1990 21 代表年份为 1990，温度为 21。
   */
  @Override
  abstract public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException;
}