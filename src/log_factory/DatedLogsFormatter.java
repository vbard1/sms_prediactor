package src.log_factory;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DatedLogsFormatter extends Formatter  {
    private final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        String formattedDate = String.format("%-" + DATE_PATTERN.length() + "s", dateFormat.format(new Date(record.getMillis())));
        builder.append(formattedDate).append(" : "); 
        builder.append(record.getLevel()).append(" "); 
        builder.append(record.getMessage());
        builder.append("\n");
        return builder.toString();
    }

}
