package DSPPTest.student.flink.email_assignment;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

import DSPPCode.flink.email_assignment.impl.EmailAssignmentImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

public class EmailAssignmentTest extends TestTemplate {

  @Test
  public void test() throws Exception {

    // 设置路径
    String inputFile = root + "/flink/email_assignment/input";
    String outputFile = outputRoot + "/flink/email_assignment/output";
    String answerFile = root + "/flink/email_assignment/answer";

    // 删除旧输出
    deleteFolder(outputFile);

    new EmailAssignmentImpl().run(inputFile, outputFile);
    verifyList(readFile2String(outputFile), readFile2String(answerFile));

    System.out.println("恭喜通过~");

  }

}
