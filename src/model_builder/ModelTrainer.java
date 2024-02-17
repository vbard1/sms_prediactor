package src.model_builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.*;
import src.Main;
import src.config_reader.ConfigReader;
import src.log_factory.LogFactory;

public class ModelTrainer {

  static WordTree wordTree;

  public static WordTree train(
    Document preparedXMLSources,
    String selectedAddress
  ) {
    Main.log.info("Starting Model Trainer with address " + selectedAddress);
    //computing sentences into the occurence tree
    wordTree = new WordTree("ROOT_OCCURENCES", -1);
    traverseNodes(preparedXMLSources.getDocumentElement());

    //computing frequencies out of occurence tree
    wordTree.computeFrequencies();

    //sorting wordTrees by frequency
    wordTree.sortByFrequency();

    // returning the trained model :
    return wordTree;
    // TODO tenter de varier la taille du crible et le paramétriser
    // TODO arrondir les stats (moins de volume?)
    // TODO a la place d'une progress bar faire un count de nodes
    // TODO Mettre tout l'arbre dans un fichier ou une structure de données appropriée (?)
    // TODO développer la factory de suggestion live (android? -> cycle de vie : en conf introduire la période à considérer depuis le jour J (backtime eg. -3 mois)
    // TODO implémenter un runner de réentrainnement de model réguler)
    // TODO implémenter l'option non ne pas réentrainner le modèle...
    // TODO ...et dans ce cas lister les fichiers existant (pour celà, y associer des données notamment le destinataire concerné)
    // TODO la première fenêtre devrait être le paramétrage du fichier de config (Config.write?)
    // TODO améliroer le découpage des phrases et des messages (une nouvelle phrase c'est smiley/.../.)
    // TODO TOCONSIDER passer en crible de n mots pour éviter les effets niche? ajouter pour celà une sentence par n mots décalés sur le tableau de la sentence
    // exemple :
    // mot mot mot [ mot mot mot ]
    // mot mot [ mot mot mot ] mot
    // mot [ mot mot mot ] mot mot
    // [ mot mot mot ] mot mot mot
    // ICI CHAQUE CRIBLE DE 3 MOTS EST UNE SENTENCE (paramétrer)
    // TOCONSIDER adapter la réponse en fonction du message reçu? (?)
  }

  public static void traverseNodes(Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;

      if (element.getNodeName().equals("sms")) {
        String messageBody = element
          .getAttributeNode("body")
          .getValue()
          .toString();

        String emojiRegexBlock =
          "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)";
        List<String> sentences = Arrays
          .asList(messageBody.split("\\p{Punct}+" + emojiRegexBlock))
          .stream()
          .map(String::trim)
          .filter(sentence -> !sentence.isEmpty())
          .collect(Collectors.toList());

        for (String sentence : sentences) {
          /** //_v1 -> réduit le volume de données mais fait des niches (avec des mots spéciaux)
           * addSentence(sentence.trim().split(" "));
           */
          // _v2 -> fonctionne très bien pour éliminer les niches mais démultiplie le nombre de données : le faire à la manip? (un enfer de temps de calcul)
          //-> TODO tenter de varier la taille du crible et le paramétriser
          int tree_depth = Integer.parseInt(
            Main.config.getParamValue("tree_depth")
          ); // taille du crible

          String[] mots = sentence.trim().split(" ");
          int longueur = mots.length;

          if (longueur <= tree_depth) {
            addSentence(mots);
          } else {
            for (int i = 0; i <= longueur - tree_depth; i++) {
              StringBuilder nouvellePhrase = new StringBuilder();

              // Construire la nouvelle phrase avec le groupe de mots actuel
              for (int j = i; j < i + tree_depth; j++) {
                nouvellePhrase.append(mots[j]).append(" ");
              }

              // Ajouter la phrase construite à la liste
              addSentence(nouvellePhrase.toString().trim().split(" "));
            }
          }
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
      word =
        new WordTree(
          Main.config.getBooleanParamValue("ignore_case")
            ? currentWordString.toLowerCase()
            : currentWordString,
          referenceWord.depth
        );

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
}
