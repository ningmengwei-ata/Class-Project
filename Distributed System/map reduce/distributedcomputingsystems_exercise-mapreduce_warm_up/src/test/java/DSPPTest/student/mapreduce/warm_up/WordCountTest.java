package DSPPTest.student.mapreduce.warm_up;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

import DSPPCode.mapreduce.warm_up.question.WordCountRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;

public class WordCountTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath = root + "/mapreduce/warm_up/input";
    String outpuPath = outputRoot + "/mapreduce/warm_up";
    String outputFile = outpuPath + "/part-r-00000";
    String answerFile = root + "/mapreduce/warm_up/answer";

    // 删除旧输出
    deleteFolder(outpuPath);

    String[] args = {inputPath, outpuPath};
    int exitCode = ToolRunner.run(new WordCountRunner(), args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser("\t"));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }

}
