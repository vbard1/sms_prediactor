package src.json_factory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JsonFactory {
/*
       public static String convertToJson(Map<String, WordAssociation> probabilityTree) {
        StringBuilder sb = new StringBuilder("[\n");

        boolean firstWord = true; // Pour vérifier si c'est le premier mot
        
        for (Map.Entry<String, WordAssociation> entry : probabilityTree.entrySet()) {
            if (!firstWord) {
                sb.append(",\n"); // Ajouter une virgule et un saut de ligne si ce n'est pas le premier mot
            } else {
                firstWord = false;
            }

            String word = entry.getKey();
            WordAssociation wordAssociation = entry.getValue();

            // Début de la liste pour le mot actuel
            sb.append("[{\"").append(word).append("\":").append(wordAssociation.occurrences);

            // Si le mot a des mots suivants
            if (!wordAssociation.nextWords.isEmpty()) {
                sb.append(",");
                // Appel récursif pour imprimer les mots suivants
                sb.append(convertNextWordsToJsonRecursive(wordAssociation.nextWords));
            }

            sb.append("}");
        }

        sb.append("\n]");
        return sb.toString();
    }

    // Méthode récursive pour convertir les mots suivants en JSON
    private static String convertNextWordsToJsonRecursive(Map<String, WordAssociation> nextWords) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<String, WordAssociation> entry : nextWords.entrySet()) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            String word = entry.getKey();
            WordAssociation wordAssociation = entry.getValue();
            sb.append("{\"").append(word).append("\":").append(wordAssociation.occurrences);
            
            // Si le mot a des mots suivants, appel récursif
            if (!wordAssociation.nextWords.isEmpty()) {
                sb.append(",");
                sb.append(convertNextWordsToJsonRecursive(wordAssociation.nextWords));
            }

            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }
    private static String washWord(String key) {
        return key.replaceAll("\n", " ").replaceAll("\"", "").replaceAll("\\\\", "");
    }

    public static void writeJsonToFile(String json, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */
}
