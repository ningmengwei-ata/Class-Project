package DSPPTest.student.flink.grade_point;

import DSPPCode.flink.grade_point.impl.GradePointImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

public class GradePointTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputFile = root + "/flink/grade_point/input";
    String outputFile = outputRoot + "/flink/grade_point";
    String answerFile = root + "/flink/grade_point/answer";

    // 删除旧输出
    deleteFolder(outputFile);

    // 执行
    String[] args = {inputFile, outputFile};
    GradePointImpl gradePoint = new GradePointImpl();
    gradePoint.run(args);

    // 检验结果
    verifyList(readFile2String(outputFile), readFile2String(answerFile));

    System.out.println("恭喜通过~");
  }
}
