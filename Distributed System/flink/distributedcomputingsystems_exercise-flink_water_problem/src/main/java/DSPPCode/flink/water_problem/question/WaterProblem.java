package DSPPCode.flink.water_problem.question;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public abstract class WaterProblem {

  public void run(SourceFunction<String> source, String outputFile) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment
        .getExecutionEnvironment()
        .setParallelism(1);

    // 添加数据源
    DataStream<String> inputEventDataStream = env.addSource(source);
    // 处理事件流
    DataStream<String> outputEventDataStream = execute(inputEventDataStream);
    // 将结果保存到文件中
    outputEventDataStream.writeAsText(outputFile, FileSystem.WriteMode.OVERWRITE);

    env.execute();
  }

  /**
   * TODO 请完成该方法
   *
   * @param dataStream 事件流
   * @return 查询结果
   */
  public abstract DataStream<String> execute(DataStream<String> dataStream);


}
