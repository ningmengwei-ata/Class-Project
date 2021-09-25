package DSPPCode.mapreduce.pagerank.impl;

import DSPPCode.mapreduce.pagerank.question.PageRankMapper;
import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.pagerank.question.ReducePageRankWritable;
import DSPPCode.mapreduce.pagerank.question.utils.Rank;
import DSPPCode.mapreduce.pagerank.question.utils.RanksOperation;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import java.io.IOException;
import java.util.List;

public class PageRankMapperImpl extends PageRankMapper {
    //第一轮迭代从cachefile读取网页初始排名 然后转换成A 1.0 B 1.0 D 1.0
    //从第二轮迭代开始都正常得分析value得到对应网页排名和链接信息
    @Override
 //步骤1：确定输入键值对[K1,V1]的数据类型为[LongWritable,Text]，确定输出键值对[K2,V2]的数据类型为[Text,ReducePageRankWritable]
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //步骤2：编写处理逻辑将[K1,V1]转换为[K2,V2]并输出
        //以空格切分 获取网页链接关系
      String []pageInfo=value.toString().split(" ");
      //初始化链出网页的排名值
      double pageRank=0.0;
      int iteration=context.getConfiguration().getInt(PageRankRunner.ITERATION,0);
//      第一轮
      if (iteration==0){
          String path=context.getCacheFiles()[0].toString();
          //      System.out.println(pageRank);
//          可以看到 utils下的ranksoperation下的getRanks函数的功能为从指定路径读取网页排名
//          网页排名数据路径
          List<Rank> pagerank=RanksOperation.getRanks(path,true);
//          遍历网页链接排名 找到当前网页对应的排名
          for (Rank rank : pagerank) {
//              System.out.println(pagerank.get(i).getPageName());
              if (rank.getPageName().equals(pageInfo[0])) {
                  pageRank = rank.getRank();
//                  System.out.println(pageRank);
              }
          }
//          出链接数即为链接排名中 除了第一个链出链接以外的所有
          int outLink=pageInfo.length-1;

//          转换成A 1.0 B 1.0 D 1.0的格式
//          输出一列 将他们加入到网页链接关系中 第一个网页链接是
//          所有网页权重按1.0计算 所以所有链向的网页都赋值为1
          StringBuilder result=new StringBuilder();
//          构造A 1.0
          result.append(pageInfo[0]).append(" ").append(pageRank).append(" ");
          System.out.println(result);
//          构造B 1.0 D 1.0
          for(int i=1;i< pageInfo.length;i++){
              result.append(pageInfo[i]).append(" ").append("1.0").append(" ");
          }
//          构造输出ReducePageRankWritable
          ReducePageRankWritable writableInfo;
//          保存网页信息并标识
//          在网页信息中保存之前构造好的包含 链出链接 排名值 相应链入链接 对应的权重(1.0)
          writableInfo=new ReducePageRankWritable();
          writableInfo.setTag(ReducePageRankWritable.PAGE_INFO);
          writableInfo.setData(String.valueOf(result));
          //以A作为key value 是A 1.0 B 1.0 D 1.0
          context.write(new Text(pageInfo[0]), writableInfo);
//        tag标识data保存的是贡献值还是网页信息
          ReducePageRankWritable writable;
          writable=new ReducePageRankWritable();
          //计算贡献值
          //贡献值为链出的排名值/链接数
          writable.setData(String.valueOf(pageRank/outLink));
          //设置对应标识
          writable.setTag(ReducePageRankWritable.PR_L);
          //对于每一个出站链接，输出贡献值
          for(int i=1;i<pageInfo.length;i+=1){
              //以输入的网页信息的网页名称为键进行输出
              context.write(new Text(pageInfo[i]),writable);
          }
      }
      else{
//          获取网页信息
          String []pageInfo1=value.toString().split(" ");
          double pageRank1=Double.parseDouble(pageInfo1[1]);
//          A 1.0 B 1.0 D 1.0 计算B和D 即为2
          int outLink1=(pageInfo1.length-2)/2;
          ReducePageRankWritable writable1;
          writable1=new ReducePageRankWritable();
//          计算贡献值并保存
          writable1.setData(String.valueOf(pageRank1/outLink1));
//          设置对应标识
          writable1.setTag(ReducePageRankWritable.PR_L);
//          对于每一个出站链接，输出贡献值
          for(int i=2;i<pageInfo1.length;i+=2){
              context.write(new Text(pageInfo1[i]),writable1);
          }
//        value中保存的信息如A 1.0 B 1.0 D 1.0
          writable1=new ReducePageRankWritable();
          writable1.setData(value.toString());
          writable1.setTag(ReducePageRankWritable.PAGE_INFO);
//          以输入的网页信息的网页名称为key进行输出
          context.write(new Text(pageInfo1[0]),writable1);
      }
    }
}
