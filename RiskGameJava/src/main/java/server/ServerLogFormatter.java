package server;

import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerLogFormatter extends Formatter {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String time = sdf.format(new Date(record.getMillis()));
        String level = String.format("%-7s", record.getLevel());
        String logger = record.getLoggerName();
        String msg = formatMessage(record);
        String thrown = "";
        if (record.getThrown() != null) {
            thrown = " | " + record.getThrown();
        }
        return String.format("[%s] [%s] [%s] %s%s%n", time, level, logger, msg, thrown);
    }
} 
