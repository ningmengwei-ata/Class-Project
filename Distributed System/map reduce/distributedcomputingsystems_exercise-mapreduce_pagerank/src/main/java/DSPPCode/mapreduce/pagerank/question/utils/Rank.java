package DSPPCode.mapreduce.pagerank.question.utils;

public class Rank {
  //网页名
  private String pageName;
  //网页排名
  private double rank;

  public void setPageName(String pageName) {
    this.pageName = pageName;
  }

  public void setRank(double rank) {
    this.rank = rank;
  }

  public double getRank() {
    return rank;
  }

  public String getPageName() {
    return pageName;
  }
}
