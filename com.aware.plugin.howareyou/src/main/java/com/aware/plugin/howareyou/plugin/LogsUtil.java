package com.aware.plugin.howareyou.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class LogsUtil {
    private static final int MAX_LOGCAT_ENTRIES = 200;

    public static StringBuilder readReasoningLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Job executed")){
                    logBuilder = new StringBuilder();
                }
                if  (line.contains("System.err: HEART") &&
                     !line.contains("fired.") &&
                        (line.contains("Processing table ") ||
                         line.contains("Condition ") ||
                         line.contains("Finished evaluating ") ||
                         line.contains("Rule ")
                        )
                    )
                {
                    if (logBuilder.length()== 0)
                    {
                        logBuilder.append("Timestamp: " + line.substring(0, 18) + "\n\n");
                    }

                    int startIndex = line.lastIndexOf("System.err: HEART: ") + 19;
                    if (startIndex != -1) {
                        String prefix = "System.err: HEART: ";
                        String string = line.substring(line.lastIndexOf(prefix) + prefix.length());
                        if (string.contains("Finished evaluating rule")){
                            string += '\n';
                        }
                        if (string.contains("Processing table") && string.contains("finished")){
                            string = "";
                        }
                        if (string != null){
                            string = string.replaceAll(" \\(ID: null\\)|satisfied with certainty |\\. SATISFIED with|certainty\\.|Processing |Finished evaluating |Condition ","");
                            string = string.replaceAll("rule", "        Rule");
                            string = string.replaceAll("\\(1.0\\)", "--> TRUE");
                            string = string.replaceAll("\\(0.0\\)", "--> FALSE");
                            string = string.replaceAll("(?<!\\d)\\.(?!\\d)", "");
                            if (string.contains("table")){
                                string = string.toUpperCase();
                            }

                            logBuilder.append(string + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return logBuilder;
    }

    public static StringBuilder readApplicationLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logBuilder.append(line + "\n");
            }
        } catch (IOException e) {
        }

        String fullLog = logBuilder.toString();
        String[] logTable = fullLog.split("\\r?\\n");;
        StringBuilder logBuilder2 = new StringBuilder();

        int cnt = 0;
        for (int i = logTable.length - 1; i >= 0; i--) {
            logBuilder2.append(logTable[i]).append("\n\n");
            ++cnt;
            if (cnt > MAX_LOGCAT_ENTRIES){
                break;
            }
        }
        return logBuilder2;
    }
}