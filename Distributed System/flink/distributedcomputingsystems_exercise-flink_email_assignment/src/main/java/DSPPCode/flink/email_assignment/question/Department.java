package DSPPCode.flink.email_assignment.question;

public class Department {

  /** 一级部门代码 */
  private int firstLevelCode;

  /** 二级部门代码 */
  private int secondLevelCode;

  public Department(int firstLevelCode, int secondLevelCode) {
    this.firstLevelCode = firstLevelCode;
    this.secondLevelCode = secondLevelCode;
  }

  public int getFirstLevelCode() {
    return firstLevelCode;
  }

  public void setFirstLevelCode(int firstLevelCode) {
    this.firstLevelCode = firstLevelCode;
  }

  public int getSecondLevelCode() {
    return secondLevelCode;
  }

  public void setSecondLevelCode(int secondLevelCode) {
    this.secondLevelCode = secondLevelCode;
  }

}
