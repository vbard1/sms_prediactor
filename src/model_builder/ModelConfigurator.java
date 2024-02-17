package src.model_builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import src.Main;

/**
 * Classe pour la construction du modèle à partir des données XML.
 */
public class ModelConfigurator {

  public SmsXmlFile smsXmlFile;
  public String selectedAddress; // Variable pour stocker l'adresse sélectionnée
  public Document preparedXMLSources;

  /**
   * Constructeur de la classe ModelConfigurator.
   *
   * @param sourceFilePath Chemin du fichier source XML.
   */
  public ModelConfigurator(String sourceFilePath) {
    smsXmlFile = new SmsXmlFile();
    smsXmlFile.filePath = sourceFilePath;
  }

  /**
   * Lance le processus de formation du modèle.
   * Cette méthode prépare d'abord le document XML en le nettoyant des nœuds MMS,
   * puis elle effectue des statistiques sur les adresses présentes dans le
   * document.
   * @return
   */
  public boolean configure() {
    boolean success = false;
    preparedXMLSources = prepareDocument();
    if (preparedXMLSources != null) {
      chooseRecipientToTrainWith(preparedXMLSources);
      if (selectedAddress != null) {
        deleteSmsWithDifferentAddress(
          preparedXMLSources.getDocumentElement(),
          selectedAddress
        );
        Main.log.info("Selected address was isolated");
        removeNodesWithTagValue(
          preparedXMLSources.getDocumentElement(),
          "type",
          "1"
        );
        Main.log.info("ModelConfigurator prepared the data");
        success = true;
      } else {
        Main.log.severe(
          "ModelConfigurator exited sooner than expected : the adress chosen to train the model with was null"
        );
      }
    } else {
      Main.log.severe(
        "ModelConfigurator exited sooner than expected : preparedSources was null"
      );
    }
    return success;
  }

