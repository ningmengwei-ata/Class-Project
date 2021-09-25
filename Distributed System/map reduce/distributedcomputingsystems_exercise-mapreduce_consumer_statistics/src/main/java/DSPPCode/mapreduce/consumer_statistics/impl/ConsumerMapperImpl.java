package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerMapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ConsumerMapperImpl extends ConsumerMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String [] info=value.toString().split("\t");
        String vipinfo=info[3];
        Consumer writeable;
        writeable=new Consumer();
        //是否vip
        writeable.setVip(vipinfo.equals("vip"));
        writeable.setId(info[0]);
        writeable.setMoney(Integer.parseInt(info[2]));
        context.write(new Text(vipinfo),writeable);
    }
}
