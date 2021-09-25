package DSPPCode.mapreduce.kmeans.impl;

import DSPPCode.mapreduce.kmeans.question.KMeansMapper;
import DSPPCode.mapreduce.kmeans.question.KMeansRunner;
import DSPPCode.mapreduce.kmeans.question.utils.CentersOperation;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import py4j.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansMapperImpl extends KMeansMapper {
    private List<List<Double>>centers=new ArrayList<>();
    @Override
    //将输入键值对根据分布式缓存提供的聚类中心集 计算数据点所属类别 以【类别号，数据点】键值对形式输出
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line=value.toString();
        List<Double> point =new ArrayList<>();
        //double会出现输出结果精确度不对的情况
        int centerIndex=0;
//        double centerIndex=0;
        double minDistance=Double.MAX_VALUE;
//        int iteration=context.getConfiguration().getInt(KMeansRunner.ITERATION,0);
        if(centers.size()==0){
            //第一次需要获取广播的聚类中心集路径
            String centersPath=context.getCacheFiles()[0].toString();
            //将聚类中心加载到集合centers中
            centers= CentersOperation.getCenters(centersPath,true);
        }
        //解析数据点
       String [] dimensions=line.split("[,\t]");
        for (int i=0;i<dimensions.length-1;i++){
            point.add(Double.parseDouble(dimensions[i]));
        }
//        boolean b= line.contains("\\t");
//        if (b){
//            String[] list =line.split("\\t");
//            String[] positions =list[0].split(",");
//            Double x=Double.valueOf(positions[0]);
//            Double y=Double.valueOf(positions[1]);
//            point.add(x);
//            point.add(y);
//        }
        //遍历聚类中心集并计算与数据点的距离
        for(int i=0;i<centers.size();i++){
            double distance=0;
            List<Double>center=centers.get(i);
            //计算数据点与当前聚类中心之间的距离
            for (int j=0;j<center.size();j++){
                distance+=Math.pow((point.get(j)-center.get(j)),2);
            }
            distance=Math.sqrt(distance);
            //如果距离小于当前记录的最小距离则将数据点分配给当前聚类中心（用类别号识别）
            if(distance<minDistance){
                minDistance=distance;
//                centerIndex=i+1;
                centerIndex=i;
            }
        }
        //从输入值中截取数据点
        String pointData=value.toString().split("\t")[0];
        //判断是否满足迭代终止条件compareResult
        if(KMeansRunner.compareResult){
            context.write(new Text(pointData),new Text(String.valueOf(centerIndex)));
        }
        else{
            //输出以类别号为键，数据点为值的键值对
            //不是输出value的所有值 应当输出的是value的第一块就是数据点的部分 所以需要事先进行split操作
            context.write(new Text(String.valueOf(centerIndex)),new Text(pointData));
        }
    }
}

