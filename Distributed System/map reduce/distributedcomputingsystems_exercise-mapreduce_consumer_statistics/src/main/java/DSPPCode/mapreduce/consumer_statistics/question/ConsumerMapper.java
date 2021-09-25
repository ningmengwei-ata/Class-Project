package DSPPCode.mapreduce.consumer_statistics.question;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public abstract class ConsumerMapper extends
    Mapper<LongWritable, Text, Text, Consumer> {

  protected static final String VIP = "vip";
  protected static final String NON_VIP = "non-vip";

  /**
   * TODO 请完成该方法
   * <p>
   * 输入:
   * <p>
   * 每个 value 代表一条消费数据, 每条数据存在消费者id、消费时间、消费金额以及消费者的会员属性这四个字段。字段之间以制表符进行分割。
   * <p>
   * 如3819  2021-03-27 21:30  357 vip 代表id为3819的vip消费者在2021-03-27 21:30时刻消费了357元
   */
  abstract public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException;
}
