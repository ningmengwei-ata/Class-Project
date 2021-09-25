package DSPPCode.flink.speed_measurement.impl;

import DSPPCode.flink.speed_measurement.question.SpeedKeyedProcessFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import java.util.ArrayList;
import java.util.List;

public class SpeedKeyedProcessFunctionImpl extends SpeedKeyedProcessFunction {
  private transient ValueState<Boolean> flagState;
  private transient ValueState<Boolean> timerState;
  private int count=0;
  List<String> time=new ArrayList<>();
  @Override
  public void open(Configuration parameters) throws Exception {
    ValueStateDescriptor<Boolean> flagDescriptor = new ValueStateDescriptor<>(
        "flag",
        Types.BOOLEAN);
    flagState = getRuntimeContext()
        .getState(flagDescriptor);
    ValueStateDescriptor<Boolean> timerDescriptor = new ValueStateDescriptor<>(
        "timer",
        Types.BOOLEAN);
    timerState = getRuntimeContext().getState(timerDescriptor);
  }

  @Override
  public void processElement(Tuple2<String, String> t, Context context, Collector<String> collector)
      throws Exception {
   // System.out.println(t);
   // System.out.println(context);
   //  System.out.println(collector);
    String[] times = new String[0];


    if(count%4==0||count%4==3)
    {
      time.add(t.f1);
      // times[count++]=t.f1;
    }
    System.out.println("time array");
    System.out.println(time);
    // if (count==4){
    //   count=0;
    // }
    times=time.toArray(new String[time.size()]);
    if(count%4==3){
      System.out.print(times);
      if(averageSpeed(times)>60){
        flagState.update(true);
      }
      time.clear() ;
    }
    System.out.println(count);
    System.out.println("*****");
if(count%4==2 || count%4==1)
{
  System.out.println(t.f1);
  if(Integer.parseInt(t.f1)>60){
    flagState.update(true);
  }
}
System.out.println(flagState.value());
if(flagState.value()!=null)
{
  if(flagState.value() &&count%4==3)
  {
    collector.collect(t.f0);
  }
}


count++;


  }

  @Override
  public float averageSpeed(String[] times) {

    String []time1=times[0].split(":");
    String []time2=times[1].split(":");
    float period=(Float.parseFloat(time2[0])-Float.parseFloat(time1[0]))*3600+(Float.parseFloat(time2[1])-Float.parseFloat(time1[1]))*60
        +(Float.parseFloat(time2[2])-Float.parseFloat(time1[2]));
    float speed= (float) (10.0/(period/3600.0));
    System.out.println("speed");
    System.out.print(speed);
    return speed;
  }
}
