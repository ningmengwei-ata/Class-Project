package DSPPCode.mapreduce.kmeans.impl;

import DSPPCode.mapreduce.kmeans.question.KMeansReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansReducerImpl extends KMeansReducer {
    @Override
    //reduce计算新的聚类中心 将经过shuffle后产生的[类别号,{数据点1，数据点2...}]键值对转换为【新聚类中心，空值】键值对并输出
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<List<Double>>points=new ArrayList<>();
        for(Text text:values){
            String value=text.toString();
            List<Double> point=new ArrayList<>();
            for (String s:value.split(",")){
                point.add(Double.parseDouble(s));
            }
            points.add(point);
        }
        StringBuilder newCenter=new StringBuilder();
        //计算每个维度的平均值从而得到新的聚类中心
        for(int i=0;i<points.get(0).size();i++){
            double sum=0;
            for(List<Double>data : points){
                sum+=data.get(i);
            }
            //生成需要输出的数据
            newCenter.append(sum/points.size());
            newCenter.append(",");
            System.out.println(newCenter);
        }
        context.write(new Text(newCenter.toString()), NullWritable.get());
    }
}
