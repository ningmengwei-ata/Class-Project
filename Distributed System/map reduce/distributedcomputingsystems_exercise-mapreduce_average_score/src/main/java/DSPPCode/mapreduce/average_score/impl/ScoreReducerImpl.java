package DSPPCode.mapreduce.average_score.impl;
import DSPPCode.mapreduce.average_score.question.ScoreReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ScoreReducerImpl extends ScoreReducer {
    private IntWritable result=new IntWritable();
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        double avg=0;
        double sum=0;
        double num=0;
        for (IntWritable value:values){
            sum+=value.get();
            num+=1;
        }
        avg=sum/num;
        Integer avgs=(int)avg;
        result.set(avgs);
        context.write(key,result);
    }
}
