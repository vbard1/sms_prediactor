package src.model_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import src.config_reader.ConfigReader;

public class Word {
    public String value;
    public int occurrences;
    public ArrayList<Word> nextWords;

    public Word(String value) {
        this.value = value;
        this.occurrences=1;
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
        //TODO paramétrer lignorance de la casse avec la config
        return value.equals(otherWord.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return toStringRecursive(this, 0);
    }
    
    private String toStringRecursive(Word word, int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("  ".repeat(depth)).append(word.value)
                .append(" (").append(word.occurrences).append(")");
    
        for (Word nextWord : word.nextWords) {
            sb.append(toStringRecursive(nextWord, depth + 1));
        }
    
        return sb.toString();
    }
    
    
}
