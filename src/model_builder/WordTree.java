package src.model_builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import src.Main;

public class WordTree {

  public String value;
  public int occurrences;
  public double nodeFrequency;
  public int depth;
  public ArrayList<WordTree> nextWords;

  private ProgressWindow progressWindow;

  public WordTree(String value, int parentDepth) {
    this.value = value;
    this.occurrences = 1;
    this.nodeFrequency = -1;
    this.nextWords = new ArrayList<>();
    this.depth = parentDepth + 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    WordTree otherWord = (WordTree) obj;
    return Boolean.parseBoolean(Main.config.getParamValue("ignore_case"))
      ? value.equalsIgnoreCase(otherWord.value)
      : value.equals(otherWord.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return toStringRecursive(this, "", true);
  }

  private String toStringRecursive(
    WordTree word,
    String prefix,
    boolean isLast
  ) {
    StringBuilder sb = new StringBuilder();
    sb
      .append("\n")
      .append(prefix)
      .append(isLast ? "└─ " : "├─ ")
      .append(word.value)
      .append(" (")
      .append(word.nodeFrequency)
      .append(")");

    List<WordTree> nextWords = word.nextWords;
    for (int i = 0; i < nextWords.size() - 1; i++) {
      WordTree nextWord = nextWords.get(i);
      sb.append(
        toStringRecursive(nextWord, prefix + (isLast ? "   " : "│  "), false)
      );
    }
    if (nextWords.size() >= 1) {
      WordTree nextWord = nextWords.get(nextWords.size() - 1);
      sb.append(
        toStringRecursive(nextWord, prefix + (isLast ? "   " : "│  "), true)
      );
    }

    return sb.toString();
  }

  public void computeFrequencies() {
    Main.log.info("Computing tree frequencies now...");
    computeFrequenciesRecursive(this);
    Main.log.info("Frequencies computed");
  }

  private void computeFrequenciesRecursive(WordTree computationRootWord) {
    /*if (progressWindow == null) {
      progressWindow =
        new ProgressWindow(
          "Local word frequencies computation",
          new String[] { "Progress : " }
        );
      progressWindow.setVisible(true);
    }*/

    double totalNodeOccurrences = 0;
    for (WordTree word : computationRootWord.nextWords) {
      totalNodeOccurrences += word.occurrences;
    }
    //int nbLvl2WordsTreated = 0;
    for (WordTree word : computationRootWord.nextWords) {
      word.nodeFrequency = ((double) word.occurrences) / totalNodeOccurrences;
      if (word.depth == 2) {
        //nbLvl2WordsTreated++;
        /*progressWindow.setProgress(
          0,
          (int) (
            (double) 100 * nbLvl2WordsTreated / (double) this.nextWords.size()
          )
        );*/
      }
      computeFrequenciesRecursive(word);
    }
  }

  public void sortByFrequency() {
    sortByFrequencyRecursive(this);
  }

  private void sortByFrequencyRecursive(WordTree word) {
    Collections.sort(
      word.nextWords,
      (w1, w2) -> Double.compare(w2.nodeFrequency, w1.nodeFrequency)
    );

    for (WordTree nextWord : word.nextWords) {
      sortByFrequencyRecursive(nextWord);
    }
  }
}
