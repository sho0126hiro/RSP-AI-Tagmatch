package common;

/**
 * じゃんけんを定義する関数
 */
public enum RSPEnum {
  ROCK(0), SCISORS(1), PAPER(2);

  private final int index;

  private RSPEnum(final int index) {
    this.index = index;
  }

  /**
   * index: ROCK SCISORS PAPER の順
   */
  public int getIndex() {
    return this.index;
  }

}
