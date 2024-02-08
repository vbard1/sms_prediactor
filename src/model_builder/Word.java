package src.model_builder;

import java.util.HashMap;
import java.util.Map;

import src.Main;

public class Word {
    public String value;
    public HashMap<Word, Integer> nextWords;

    public Word(String value) {
        this.value = value;
        this.nextWords = new HashMap<>();
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
        return value.toLowerCase().equals(otherWord.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringRecursive(sb, this, 0);
        return sb.toString();
    }

    private void toStringRecursive(StringBuilder sb, Word word, int depth) {
        for (Map.Entry<Word, Integer> entry : word.nextWords.entrySet()) {
            Word nextWord = entry.getKey();
            int count = entry.getValue();
            // Ajoute la prochaine Word et le nombre d'occurrences
            sb.append("\n");
            sb.append("  ".repeat(depth + 1)).append(nextWord.value)
                    .append(" (").append(count).append(")");
            // Appel récursif pour les suivants de la prochaine Word
            toStringRecursive(sb, nextWord, depth + 2);
        }
    }
}
