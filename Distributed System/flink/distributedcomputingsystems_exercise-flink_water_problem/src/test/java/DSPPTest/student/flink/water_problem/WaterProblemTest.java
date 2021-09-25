package DSPPTest.student.flink.water_problem;

import DSPPCode.flink.water_problem.question.WaterProblem;
import DSPPCode.flink.water_problem.impl.WaterProblemImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

public class WaterProblemTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputFile = root + "/flink/water_problem/input";
    String outputFile = outputRoot + "/flink/water_problem";
    String answerFile = root + "/flink/water_problem/answer";

    // 删除旧输出
    deleteFolder(outputFile);

    // 执行
    WaterProblem waterProblem = new WaterProblemImpl();
    waterProblem.run(new WaterEventSource(inputFile), outputFile);

    // 检验结果
    verifyList(readFile2String(outputFile), readFile2String(answerFile));

    System.out.println("恭喜通过~");
  }
}
