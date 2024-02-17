package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;
import javax.swing.*;
import src.config_reader.ConfigReader;
import src.log_factory.LogFactory;
import src.model_builder.ModelConfigurator;
import src.model_builder.ModelExploiter_Dummy;
import src.model_builder.ModelTrainer;
import src.model_builder.WordTree;

public class Main {

  public static Logger log;
  public static ConfigReader config;

  public static void main(String[] args) {
    try {
      config = new ConfigReader();
      config.readConfigFile();
      log =
        LogFactory.getNewDatedLogFactory(
          Logger.getLogger(Main.class.getName())
        );
      PromptMenu();
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTraceString = sw.toString();
      log.severe("Exception not handled : Stack trace =\n" + stackTraceString);
    }
  }

  private static void PromptMenu() {
    switch (
      JOptionPane.showConfirmDialog(
        null,
        "Would you like to run the model trainer first?"
      )
    ) {
      case 0:
        workflow();
      default:
        log.info("The application finished with no more job to do");
    }
  }

  private static void workflow() {
    WordTree trainedModel = launchModelTraining(
      launchModelConfiguration(selectFileUser())
    );
    logModel(trainedModel);
    exploitModel(trainedModel);
  }

  private static void exploitModel(WordTree trainedModel) {
    ModelExploiter_Dummy modelExploiter = new ModelExploiter_Dummy(trainedModel);
    modelExploiter.start();
  }

  private static void logModel(WordTree trainedModel) {
    LogFactory.toggleConsoleLogs(false, Main.log);
    for (WordTree word : trainedModel.nextWords) {
      Main.log.info(word.toString());
    }
    LogFactory.toggleConsoleLogs(true, Main.log);
  }

  private static WordTree launchModelTraining(
    ModelConfigurator modelConfigurator
  ) {
    return ModelTrainer.train(
      modelConfigurator.preparedXMLSources,
      modelConfigurator.selectedAddress
    );
  }

  private static ModelConfigurator launchModelConfiguration(File selectedFile) {
    log.warning(
      "Launching model configuration with file: " +
      selectedFile.getAbsolutePath()
    );
    ModelConfigurator mc = new ModelConfigurator(
      selectedFile.getAbsolutePath()
    );
    mc.configure();
    return mc;
  }

  private static File selectFileUser() {
    try {
      JFileChooser fileChooser = new JFileChooser("inputs");
      int returnValue = fileChooser.showOpenDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        return fileChooser.getSelectedFile();
      } else {
        throw new Exception("No file was chosen");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
