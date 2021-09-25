package DSPPTest.student.flink.digital_conversion;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

import DSPPCode.flink.digital_conversion.impl.DigitalConversionImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

public class DigitalConversationTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputFile = root + "/flink/digital_conversion/input";
    String outputPath = outputRoot + "/flink/digital_conversion";
    String outputTask1 = outputPath + "/1";
    String outputTask2 = outputPath + "/2";
    String answerPath = root + "/flink/digital_conversion/answer";
    String answerTask1 = answerPath + "/answer1-1";
    String answerTask2 = answerPath + "/answer1-2";

    // 删除旧输出
    deleteFolder(outputPath);

    // 执行
    String[] args = {inputFile, outputPath};
    DigitalConversionImpl digitalConversion = new DigitalConversionImpl();
    digitalConversion.run(args);

    // 检验结果
    verifyList(readFile2String(outputTask1), readFile2String(answerTask1));
    verifyList(readFile2String(outputTask2), readFile2String(answerTask2));

    System.out.println("恭喜通过~");
  }
}
