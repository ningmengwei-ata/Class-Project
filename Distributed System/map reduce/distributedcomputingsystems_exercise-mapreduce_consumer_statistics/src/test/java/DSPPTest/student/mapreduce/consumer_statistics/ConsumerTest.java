package DSPPTest.student.mapreduce.consumer_statistics;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

import DSPPCode.mapreduce.consumer_statistics.question.ConsumerRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.ListParser;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;

public class ConsumerTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    //set dir
    String inputPath = root + "/mapreduce/consumer_statistics/input";
    String outputPath = outputRoot + "/mapreduce/consumer";
    String outputFile = outputPath + "/part-r-00000";
    String answerFile = root + "/mapreduce/consumer_statistics/answer";

    //delete old dirl
    deleteFolder(outputPath);

    String[] args = {inputPath, outputPath};
    int exitCode = ToolRunner.run(new ConsumerRunner(), args);

    //check result
    verifyList(readFile2String(outputFile), readFile2String(answerFile), new ListParser());
    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }
}
