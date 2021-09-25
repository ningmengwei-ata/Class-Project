package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class MaxTemperatureReducerImpl extends MaxTemperatureReducer {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int maxTemper=Integer.MIN_VALUE;
        for(IntWritable s:values){
         maxTemper=Math.max(s.get(),maxTemper);
        }
        IntWritable ints=new IntWritable();
        ints.set(maxTemper);
        context.write(key,ints);
    }
}
