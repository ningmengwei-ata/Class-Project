package DSPPCode.mapreduce.warm_up.impl;

import DSPPCode.mapreduce.warm_up.question.TokenizerMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 答案示例
 */
public class TokenizerMapperImpl extends TokenizerMapper {

  private static final IntWritable ONE = new IntWritable(1);

  private final Text word = new Text();

  private final Pattern pattern = Pattern.compile("\\W+");

  @Override
  public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException {
    StringTokenizer itr = new StringTokenizer(value.toString());
    while (itr.hasMoreTokens()) {
      String str = itr.nextToken();
      str = pattern.matcher(str).replaceAll("");
      word.set(str);
      context.write(word, ONE);
    }
  }

}
