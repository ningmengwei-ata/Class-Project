package DSPPCode.flink.email_assignment.impl;

import DSPPCode.flink.email_assignment.question.Department;
import DSPPCode.flink.email_assignment.question.EmailAssignment;
import DSPPCode.flink.email_assignment.question.Request;
import DSPPCode.flink.email_assignment.question.RequestType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAssignmentImpl extends EmailAssignment {



  @Override
  public DataStream<String> processRequest(DataStream<Request> requests) {
    List<String> alias_exist=new ArrayList<>();
    List<List<String>> hs=new ArrayList<>();
    DataStream<String> result=requests.keyBy(Request::getDepart).flatMap(
        new FlatMapFunction<Request, String>() {
          @Override
          public void flatMap(Request request, Collector<String> collector) throws Exception {
            //名字格式不对
           // System.out.println(request);
           //  System.out.println("*******");
            System.out.println(hs);
            String validateStr = "[a-zA-Z0-9\\\\_]+";
            boolean rs;
            rs = Pattern.matches(validateStr, request.getAlias());
           if( request.getAlias().length()<5 ||request.getAlias().length()>11 ||!rs){
             System.out.print("name failure");
             collector.collect("FAILURE");
             return;
           }
            if(request.getType()== RequestType.REVOKE){
              List<String> lists=new ArrayList<>();
              lists.add(String.valueOf(request.getId()));
              lists.add(request.getAlias());
              // lists.add(String.valueOf(request.getDepart()));
              lists.add(String.valueOf(request.getDepart().getFirstLevelCode()));
              lists.add(String.valueOf(request.getDepart().getSecondLevelCode()));
              System.out.println("*******");
              System.out.println(lists);
              if(hs.contains(lists)){
                System.out.print("revoke success");
                collector.collect("SUCCESS");
                hs.remove(lists);
              }

              else{
                System.out.print("revoke failure");
                collector.collect("FAILURE");
              }
            }
            if(request.getType()==RequestType.APPLY){
              for (List<String> s:hs){
               if(s.get(1).equals(request.getAlias()) && s.get(2).equals(String.valueOf(request.getDepart().getFirstLevelCode()))&& s.get(3).equals(String.valueOf(request.getDepart().getSecondLevelCode()))){
                 // System.out.println(request.getAlias());
                 System.out.println("apply failure");
                 collector.collect("FAILURE");
                 return;

               }

              }
              // Iterator iterator =hs.iterator();
              // while (iterator.hasNext()) {
              //   System.out.println(iterator);
              // }

              // if(hs.contains(request.getAlias())){
              //   System.out.println(request.getAlias());
              //   collector.collect("FAILURE");
              // }
              // else{
              //   collector.collect("SUCCESS");
              // }
              System.out.println("apply success");
              collector.collect("SUCCESS");
              List <String>element=new ArrayList<>();
              element.add(String.valueOf(request.getId()));
              element.add(request.getAlias());
              element.add(String.valueOf(request.getDepart().getFirstLevelCode()));
              element.add(String.valueOf(request.getDepart().getSecondLevelCode()));

              hs.add(element);
            }



           // hs.add(new Tuple2<>(request.getId(),request.getAlias()));

    //        if(request.getType()== RequestType.APPLY){
    //          collector.collect("FAILURE");
    //        }
    //         if(request.getType()== RequestType.REVOKE){
    //           collector.collect("SUCCESS");
    //         }
    //
    //         collector.collect("SUCCESS");
    //         // System.out.println(request.getType()== RequestType.APPLY);
    //         // System.out.println("******");
    //         for (String t:alias_exist){
    //           if(request.getType()== RequestType.APPLY){
    //           // if(t)
    //           }
    //         if(t==request.getAlias()){
    //
    //         }
    //         }
    //         alias_exist.add(request.getAlias());
    //         request.setAlias(request.getAlias());
    //         request.setDepart(request.getDepart());
    //         request.setId(request.getId());
    //         request.setType(request.getType());
    //         for (String t:alias_exist){
    //
    //         }
     }
      }
    );
    result.print();
    return result;
  }
}
