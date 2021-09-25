package DSPPCode.mapreduce.consumer_statistics.question;

import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public abstract class ConsumerReducer extends
    Reducer<Text, Consumer, Text, NullWritable> {
//前两位是输入中传入的参数类型
  //后两位是输出的参数类型
  /**
   * TODO 请完成该方法
   * <p>
   * 输出: 会员属性 人数  消费金额总数，各字段间用制表符分隔。
   */
  protected abstract void reduce(Text key, Iterable<Consumer> values, Context context)
      throws IOException, InterruptedException;
}
