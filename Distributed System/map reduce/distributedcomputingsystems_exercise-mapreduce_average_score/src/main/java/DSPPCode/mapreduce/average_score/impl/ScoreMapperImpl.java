package DSPPCode.mapreduce.average_score.impl;
import DSPPCode.mapreduce.average_score.question.ScoreMapper;
import DSPPCode.mapreduce.average_score.question.Util;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
public class ScoreMapperImpl extends ScoreMapper {
    private static IntWritable num = new IntWritable();
    private final Text word = new Text();
    public void map(Object key, Text value, Context context)throws IOException, InterruptedException{
        String  line=value.toString();
        String[] list =line.split(",");
        System.out.print(list);
        //第一个值为学号 后三个值为对应的成绩
//        String s_no=list[0];
        for (int i=1;i<=3;i++){
            //课程名列表从0开始计数

            String cname= Util.getCourseName(i-1);
            word.set(cname);
            Integer num1=Integer.valueOf(list[i]);
            num.set(num1);
            context.write(word, num);
        }
    }
}
