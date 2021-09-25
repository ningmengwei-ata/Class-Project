package DSPPTest.student.mapreduce.kmeans;

import DSPPCode.mapreduce.kmeans.question.KMeansRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class KMeansTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath1 = root + "/mapreduce/kmeans/data";
    String inputPath2 = root + "/mapreduce/kmeans/center/";
    String outputPath = outputRoot + "/mapreduce/kmeans/";
    String outputFile = outputPath + "3/part-m-00000";
    String answerFile = root + "/mapreduce/kmeans/answer";

    // 删除旧输出
    deleteFolder(outputPath);

    // 执行
    KMeansRunner kmeanseRunner = new KMeansRunner();
    String[] args = {inputPath1, outputPath, inputPath2, "0.05"};
    int exitCode = kmeanseRunner.mainRun(args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser("\t"));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }
}
