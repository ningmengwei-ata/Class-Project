package DSPPCode.mapreduce.average_score.question;

public class Util {

  private static final String[] COURSE_NAME = {
      "Mathematical analysis",
      "Probability Theory",
      "Function of Real Variable",
  };

  public static String getCourseName(int id) {
    return COURSE_NAME[id];
  }

}
