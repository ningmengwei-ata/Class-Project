package DSPPTest.student.mapreduce.pagerank;


import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class PageRankTest extends TestTemplate {
  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath1 = root + "/mapreduce/pagerank/pages";
    String inputPath2 = root + "/mapreduce/pagerank/ranks";
    String outputPath = outputRoot + "/mapreduce/pagerank/";
    String outputFile = outputPath + "19/part-r-00000";
    String answerFile = root + "/mapreduce/pagerank/answer";

    // 删除旧的输出
    deleteFolder(outputPath);

    // 执行
    PageRankRunner pageRank = new PageRankRunner();
    String[] args = {inputPath1,outputPath,"4",inputPath2};
    int exitCode = pageRank.mainRun(args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser(" "));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }
}
