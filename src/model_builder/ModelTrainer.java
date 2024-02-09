package src.model_builder;


import java.util.Arrays;
import java.util.LinkedList;
import org.w3c.dom.*;
import src.Main;

public class ModelTrainer {

  static Word occurenceTree;

  public static void train(
    SmsXmlFile sourceFile,
    Document preparedXMLSources,
    String selectedAddress
  ) {
    Main.log.info("Starting Model Trainer with address " + selectedAddress);
    occurenceTree = new Word("ROOT");
    traverseNodes(preparedXMLSources.getDocumentElement());
    for (Word word : occurenceTree.nextWords) {
      Main.log.info(word.toString());
    }
    // TODO nettoyer
    // TODO Transformer les occurences en fréquences après comptage (pendant? bof)
    // TODO Mettre tout l'arbre dans un fichier ou une structure de données appropriée (?)
    // TODO Filtrer par message reçu ou envoyé
    // TODO développer la factory de suggestion live (android? -> cycle de vie : en conf introduire la période à considérer depuis le jour J (backtime eg. -3 mois)
    // et implémenter un runner de réentrainnement de model réguler)
    // TODO implémenter l'option non ne pas réentrainner le modèle...
    // TODO paramétrer l'indépendance de la casse
    // TODO améliroer le découpage des phrases et des messages (une nouvelle phrase c'est smiley/.../.)
    // TODO ...et dans ce cas lister les fichiers existant (pour celà, y associer des données notamment le destinataire concerné)
    // TOCONSIDER adapter la réponse en fonction du message reçu? (?)
  }

  public static void traverseNodes(Node node) {
    // Vérifier si le nœud actuel est un élément (et non un texte, un commentaire,
    // etc.)
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;

      // Traiter l'élément selon ses besoins
      if (element.getNodeName().equals("sms")) {
        String messageBody = element
          .getAttributeNode("body")
          .getValue()
          .toString();
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
    if (sentence.length == 0) return;

    LinkedList<String> sentenceList = new LinkedList<>(Arrays.asList(sentence));
    Word word = new Word(sentenceList.get(0));
    Word referenceWord = occurenceTree;
    boolean found = false;
    int i = 0;

    //traitement ittératif des mots
    String currentWordString;
    for (
      int currentWordIndex = 0;
      currentWordIndex < sentenceList.size();
      currentWordIndex++
    ) {
      currentWordString = sentenceList.get(currentWordIndex);
      word = new Word(currentWordString);

      // 1. récupérer l'index du mot de la phrase s'il existe dans les mots suivants et incrémenter
      found = false;
      i = 0;
      while (!found && i < referenceWord.nextWords.size()) {
        if (referenceWord.nextWords.get(i).equals(word)) {
          referenceWord.nextWords.get(i).occurrences = referenceWord.nextWords.get(i).occurrences+1;
          found = true;
          referenceWord = referenceWord.nextWords.get(i);
        }
        i++;
      }

      // 2. si pas trouvé, le rajouter
      if (!found) {
        referenceWord.nextWords.add(word);
        referenceWord = referenceWord.nextWords.getLast();
      }
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