  /**
   * Méthode pour choisir le destinataire avec lequel entraîner le modèle.
   *
   * @param preparedSources Document XML préparé.
   */
  private String chooseRecipientToTrainWith(Document preparedSources) {
    // Création d'une nouvelle fenêtre JFrame
    JFrame frame = new JFrame("Choisir le destinataire");

    // Création d'un conteneur JPanel pour organiser les composants
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Création d'un groupe de boutons radio pour assurer la sélection exclusive
    ButtonGroup buttonGroup = new ButtonGroup();

    // Obtention du nombre minimum d'occurrences à partir du fichier de
    // configuration
    int minOccurrences = Integer.parseInt(
      Main.config.getParamValue("nb_min_chats_per_adress_to_be_trained")
    );

    // TreeMap pour trier les adresses par ordre décroissant d'occurrences
    TreeMap<Integer, String> sortedAddresses = new TreeMap<>((o1, o2) -> o2 - o1
    );

    // Parcourir les adresses et les occurrences dans la HashMap
    for (Map.Entry<String, Integer> entry : smsXmlFile.adresses.entrySet()) {
      String address = entry.getKey();
      int occurrences = entry.getValue();
      // Ne prendre que les adresses avec un nombre d'occurrences supérieur au seuil
      // spécifié dans le fichier de configuration
      if (occurrences > minOccurrences) {
        sortedAddresses.put(occurrences, address);
      }
    }

    // Limiter le nombre d'adresses à afficher aux 5 premières
    int count = 0;
    for (Map.Entry<Integer, String> entry : sortedAddresses.entrySet()) {
      String address = entry.getValue();
      int occurrences = entry.getKey();
      // Création du bouton radio avec l'adresse et le nombre d'occurrences
      JRadioButton radioButton = new JRadioButton(
        "Adresse: " + address + ", " + occurrences + " sms"
      );
      // Ajout d'un écouteur d'événements pour le bouton radio
      radioButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Récupérer l'adresse sélectionnée lorsque le bouton radio est sélectionné
            selectedAddress = address;
          }
        }
      );
      // Ajout du bouton radio au groupe de boutons
      buttonGroup.add(radioButton);
      // Ajout du bouton radio au panneau
      panel.add(radioButton);

      count++;
      if (count >= 5) {
        break; // Sortir de la boucle après avoir ajouté les 5 premières adresses
      }
    }

    // Création du bouton "OK"
    JButton okButton = new JButton("OK");
    okButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Fermer la fenêtre lors du clic sur le bouton "OK"
          frame.dispose();
        }
      }
    );

    // Ajout du bouton "OK" au panneau
    panel.add(okButton);

    // Ajout du panneau à la fenêtre
    frame.add(panel);

    // Paramètres de la fenêtre
    frame.pack();
    frame.setSize((int) (frame.getWidth() * 1.5), frame.getHeight());
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Attendre jusqu'à ce que la fenêtre soit fermée
    while (frame.isVisible()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    // Retourner l'adresse sélectionnée
    return selectedAddress;
  }

  /**
   * Prépare le document XML en le nettoyant des nœuds MMS et en effectuant des
   * statistiques sur les adresses.
   *
   * @return Document XML préparé.
   */
  private Document prepareDocument() {
    try {
      // Affiche le fichier en cours de préparation
      Main.log.info("Preparing file : " + smsXmlFile.filePath);

      // Création d'un nouveau constructeur de document XML
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(smsXmlFile.filePath);

      // Suppression des nœuds MMS du document
      Main.log.info("Removing mms nodes");
      removeNodesWithName(document.getDocumentElement(), "mms");

      // Effectue des statistiques sur les adresses présentes dans le document
      adressesStats(document);
      Main.log.info("Prepared document successfuly.");
      return document;
    } catch (ParserConfigurationException | SAXException | IOException e) {
      Main.log.severe(
        "Couldn't prepare file properly : " + smsXmlFile.filePath
      );
      return null;
    }
  }

  /**
   * Effectue des statistiques sur les adresses présentes dans le document XML.
   * Les statistiques incluent le nombre d'occurrences de chaque adresse, stockées
   * dans SmsXmlFile.adresses.
   *
   * @param document Document XML.
   */
  public void adressesStats(Document document) {
    // Map pour stocker les occurrences de chaque adresse
    // Récupère tous les nœuds "sms" dans le document
    NodeList smsNodes = document.getElementsByTagName("sms");
    // Parcourt tous les nœuds "sms" pour récupérer les adresses
    for (int i = 0; i < smsNodes.getLength(); i++) {
      Node smsNode = smsNodes.item(i);
      if (smsNode.getNodeType() == Node.ELEMENT_NODE) {
        Element smsElement = (Element) smsNode;
        String address = smsElement.getAttribute("address");
        // Incrémente le nombre d'occurrences de l'adresse actuelle
        smsXmlFile.adresses.put(
          address,
          smsXmlFile.adresses.getOrDefault(address, 0) + 1
        );
      }
    }

    // Affiche le nombre d'occurrences de chaque adresse et les stocke dans
    // SmsXmlFile.adresses
    Main.log.info(smsXmlFile.adresses.size() + " adresses found for analysis");
  }

  /**
   * Supprime les nœuds commençant par "mms" dans le document XML.
   *
   * @param node Nœud à partir duquel commencer la suppression.
   */
  private void removeNodesWithName(Node node, String name) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      // Si le nom du nœud commence par "name", le supprime
      if (element.getNodeName().startsWith(name)) {
        node.getParentNode().removeChild(node);
      } else {
        // Sinon, parcourt les nœuds enfants récursivement
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          removeNodesWithName(children.item(i), name);
        }
      }
    }
  }

  private void removeNodesWithTagValue(Node node, String tag, String value) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;

      if (
        element.hasAttribute(tag) && element.getAttribute(tag).equals(value)
      ) {
        node.getParentNode().removeChild(node);
        return;
      }

      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        removeNodesWithTagValue(children.item(i), tag, value);
      }
    }
  }

  // Méthode récursive pour parcourir et supprimer les nœuds sms avec une adresse
  // différente
  public static void deleteSmsWithDifferentAddress(
    Node node,
    String selectedAddress
  ) {
    // Vérifier si le nœud actuel est un élément
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;

      // Vérifier si l'élément est un nœud sms
      if ("sms".equals(element.getNodeName())) {
        // Récupérer la valeur de l'attribut adress
        String addressAttribute = element.getAttribute("address");

        // Si l'attribut adress est différent de l'adresse sélectionnée, supprimer le
        // nœud
        if (!selectedAddress.equals(addressAttribute)) {
          // Supprimer le nœud sms
          element.getParentNode().removeChild(element);
          return; // Pas besoin de parcourir les enfants de ce nœud puisqu'il est supprimé
        }
      }

      // Récupérer la liste des enfants de cet élément
      NodeList children = element.getChildNodes();

      // Parcourir les enfants récursivement
      for (int i = 0; i < children.getLength(); i++) {
        deleteSmsWithDifferentAddress(children.item(i), selectedAddress);
      }
    }
  }
}
