package DSPPCode.flink.grade_point.question;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public abstract class GradePoint {

  public void run(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);
    // 添加数据源以及计算总学分及成绩
    DataStreamSource<String> dataStreamSource = env.readTextFile(args[0]);
    DataStream<Tuple3<String, Integer, Float>> result = calculate(dataStreamSource);
    // 将结果保存到文件中
    result.writeAsText(args[1]);
    env.execute(getClass().getName());
  }

  /**
   * TODO 请完成该方法
   *
   * @param text 成绩信息
   * @return 每个学生总学分及平均学分绩点
   */
  public abstract DataStream<Tuple3<String, Integer, Float>> calculate(DataStream<String> text);

}
