package com.Usaid_Syed.taskforge.CSV;

import com.Usaid_Syed.taskforge.task.TaskResponse;

import java.util.List;

public class TaskCsvWriter {
    private static final String HEADER = "id,title,description,status,createdAt";

    private TaskCsvWriter() {}

    public static String toCsv(List<TaskResponse> tasks) {
        StringBuilder sb = new StringBuilder(HEADER).append("\r\n");
        for (TaskResponse t : tasks) {
            sb.append(t.id()).append(',')
                    .append(escape(t.title())).append(',')
                    .append(escape(t.description())).append(',')
                    .append(t.status()).append(',')
                    .append(t.createdAt()).append("\r\n");
        }
        return sb.toString();
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        String v = value;

        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }

        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("/r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }

        return v;
    }
}
