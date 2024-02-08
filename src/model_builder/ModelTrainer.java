package src.model_builder;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.*;

import src.Main;
import src.json_factory.JsonFactory;

public class ModelTrainer {

    static HashMap<Word, Integer> occurenceTree;

    public static void train(SmsXmlFile sourceFile, Document preparedXMLSources, String selectedAddress) {
        occurenceTree = new HashMap<>();
        // ArrayList<Word>(sourceFile.adresses.get(selectedAddress));
        Main.log.info("Starting Model Trainer with address " + selectedAddress);
        traverseNodes(preparedXMLSources.getDocumentElement()); // recurse through all nodes
        for (Word word : occurenceTree.keySet()) {
            Main.log.info(word.toString());
        }

        // JsonFactory.writeJsonToFile(JsonFactory.convertToJson(probabilityTree),
        // "outputs/test.json");
    }

    public static void traverseNodes(Node node) {
        // Vérifier si le nœud actuel est un élément (et non un texte, un commentaire,
        // etc.)
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            // Traiter l'élément selon ses besoins
            if (element.getNodeName().equals("sms")) {
                String messageBody = element.getAttributeNode("body").getValue().toString();
                // TODO découper par séparateusr (points smileyx) et boucler la suite sur le
                // tableau obtenu
                addSentence(messageBody.split(" "));
            }
            NodeList childrenNodes = element.getChildNodes();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                traverseNodes(childrenNodes.item(i));
            }
        }
    }

    public static void addSentence(String[] sentence) {
        if (sentence.length == 0)
            return;

        LinkedList<String> sentenceList = new LinkedList<>(Arrays.asList(sentence));
        Word word = new Word(sentenceList.get(0));
        Word referenceWord = word;

        if (occurenceTree.containsKey(word)) {
            occurenceTree.put(word, occurenceTree.get(word) + 1);
        } else {
            occurenceTree.put(word, 1);
        }

        for (String currentWordString : sentenceList) {
            word = new Word(currentWordString);
            if (referenceWord.nextWords.containsKey(word)) {
                referenceWord.nextWords.put(word, referenceWord.nextWords.get(word) + 1);
            } else {
                referenceWord.nextWords.put(word, 1);
            }
            referenceWord = word;
        }
    }
    /*public static void addSentence(String[] sentence) {
        if (sentence.length == 0)
            return;
    
        Word previousWord = null;
        Map<Word, Integer> occurrencesInSentence = new HashMap<>();
    
        for (String currentWordString : sentence) {
            Word currentWord = new Word(currentWordString);
    
            // Mise à jour de l'occurrence pour le mot actuel dans occurrencesInSentence
            occurrencesInSentence.compute(currentWord, (k, v) -> (v == null) ? 1 : v + 1);
    
            // Mise à jour de l'occurrence pour le mot suivant dans la séquence
            if (previousWord != null) {
                previousWord.nextWords.compute(currentWord, (k, v) -> (v == null) ? 1 : v + 1);
            }
    
            previousWord = currentWord;
        }
    
        // Mettre à jour occurenceTree avec les occurrences dans occurrencesInSentence
        occurrencesInSentence.forEach((word, count) ->
                occurenceTree.compute(word, (k, v) -> (v == null) ? count : v + count));
    }*/
    
}
