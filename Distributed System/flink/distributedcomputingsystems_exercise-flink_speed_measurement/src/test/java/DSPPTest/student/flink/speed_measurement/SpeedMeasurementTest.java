package DSPPTest.student.flink.speed_measurement;

import DSPPCode.flink.speed_measurement.question.SpeedMeasurement;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

public class SpeedMeasurementTest extends TestTemplate {
  @Test
  public void test() throws Exception {
    // 设置路径
    String inputFile = root + "/flink/speed_measurement/input";
    String outputFile = outputRoot + "/flink/speed_measurement";
    String answerFile = root + "/flink/speed_measurement/answer";

    // 删除旧输出
    deleteFolder(outputFile);

    // 执行
    String[] args = {inputFile, outputFile};
    SpeedMeasurement speedMeasurement = new SpeedMeasurement();
    speedMeasurement.run(args);

    // 检验结果
    verifyList(readFile2String(outputFile), readFile2String(answerFile));

    System.out.println("恭喜通过~");
  }
}
