package DSPPCode.flink.digital_conversion.question;


import DSPPCode.flink.digital_conversion.impl.DigitalPartitionerImpl;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public abstract class DigitalConversion {

  public void run(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(2);

    // 从文本中读取数据，并行度为1
    DataStreamSource<String> dataStreamSource = env.readTextFile(args[0]).setParallelism(1);
    // 将输入数据转换为Tuple
    DataStream<Tuple1<String>> tupleData = dataStreamSource.map(
        (MapFunction<String, Tuple1<String>>) Tuple1::new).returns(Types.TUPLE(Types.STRING));
    // 根据自定义的Partitioner对数据进行分区
    DataStream<Tuple1<String>> partData = tupleData
        .partitionCustom(new DigitalPartitionerImpl(), 0);

    DataStream<String> result = digitalConversion(partData);

    // 将结果保存到文件中
    result.writeAsText(args[1]);
    env.execute(getClass().getName());
  }

  /**
   * TODO 请完成该方法
   *
   * @param digitals 输入的数字
   * @return 转换结果
   */
  public abstract DataStream<String> digitalConversion(DataStream<Tuple1<String>> digitals);
}
