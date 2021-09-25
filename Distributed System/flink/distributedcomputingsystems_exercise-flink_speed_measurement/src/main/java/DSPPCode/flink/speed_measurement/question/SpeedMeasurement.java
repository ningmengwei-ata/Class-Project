package DSPPCode.flink.speed_measurement.question;

import DSPPCode.flink.speed_measurement.impl.SpeedKeyedProcessFunctionImpl;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SpeedMeasurement {

  public void run(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 添加数据源
    DataStreamSource<String> dataStreamSource = env.readTextFile(args[0]);
    // 将数据源中的数据解析成[车牌号, 时刻]或[车牌号, 速度]的键值对
    DataStream<Tuple2<String, String>> inputStream =
        dataStreamSource.map(new MapFunction<String, Tuple2<String, String>>() {
          @Override
          public Tuple2<String, String> map(String s) throws Exception {
            String[] splits = s.split(" ");
            return new Tuple2<>(splits[0], splits[1]);
          }
        });
    // 按车牌号聚合键值对
    KeyedStream<Tuple2<String, String>, String> keyedStream = inputStream.keyBy(x -> x.f0);
    // 统计是否超速
    DataStream<String> overSpeedStream = keyedStream.process(new SpeedKeyedProcessFunctionImpl());
    // 将超速的车牌号写入到文件中
    overSpeedStream.writeAsText(args[1]);
    env.execute(getClass().getName());
  }
}