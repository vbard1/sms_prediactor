package src.log_factory;

import java.io.*;
import java.util.Date;
import java.util.logging.*;

public class LogFactory {

    private final static String PATH_TO_LOGS_FOLDER = "./outputs/logs/";
    private final static int MAX_LOG_FILES = 5;

    public static Logger getNewDatedLogFactory(Logger logger) {
        try {
            File logsFolder = new File(PATH_TO_LOGS_FOLDER);
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            
            File[] existingLogs = logsFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().endsWith(".log")||name.toLowerCase().endsWith(".lck"));
                }
            });

            if (existingLogs.length >= MAX_LOG_FILES) {
                File oldestFile = existingLogs[0];
                for (int i = 1; i < existingLogs.length; i++) {
                    if (existingLogs[i].lastModified() < oldestFile.lastModified()) {
                        oldestFile = existingLogs[i];
                    }
                }
                oldestFile.delete();
            }

            // Créer un nouveau fichier de log
            FileHandler fileHandler = new FileHandler(PATH_TO_LOGS_FOLDER + (new Date()).toString().replaceAll(":", "_") + ".log");
            fileHandler.setFormatter(new DatedLogsFormatter());
            logger.addHandler(fileHandler);
            logger.info("Logger initialized. Logs can be found at: " + PATH_TO_LOGS_FOLDER);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error setting up file logging", e);
        }
        return logger;
    }
}
