package DSPPCode.mapreduce.warm_up.impl;

import DSPPCode.mapreduce.warm_up.question.IntSumReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * 答案示例
 */
public class IntSumReducerImpl extends IntSumReducer {

  private IntWritable result = new IntWritable();

  @Override
  public void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException {
    int sum = 0;
    for (IntWritable val : values) {
      sum += val.get();
    }
    result.set(sum);
    context.write(key, result);
  }

}
