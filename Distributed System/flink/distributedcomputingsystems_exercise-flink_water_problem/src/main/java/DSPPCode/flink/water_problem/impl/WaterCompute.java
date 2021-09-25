package DSPPCode.flink.water_problem.impl;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.python.antlr.op.In;
import java.util.HashMap;

public class WaterCompute extends KeyedProcessFunction<Integer, Tuple4<String,Integer,Integer,Integer>,String> {
  private transient HashMap<Integer, Integer> speed;
  private transient HashMap<Integer,Integer> water;
  private transient ValueState<Tuple3<Integer,Integer,Long>> recordState;
  public void open(Configuration parameters) throws Exception {
    //存水量
    // water = new HashMap<>();
    // waterState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("time-state",Integer.class));
    //水流速度
    // speed = new HashMap<>();
    // speedState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("speed-state",Integer.class));
    //当前时间
    // timeState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("time-state",Integer.class));
    ValueStateDescriptor<Tuple3<Integer,Integer,Long>>record=new ValueStateDescriptor<Tuple3<Integer, Integer, Long>>(
        "key", TypeInformation.of(new TypeHint<Tuple3<Integer,Integer,Long>>(){}));
    recordState=getRuntimeContext().getState(record);
  }
  @Override
  public void processElement(
      Tuple4<String, Integer, Integer, Integer> s, Context context,
      Collector<String> collector) throws Exception {
    Tuple3<Integer,Integer,Long>recordValue=recordState.value();
    if(s.f0.equals("false")){
      if(recordValue==null){
        recordState.update(new Tuple3<>(s.f2,s.f3,0L));
      }
      else{
        Long water_volume=recordValue.f2;
        Long stamp=recordValue.f0.longValue();
        Long speed=recordValue.f1.longValue();

        Long currentVolume=water_volume+(s.f2.longValue()-stamp)*speed;
        if(currentVolume<0L)
        {
          currentVolume=0L;
        }
        recordState.update(new Tuple3<>(s.f2,s.f3,currentVolume));
      }
    }
    else{
      if(recordValue!=null){
        Long water_volume=recordValue.f2;
        Long stamp=recordValue.f0.longValue();
        Long speed=recordValue.f1.longValue();

        Long currentVolume=water_volume+(s.f2.longValue()-stamp)*speed;
        if(currentVolume<0L)
        {
          currentVolume=0L;
        }
        String result=context.getCurrentKey()+","+s.f2+","+currentVolume;
        collector.collect(result);
      }
      else{
        String result=context.getCurrentKey()+","+s.f2+","+"0";
        collector.collect(result);

      }


    }

  }
}
