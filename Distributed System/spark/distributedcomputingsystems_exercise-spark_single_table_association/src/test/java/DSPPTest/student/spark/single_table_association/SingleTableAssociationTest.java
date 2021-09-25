package DSPPTest.student.spark.single_table_association;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyList;

import DSPPCode.spark.single_table_association.impl.SingleTableAssociationImpl;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.ListParser;
import org.junit.Test;

public class SingleTableAssociationTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath = root + "/spark/single_table_association/input";
    String outputPath = outputRoot + "/spark/single_table_association";
    String outputFile = outputPath + "/part-00000";
    String answerFile = root + "/spark/single_table_association/answer";

    // 删除旧输出
    deleteFolder(outputPath);

    // 执行
    String[] args = {inputPath, outputPath};
    SingleTableAssociationImpl singleTableAssociation = new SingleTableAssociationImpl();
    singleTableAssociation.run(args);

    // 检验结果
    verifyList(readFile2String(outputFile), readFile2String(answerFile), new ListParser());

    System.out.println("恭喜通过~");
  }
}
