package DSPPCode.flink.speed_measurement.question;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import java.io.Serializable;

public abstract class SpeedKeyedProcessFunction extends
    KeyedProcessFunction<String, Tuple2<String, String>, String> implements Serializable {

  /**
   * TODO 请完成该方法
   * @param parameters
   * @throws Exception
   */
  @Override
  public abstract void open(Configuration parameters) throws Exception;

  /**
   * TODO 请完成该方法
   * @param t
   * @param context
   * @param collector
   * @throws Exception
   */
  @Override
  public abstract void processElement(Tuple2<String, String> t, Context context,
      Collector<String> collector) throws Exception;

  /**
   * TODO 请完成该方法
   *
   * @param times 包含一个进入A入口的时刻和一个离开B出口的时刻
   * @return 返回A入口到B出口区间内的平均速度，单位为km/h
   */
  public abstract float averageSpeed(String[] times);
}
