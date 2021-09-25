package DSPPTest.student.spark.moving_averages;

import DSPPCode.spark.moving_averages.impl.MovingAveragesImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Before;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

public class MovingAveragesTest extends TestTemplate {

  // 输入文件基础路径
  private static final String BASE_INPUT_PATH = root + "/spark/moving_averages/input";
  // 作业的输出路径
  private static final String OUTPUT_PATH = outputRoot + "/spark/moving_averages";
  // 作业的输出结果路径
  private static final String OUTPUT_FILE = OUTPUT_PATH + "/part-00000";
  // 答案的基础路径
  private static final String BASE_ANSWER_FILE = root + "/spark/moving_averages/answer";

  // 删除旧输出
  @Before
  public void deleteOutput() {
    deleteFolder(OUTPUT_PATH);
  }

  @Test
  public void test() throws Exception {
    // 执行
    String[] args = {BASE_INPUT_PATH, OUTPUT_PATH};
    MovingAveragesImpl movingAverages = new MovingAveragesImpl();
    movingAverages.run(args);

    // 检验结果
    verifyList(readFile2String(OUTPUT_FILE), readFile2String(BASE_ANSWER_FILE));

    System.out.println("恭喜通过~");
  }
}
