package DSPPCode.mapreduce.warm_up.question;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

abstract public class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {


  /**
   * TODO 请完成该方法
   * <p>
   * 输入：
   * <p>
   * 单行文本
   */
  @Override
  abstract public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException;

}
