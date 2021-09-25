package DSPPTest.student;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

public abstract class TestTemplate {

  // 输入, 答案根目录
  protected static String root = System.getProperty("user.dir") + "/src/test/resources/student";

  // 输出根目录
  protected static String outputRoot = "/tmp/dspp_output/student";

  @BeforeClass
  public static void init() {
    BasicConfigurator.configure();
    Logger.getRootLogger().setLevel(Level.ERROR);
  }

}
