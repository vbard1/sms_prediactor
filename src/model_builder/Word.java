package src.model_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import src.Main;
import src.config_reader.ConfigReader;

public class Word {

  public String value;
  public int occurrences;
  public ArrayList<Word> nextWords;

  public Word(String value) {
    this.value = value;
    this.occurrences = 1;
    this.nextWords = new ArrayList<>();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Word otherWord = (Word) obj;
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

  private String toStringRecursive(Word word, String prefix, boolean isLast) {
    StringBuilder sb = new StringBuilder();
    sb
      .append("\n")
      .append(prefix)
      .append(isLast ? "└─ " : "├─ ")
      .append(word.value)
      .append(" (")
      .append(word.occurrences)
      .append(")");

    List<Word> nextWords = word.nextWords;
    for (int i = 0; i < nextWords.size() - 1; i++) {
      Word nextWord = nextWords.get(i);
      sb.append(
        toStringRecursive(nextWord, prefix + (isLast ? "   " : "│  "), false)
      );
    }
    if (nextWords.size() >= 1) {
      Word nextWord = nextWords.get(nextWords.size() - 1);
      sb.append(
        toStringRecursive(nextWord, prefix + (isLast ? "   " : "│  "), true)
      );
    }

    return sb.toString();
  }
}
