package DSPPCode.mapreduce.kmeans.question;


import DSPPCode.mapreduce.kmeans.impl.KMeansMapperImpl;
import DSPPCode.mapreduce.kmeans.impl.KMeansReducerImpl;
import DSPPCode.mapreduce.kmeans.question.utils.CentersOperation;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.net.URI;

public class KMeansRunner extends Configured implements Tool {

  // 判断是否满足迭代终止条件
  public static boolean compareResult = false;
  // 设定的阈值
  public static double sigma;
  // 从0开始记录迭代步数
  public static int iteration = 0;

  // 配置项中用于记录当前迭代步数的键
  public static final String ITERATION = "1";

  @Override
  public int run(String[] args) throws Exception {

    getConf().setInt(KMeansRunner.ITERATION, iteration);
    Job job = Job.getInstance(getConf(), getClass().getSimpleName());
    // 设置程序的类名
    job.setJarByClass(getClass());

    // 设置数据的输入输出路径
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1] + iteration));

    // 设置map方法及其输出键值对数据类型
    job.setMapperClass(KMeansMapperImpl.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    // 将聚类中心集通过分布式缓存广播出去
    if (iteration == 0) {
      // 第一次迭代时的聚类中心集
      job.addCacheFile(new Path(args[2]).toUri());
    } else {
      // 广播上一次迭代输出的聚类中心集
      job.addCacheFile(new Path(args[1] + (iteration - 1)).toUri());
    }

    // 最后一次迭代输出的是聚类结果，不需要再计算新的聚类中心
    if (!compareResult) {
      job.setReducerClass(KMeansReducerImpl.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(NullWritable.class);
    } else {
      job.setNumReduceTasks(0);
    }

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public int mainRun(String[] args) throws Exception {
    int exitCode = 0;
    sigma = Double.parseDouble(args[3]);
    // 执行指定次数的迭代，在最后一次迭代时输出聚类结果
    while (!compareResult) {
      exitCode = ToolRunner.run(new KMeansRunner(), args);
      if (exitCode == -1) {
        break;
      }
      //判断迭代前后聚类中心之间的距离是否小于阈值
      if (iteration == 0) {
        compareResult = CentersOperation.compareCenters(args[2], args[1] + iteration);
      } else {
        compareResult = CentersOperation.compareCenters(args[1] + (iteration - 1), args[1] + iteration);
      }
      iteration++;
    }
    ToolRunner.run(new KMeansRunner(), args);
    return exitCode;
  }

}