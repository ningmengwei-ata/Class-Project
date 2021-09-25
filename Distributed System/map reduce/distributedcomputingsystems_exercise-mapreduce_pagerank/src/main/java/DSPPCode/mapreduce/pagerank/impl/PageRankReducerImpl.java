package DSPPCode.mapreduce.pagerank.impl;

import DSPPCode.mapreduce.pagerank.question.PageRankReducer;
import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.pagerank.question.ReducePageRankWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class PageRankReducerImpl extends PageRankReducer {
    // 阻尼系数
//    必须这样定义为double类型 否则会出现assert中的最后几位小数出错
    private static final double D=0.85;
    @Override
//    步骤1：确定输出键值对[K2,V2]的数据类型为[Text,ReducePageRankWritable]，确定输出键值对[K3,V3]的数据类型为[Text,NullWritable]
    public void reduce(Text key, Iterable<ReducePageRankWritable> values, Context context) throws IOException, InterruptedException {
        // 从配置项中读取网页的总数
        int totalPage=context.getConfiguration().getInt(PageRankRunner.TOTAL_PAGE,0);
        // 从配置项中读取当前迭代步数
        int iteration=context.getConfiguration().getInt(PageRankRunner.ITERATION,0);
        double sum=0;
        String[] pageInfo =null;
//        System.out.println(values);
        for(ReducePageRankWritable value:values){
            String tag=value.getTag();
//            如果是贡献值则求和，否则以空格为分隔符切分后保存到pageInfo
            if(tag.equals(ReducePageRankWritable.PR_L)){
                sum+=Double.parseDouble(value.getData());
            }else if(tag.equals(ReducePageRankWritable.PAGE_INFO)){
                pageInfo=value.getData().split(" ");
            }
        }
        //计算排名值
        double pageRank=(1-D)/totalPage+D*sum;
//        System.out.println(pageInfo);
        //pageInfo[1]为网页对应的排名
        //更新网页信息中的排名值
        pageInfo[1]=String.valueOf(pageRank);
//        System.out.println(pageInfo);
        //最后一次迭代输出网页名和排名，其余输出网页信息
        StringBuilder result=new StringBuilder();
        if(iteration==(PageRankRunner.MAX_ITERATION-1)){
            result.append(pageInfo[0]).append(" ").append(String.format("%.5f",pageRank));
        }
        else{
            for(String data:pageInfo){
                result.append(data).append(" ");
            }
        }
        //[网页信息,空值]
        context.write(new Text(result.toString()), NullWritable.get());
    }
}
