package DSPPCode.flink.email_assignment.question;

public class Request {

  /** 请求类型 */
  private RequestType type;

  /** 请求用户的工号 */
  private int id;

  /** 部门 */
  private Department depart;

  /** 请求的别名 */
  private String alias;

  public Request(RequestType type, int id, Department depart, String alias) {
    this.type = type;
    this.id = id;
    this.depart = depart;
    this.alias = alias;
  }

  public RequestType getType() {
    return type;
  }

  public void setType(RequestType type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Department getDepart() {
    return depart;
  }

  public void setDepart(Department depart) {
    this.depart = depart;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

}
