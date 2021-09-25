package DSPPTest.student.flink.gcd;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

import DSPPCode.flink.gcd.impl.GCDImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

public class GCDTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputFile = root + "/flink/gcd/input";
    String outputFile = outputRoot + "/flink/gcd";
    String answerFile = root + "/flink/gcd/answer";

    // 删除旧输出
    deleteFolder(outputFile);

    // 执行
    String[] args = {inputFile, outputFile};
    GCDImpl gcdImpl = new GCDImpl();
    gcdImpl.run(args);

    // 检验结果
    verifyList(readFile2String(outputFile), readFile2String(answerFile));

    System.out.println("恭喜通过~");
  }
}
