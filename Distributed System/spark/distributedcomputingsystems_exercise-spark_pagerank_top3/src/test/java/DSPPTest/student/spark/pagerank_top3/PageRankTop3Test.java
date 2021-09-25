package DSPPTest.student.spark.pagerank_top3;

import DSPPCode.spark.pagerank_top3.impl.PageRankTop3Impl;
import DSPPCode.spark.pagerank_top3.question.PageRankTop3;
import DSPPTest.student.TestTemplate;
import org.junit.Before;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class PageRankTop3Test extends TestTemplate {

  // 输入文件基础路径
  private static final String BASE_INPUT_PATH = root + "/spark/pagerank_top3/input";
  // 作业的输出路径
  private static final String OUTPUT_PATH = outputRoot + "/spark/pagerank_top3";
  // 作业的输出结果路径
  private static final String OUTPUT_FILE = OUTPUT_PATH + "/part-00000";
  // 答案的基础路径
  private static final String BASE_ANSWER_FILE = root + "/spark/pagerank_top3/answer";

  // 删除旧输出
  @Before
  public void deleteOutput() {
    deleteFolder(OUTPUT_PATH);
  }


  @Test
  public void test() throws Exception {

    // 执行
    String[] args = {BASE_INPUT_PATH, OUTPUT_PATH};
    PageRankTop3 pageRankTop3 = new PageRankTop3Impl();
    pageRankTop3.run(args);

    // 检验结果
    verifyKV(readFile2String(OUTPUT_FILE), readFile2String(BASE_ANSWER_FILE));

    System.out.println("恭喜通过~");
  }

}
