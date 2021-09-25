package DSPPTest.student.flink.water_problem;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.io.BufferedReader;
import java.io.Serializable;

import static DSPPTest.util.FileOperator.getBufferedReader;

public class WaterEventSource implements SourceFunction<String>, Serializable {

  /**
   * 输入路径
   */
  private final String inputPath;


  public WaterEventSource(String inputPath) {
    this.inputPath = inputPath;
  }

  @Override
  public void run(SourceContext<String> sourceContext) throws Exception {
    BufferedReader br = getBufferedReader(inputPath);
    String line;

    while ((line = br.readLine()) != null) {
      sourceContext.collect(line);
    }

    br.close();
    Thread.sleep(1000);

  }


  @Override
  public void cancel() {

  }
}
