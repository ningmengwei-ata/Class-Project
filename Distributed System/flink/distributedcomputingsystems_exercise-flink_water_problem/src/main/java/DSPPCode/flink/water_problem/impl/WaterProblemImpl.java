package DSPPCode.flink.water_problem.impl;

import DSPPCode.flink.water_problem.question.WaterProblem;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.util.Collector;
import org.python.antlr.op.In;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaterProblemImpl extends WaterProblem {

  // private static final HashMap<Integer, List<List>> tsmap = new HashMap<>();
  // private static final HashMap<Integer, List<Integer>> volumemap = new HashMap<>();

  // private static final List<List> tslist=new ArrayList<>();
  //  private static final List<Integer> volumes1=new ArrayList<>();
  // private static final List<List> list1=new ArrayList<>();
  @Override
  public DataStream<String> execute(DataStream<String> dataStream) {
    // System.out.println(dataStream);
    DataStream<Tuple4<String, Integer, Integer, Integer>> totuple = dataStream.flatMap(
        new FlatMapFunction<String, Tuple4<String, Integer, Integer, Integer>>() {
          @Override
          public void flatMap(String s,
              Collector<Tuple4<String, Integer, Integer, Integer>> collector) throws Exception {
            // System.out.println(s);
            String[] l = s.split(",");
            collector.collect(new Tuple4<>(l[0], Integer.parseInt(l[1]), Integer.parseInt(l[2]),
                Integer.parseInt(l[3])));
          }
        }
    );

    KeyedStream<Tuple4<String,Integer,Integer,Integer>,Integer> keystream=totuple.keyBy(x->x.f1);
    DataStream <String>overStream=keystream.process(new WaterCompute());
    overStream.print();
    return overStream;
  }
}

    // DataStream<String>result=totuple.keyBy(1).flatMap(
    //     new FlatMapFunction<Tuple4<String, Integer, Integer, Integer>, String>() {
    //       @Override
    //       public void flatMap(
    //           Tuple4<String, Integer, Integer, Integer> s,
    //           Collector<String> collector) throws Exception {
    //
    //           tsmap.computeIfAbsent(s.f1,);
    //           List<Integer> volume=new ArrayList<>();
    //           volume.add(0);
    //           volumemap.computeIfAbsent(s.f1,volume);
    //       }
    //     }
    // );
    // DataStream<String>result=totuple.keyBy(1).map(
    //     new MapFunction<Tuple4<String, Integer, Integer, Integer>, String>() {
    //       @Override
    //       public String map(
    //           Tuple4<String, Integer, Integer, Integer> s)
    //           throws Exception {
    //         if(s.f0=="false"){
    //           List<Integer> time_speed=new ArrayList<>();
    //           time_speed.add(s.f2);
    //           time_speed.add(s.f3);
    //           tslist.add(time_speed);
    //         }
    //         else{
    //           int numberOfElements = tslist.size();
    //           List <Integer>ts=tslist.get(numberOfElements - 1);
    //           volume+=(s.f2-ts.get(0))*ts.get(1);
    //           return String.format(String.valueOf(s.f1)+","+String.valueOf(s.f2)+","+String.valueOf(volume));
    //         }
    //       }
    //     }
    // );

    // DataStream<String>results=totuple.keyBy(1).flatMap(
    //     new FlatMapFunction<Tuple4<String, Integer, Integer, Integer>, String>() {
    //       @Override
    //       public void flatMap(
    //           Tuple4<String, Integer, Integer, Integer> s,
    //           Collector<String> collector) throws Exception {
    //
    //        if(s.f0.equals("false")){
    //
    //         List<Integer> time_speed=new ArrayList<>();
    //         time_speed.add(s.f2);
    //         time_speed.add(s.f3);
    //
    //         // System.out.println(list2);
    //         if(tsmap.get(s.f1)==null)
    //         {
    //           List<List> list2=new ArrayList<>();
    //
    //           list2.add(time_speed);
    //
    //           tsmap.put(s.f1,list2);
    //           return;
    //         }
    //
    //         List<List>use=tsmap.get(s.f1);
    //         use.add(time_speed);
    //         tsmap.put(s.f1,use);

    // List<List> list1=new ArrayList<>();

    // list1.add(time_speed);
    // list2.add(time_speed);

    // tsmap.put(s.f1,list2);
    // int t=volumemap.get(s.f1).size();
    // // if(t>1){
    // List<Integer> volume11=volumemap.get(s.f1);
    // // List<Integer>volumes1=new ArrayList<>();
    // int volume_previous=volumemap.get(s.f1).get(t-1);
    // System.out.print("volume_previous:");
    // System.out.println(volume_previous);
    // volume11.add(volume+volume_previous);
    // volumemap.put(s.f1,volume11);

    //          }
    //           System.out.print(s.f0);
    //
    //             System.out.print("****");
    //             // List<Integer>initial=new ArrayList<>();
    //             // initial.add(0);
    //             // volumemap.put(s.f1,initial);
    //             List<List>use=tsmap.get(s.f1);
    //             System.out.println("use");
    //             System.out.println(use);
    //             int numberOfElements = use.size();
    //             System.out.println("index");
    //             System.out.println(numberOfElements);
    //             // int indexs;
    //             // if(!volumemap.isEmpty()){
    //             //   indexs = volumemap.get(s.f1).size();
    //             // }
    //             // else{
    //             //   indexs=0;
    //             // }
    //
    //             if(numberOfElements>=1){
    //               List<Integer> ts=use.get(numberOfElements - 1);
    //               System.out.println(use);
    //
    //               System.out.println("&&&&&&&&");
    //               System.out.println(ts);
    //               System.out.println(s.f2);
    //               int volume;
    //               if(s.f0.equals("false")) {
    //                 volume = (s.f2 - ts.get(0)) * ts.get(1);
    //               }
    //               else {
    //                 List<Integer> last=use.get(numberOfElements - 2);
    //                volume=(s.f2-last.get(0)) *last.get(0);
    //               }
    //
    //                 List<Integer>volumes=new ArrayList<>();
    //                 volumes.add(volume);
    //
    //               if(volumemap.get(s.f1)==null)
    //               {
    //
    //                 volumemap.put(s.f1,volumes);
    //                 System.out.println("*******");
    //                 System.out.println(volumemap);
    //
    //               }
    //               else{
    //                int t=volumemap.get(s.f1).size();
    //                // if(t>1){
    //                  List<Integer> volume11=volumemap.get(s.f1);
    //                 // List<Integer>volumes1=new ArrayList<>();
    //                 int volume_previous=volumemap.get(s.f1).get(t-1);
    //               System.out.print("volume_previous:");
    //                 System.out.println(volume_previous);
    //                 int current_volume=volume+volume_previous;
    //                 System.out.print("current_volume:");
    //                 System.out.println(volume);
    //                 volume11.add(current_volume);
    //                 volumemap.put(s.f1,volume11);
    //
    //               System.out.println(volumemap);
    //               }
    //               if(s.f0.equals("true")){
    //
    //
    //               collector.collect(String.format(String.valueOf(s.f1)+","+String.valueOf(s.f2)+","+String.valueOf(volumemap.get(s.f1).get(0))));
    //             }
    //
    //
    //           }
    //
    //         }
    //       }
    //   );
    //   results.print();
    //  return results;
    // }



