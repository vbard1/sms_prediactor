package src.model_builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.*;
import src.Main;
import src.log_factory.LogFactory;

public class ModelTrainer {

  static WordTree wordTree;

  public static void train(
    SmsXmlFile sourceFile,
    Document preparedXMLSources,
    String selectedAddress
  ) {
    Main.log.info("Starting Model Trainer with address " + selectedAddress);
    //computing sentences into the occurence tree
    wordTree = new WordTree("ROOT_OCCURENCES", 0);
    traverseNodes(preparedXMLSources.getDocumentElement());

    //computing frequencies out of occurence tree
    wordTree.computeFrequencies();

    LogFactory.toggleConsoleLogs(false, Main.log);
    for (WordTree word : wordTree.nextWords) {
      Main.log.info(word.toString());
    }
    LogFactory.toggleConsoleLogs(true, Main.log);
    // TODO nettoyer
    // TODO Mettre tout l'arbre dans un fichier ou une structure de données appropriée (?)
    // TODO développer la factory de suggestion live (android? -> cycle de vie : en conf introduire la période à considérer depuis le jour J (backtime eg. -3 mois)
    // TODO implémenter un runner de réentrainnement de model réguler)
    // TODO implémenter l'option non ne pas réentrainner le modèle...
    // TODO ...et dans ce cas lister les fichiers existant (pour celà, y associer des données notamment le destinataire concerné)
    // TODO la première fenêtre devrait être le paramétrage du fichier de config (Config.write?)
    // TODO améliroer le découpage des phrases et des messages (une nouvelle phrase c'est smiley/.../.)
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

        String emojiRegexBlock="(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)";
        String emojiRegexBolck2="\\p{Block=Emoticons}";
        List<String> sentences = Arrays
          .asList(messageBody.split("\\.{1,3}|…|"+emojiRegexBlock))
          .stream()
          .map(String::trim)
          .filter(sentence -> !sentence.isEmpty())
          .collect(Collectors.toList());

        for (String sentence : sentences) {
          addSentence(sentence.trim().split(" "));
        }
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
    WordTree word;
    WordTree referenceWord = wordTree;
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
      word = new WordTree(currentWordString, referenceWord.depth);

      // 1. récupérer l'index du mot de la phrase s'il existe dans les mots suivants et incrémenter
      found = false;
      i = 0;
      while (!found && i < referenceWord.nextWords.size()) {
        if (referenceWord.nextWords.get(i).equals(word)) {
          referenceWord.nextWords.get(i).occurrences =
            referenceWord.nextWords.get(i).occurrences + 1;
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
