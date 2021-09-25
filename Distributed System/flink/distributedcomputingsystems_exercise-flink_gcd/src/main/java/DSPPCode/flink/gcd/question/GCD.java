package DSPPCode.flink.gcd.question;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public abstract class GCD {
  public void run(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 添加数据源以及执行GCD计算
    DataStreamSource<String> dataStreamSource = env.readTextFile(args[0]);
    // 提取source中的记录形成元组
    DataStream<Tuple3<String, Integer, Integer>> inputStream =
        dataStreamSource.map(
            new MapFunction<String, Tuple3<String, Integer, Integer>>() {
              @Override
              public Tuple3<String, Integer, Integer> map(String s) throws Exception {
                String[] record = s.split(" ");
                return new Tuple3<>(
                    record[0], Integer.valueOf(record[1]), Integer.valueOf(record[2]));
              }
            });
    // 创建迭代算子
    IterativeStream<Tuple3<String, Integer, Integer>> iteration = inputStream.iterate(5000);
    // 通过迭代算子计算GCD
    DataStream<Tuple3<String, Integer, Integer>> result = calGCD(iteration);
    // 将结果保存到文件中
    result.writeAsText(args[1]);
    env.execute(getClass().getName());
  }

  /**
   * TODO 请完成该方法
   *
   * @param iteration 迭代算子
   * @return GCD计算结果
   */
  public abstract DataStream<Tuple3<String, Integer, Integer>> calGCD(IterativeStream<Tuple3<String, Integer, Integer>> iteration);
}
