package DSPPTest.student.mapreduce.max_temperature;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class MaxTemperatureTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath = root + "/mapreduce/max_temperature/input";
    String outputPath = outputRoot + "/mapreduce/max_temperature";
    String outputFile = outputPath + "/part-r-00000";
    String answerFile = root + "/mapreduce/max_temperature/answer";

    // 删除旧输出
    deleteFolder(outputPath);

    String[] args = {inputPath, outputPath};
    int exitCode = ToolRunner.run(new MaxTemperatureRunner(), args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser("\t"));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }
}
