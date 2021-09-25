package DSPPCode.flink.digital_conversion.impl;

import DSPPCode.flink.digital_conversion.question.DigitalConversion;
import DSPPCode.flink.digital_conversion.question.DigitalWord;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import scopt.Zero;

public class DigitalConversionImpl extends DigitalConversion{
  public DataStream<String> digitalConversion(DataStream<Tuple1<String>> digitals){

//过滤小数
    DataStream<Tuple1<String>> step1=digitals.filter(
        new FilterFunction<Tuple1<String>>() {
          @Override
          public boolean filter(Tuple1<String> stringTuple1) throws Exception {
            String word=stringTuple1.f0;
            // System.out.println(word);
            return word.length()==1;
          }
        }
    );
    //法二 正则表达式过滤
    DataStream<Tuple1<String>> integer=digitals.filter(
        new FilterFunction<Tuple1<String>>() {
          @Override
          public boolean filter(Tuple1<String> stringTuple1) throws Exception {
            System.out.println(stringTuple1);
            //需要通过f0将数字取出来 stringTuple1 形如(1)
            return stringTuple1.f0.matches("[0-9]+");
          }
        }
    );

    //将整数转化为对应的单词
    DataStream<String> word=integer.map(
        new MapFunction<Tuple1<String>, String>() {
          @Override
          public String map(Tuple1<String> stringTuple1) throws Exception {
            String word=stringTuple1.f0;
             System.out.println(word);
            int index=Integer.parseInt(word);
            
            return DigitalWord.values()[index].getWord();

          }
        }
    );
    //法二
    DataStream<String> step2=step1.map(
        new MapFunction<Tuple1<String>, String>() {
          @Override
          public String map(Tuple1<String> stringTuple1) throws Exception {
            String number=stringTuple1.f0;
            DigitalWord []words=DigitalWord.values();
            for (DigitalWord word :words){
              // System.out.println(word);
            }
            String result=words[Integer.parseInt(number)].getWord();
            // System.out.print(words);
            // DigitalWord.ZERO.getWord();
            return result;
          }
        }
    );
     return word;
  }
}
