package src.log_factory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import src.Main;

public class LogFactory {

  private static final String PATH_TO_LOGS_FOLDER = Main.config.getParamValue(
    "path_to_logs_folder"
  );
  private static final ConsoleHandler consoleHandler = new ConsoleHandler();
  private static final int MAX_LOG_FILES = 5;

  public static Logger getNewDatedLogFactory(Logger logger) {
    try {
      File logsFolder = new File(PATH_TO_LOGS_FOLDER);
      if (!logsFolder.exists()) {
        logsFolder.mkdirs();
      }

      File[] existingLogs = logsFolder.listFiles(
        new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return (
              name.toLowerCase().endsWith(".log") ||
              name.toLowerCase().endsWith(".lck")
            );
          }
        }
      );

      List<File> existingLogsList = new ArrayList<>(
        Arrays.asList(existingLogs)
      );

      while (existingLogsList.size() >= MAX_LOG_FILES) {
        File oldestFile = existingLogsList.get(0);
        for (File file : existingLogsList) {
          if (file.lastModified() < oldestFile.lastModified()) {
            oldestFile = file;
          }
        }
        oldestFile.delete();
        existingLogsList.remove(oldestFile);
      }

      // Créer un nouveau fichier de log
      FileHandler fileHandler = new FileHandler(
        PATH_TO_LOGS_FOLDER +
        (new Date()).toString().replaceAll(":", "_") +
        ".log"
      );
      fileHandler.setFormatter(new DatedLogsFormatter());
      logger.addHandler(fileHandler);
      logger.info(
        "Logger initialized. Logs can be found at: " + PATH_TO_LOGS_FOLDER
      );
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error setting up file logging", e);
    }
    return logger;
  }

  public static void toggleConsoleLogs(boolean enable, Logger logger) {
    if (enable) {
      logger.setUseParentHandlers(true);
      logger.warning("Logger back online");
    } else {
      logger.warning(
        "Unplugging logger from console, a massive logging period is inbound"
      );
      logger.setUseParentHandlers(false);
    }
  }
}
