package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.HashSet;

public class ConsumerReducerImpl extends ConsumerReducer {

    @Override
    protected void reduce(Text key, Iterable<Consumer> values, Context context) throws IOException, InterruptedException {
        StringBuilder result=new StringBuilder();
        HashSet<String> consumer=new HashSet<>();
        long sum=0;
        for(Consumer value:values){
            String id=value.getId();
            consumer.add(id);
            int money= value.getMoney();
            sum+=money;

        }
        if(String.valueOf(key).equals("vip")){
            result.append("vip").append("\t");
        }
        else{
            result.append("non-vip").append("\t");
        }
        result.append(consumer.size()).append("\t").append(sum);


        context.write(new Text(result.toString()), NullWritable.get());
    }
}
