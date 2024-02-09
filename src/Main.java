package src;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.*;
import javax.swing.*;

import src.config_reader.ConfigReader;
import src.log_factory.LogFactory;
import src.model_builder.ModelConfigurator;

public class Main {

    public static Logger log;
    public static Map config;

    public static void main(String[] args) {
        try {
            log = LogFactory.getNewDatedLogFactory(Logger.getLogger(Main.class.getName()));
            config = ConfigReader.readConfigFile();
            ShowMenu();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            log.severe("Exception not handled : Stack trace =\n"+stackTraceString);
        }
    }

    private static void ShowMenu() {

        switch (JOptionPane.showConfirmDialog(null, "Would you like to run the model trainer first?")) {
            case 0:
                launchTrainer();
            default:
                log.info("The application finished with no more job to do");
        }
    }

    private static void launchTrainer() {
        JFileChooser fileChooser = new JFileChooser("inputs");
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            log.warning("Launching model trainer with file: " + selectedFile.getAbsolutePath());
            ModelConfigurator mb = new ModelConfigurator(selectedFile.getAbsolutePath());
            log.warning("Launching model training");
            mb.launchTraining();
        }
    }
}
