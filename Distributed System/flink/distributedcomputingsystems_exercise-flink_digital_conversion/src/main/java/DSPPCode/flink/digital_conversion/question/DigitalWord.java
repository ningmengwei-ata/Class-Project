package DSPPCode.flink.digital_conversion.question;

public enum DigitalWord {

  /**
   * 0-9的整数对应的英文单词
   */
  ZERO("Zero"), ONE("One"), TWO("Two"), THREE("Three"), FOUR("Four"), FIVE("Five"),
  SIX("Six"), SEVEN("Seven"), EIGHT("Eight"), NINE("Nine");

  private final String word;

  DigitalWord(String word) {
    this.word = word;
  }

  public String getWord() {
    return word;
  }
}
