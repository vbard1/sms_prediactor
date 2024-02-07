package src.model_builder;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.*;

import src.Main;
import src.json_factory.JsonFactory;

public class ModelTrainer {

    //static ArrayList<Word> occurenceTree;

    public static void train(SmsXmlFile sourceFile, Document preparedXMLSources, String selectedAddress) {
        //occurenceTree = new ArrayList<Word>(sourceFile.adresses.get(selectedAddress));
        Main.log.info("Starting Model Trainer with address " + selectedAddress);
        traverseNodes(preparedXMLSources.getDocumentElement()); // recurse through all nodes
        //JsonFactory.writeJsonToFile(JsonFactory.convertToJson(probabilityTree), "outputs/test.json");
    }

    public static void traverseNodes(Node node) {
        // Vérifier si le nœud actuel est un élément (et non un texte, un commentaire,
        // etc.)
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            // Traiter l'élément selon ses besoins
            if (element.getNodeName().equals("sms")) {
                String messageBody = element.getAttributeNode("body").getValue().toString();
                addSentence(messageBody.split(" "));
            }
            NodeList childrenNodes = element.getChildNodes();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                traverseNodes(childrenNodes.item(i));
            }
        }
    }

    public static void addSentence(String[] sentence) {
        
    }
}
