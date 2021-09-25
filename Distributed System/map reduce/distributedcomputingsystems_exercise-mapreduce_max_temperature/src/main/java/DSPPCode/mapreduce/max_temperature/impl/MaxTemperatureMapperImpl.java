package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class MaxTemperatureMapperImpl extends MaxTemperatureMapper {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String []s=value.toString().split(" ");
        IntWritable temper=new IntWritable();
        temper.set(Integer.parseInt(s[1]));
        context.write(new Text(s[0]),temper);
    }
}
