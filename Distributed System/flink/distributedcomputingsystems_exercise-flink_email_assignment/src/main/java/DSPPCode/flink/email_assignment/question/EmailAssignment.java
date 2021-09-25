package DSPPCode.flink.email_assignment.question;

import java.io.Serializable;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public abstract class EmailAssignment implements Serializable {

  public void run(String inputFile, String outputFile) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 添加数据源以及进行计算
    DataStreamSource<String> dataStreamSource = env.readTextFile(inputFile);
    DataStream<String> result = processRequest(parseInput(dataStreamSource));
    // 将结果保存到文件中
    result.writeAsText(outputFile);
    env.execute(getClass().getName());
  }

  /**
   * TODO 请完成该方法
   *
   * @param requests 输入请求流
   * @return 请求的执行结果流
   */
  public abstract DataStream<String> processRequest(DataStream<Request> requests);

  /**
   * 解析文本输入
   * @param text 文本输入
   * @return Request 的 DataStream
   */
  private DataStream<Request> parseInput(DataStream<String> text) {
    return text.map(t -> {
      String[] elements = t.split("\\s+");
      return new Request(RequestType.valueOf(elements[0]),
          Integer.parseInt(elements[1]),
          new Department(Integer.parseInt(elements[2]), Integer.parseInt(elements[3])),
          elements[4]);
    });
  }

}