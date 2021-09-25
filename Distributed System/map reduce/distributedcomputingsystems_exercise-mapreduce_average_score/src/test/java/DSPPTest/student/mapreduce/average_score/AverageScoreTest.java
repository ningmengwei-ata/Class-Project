package DSPPTest.student.mapreduce.average_score;

import DSPPCode.mapreduce.average_score.question.ScoreRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class AverageScoreTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath = root + "/mapreduce/average_score/input";
    String outputPath = outputRoot + "/mapreduce/average_score";
    String outputFile = outputPath + "/part-r-00000";
    String answerFile = root + "/mapreduce/average_score/answer";

    // 删除旧输出
    deleteFolder(outputPath);

    // 执行
    String[] args = {inputPath, outputPath};
    int exitCode = ToolRunner.run(new ScoreRunner(), args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser("\t"));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }

}
