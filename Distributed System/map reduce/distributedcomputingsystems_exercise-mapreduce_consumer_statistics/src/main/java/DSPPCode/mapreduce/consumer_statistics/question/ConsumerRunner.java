package DSPPCode.mapreduce.consumer_statistics.question;

import DSPPCode.mapreduce.consumer_statistics.impl.ConsumerMapperImpl;
import DSPPCode.mapreduce.consumer_statistics.impl.ConsumerReducerImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

public class ConsumerRunner extends Configured implements Tool {

  @Override
  public int run(String[] args) throws Exception {
    Job job = Job.getInstance(getConf(), getClass().getSimpleName());
    job.setJarByClass(getClass());

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setMapperClass(ConsumerMapperImpl.class);
    job.setReducerClass(ConsumerReducerImpl.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Consumer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(NullWritable.class);

    return job.waitForCompletion(true) ? 0 : 1;
  }
}
