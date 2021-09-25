package DSPPTest.student.spark.topk_power;

import static org.junit.Assert.assertEquals;
import DSPPCode.spark.topk_power.impl.TopKPowerImpl;
import DSPPTest.student.TestTemplate;
import org.junit.Test;

public class TopKPowerTest extends TestTemplate {
  @Test
  public void test1() { // 设置路径
    String inputPath = root + "/spark/topk_power/input";
    int answer = 46;

    String[] args = {inputPath};
    TopKPowerImpl topKPower = new TopKPowerImpl();
    assertEquals(answer, topKPower.run(args), 0);
    System.out.println("恭喜通过~");
  }
}
