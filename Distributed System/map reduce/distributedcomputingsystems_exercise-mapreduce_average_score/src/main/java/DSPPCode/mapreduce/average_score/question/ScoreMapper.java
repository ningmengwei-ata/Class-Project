package DSPPCode.mapreduce.average_score.question;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

abstract public class ScoreMapper extends Mapper<Object, Text, Text, IntWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输入:
   * <p>
   * 每个 value 代表一名学生的成绩, 行内由 "," 隔开, 第一个代表学号, 随后三个依次代表三门成绩.
   * <p>
   * 如 10160001,98,80,75 代表 学号10160001的学生, 数学分析98分, 概率论80分, 实变函数75分.
   * <p>
   * 课程名通过 Util.getCourseName(i) 获得
   */
  @Override
  abstract public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException;

}
