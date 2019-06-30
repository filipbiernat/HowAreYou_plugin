package com.aware.plugin.howareyou.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class LogsUtil {

    public static StringBuilder readLogs() {
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
                if (line.contains("System.err: HEART") &&
                        !line.contains("satisfied with certainty (1.0).") &&
                        !line.contains("Checking condition ")) {
                    if (logBuilder.length() != 0)
                    {
                        int startIndex = line.lastIndexOf("System.err: HEART: ") + 19;
                        if (startIndex != -1) {
                            String prefix = "System.err: HEART: ";
                            String string = line.substring(line.lastIndexOf(prefix) + prefix.length());
                            if (string.contains("Finished evaluating rule")){
                                string += '\n';
                            }
                            if (string.contains("Processing table") && string.contains("finished")){
                                string += '\n';
                                string += '\n';
                            }
                            if (string != null){
                                logBuilder.append(string + "\n");
                            }
                        }
                    } else {
                        logBuilder.append(line + "\n");
                    }
                }
            }
        } catch (IOException e) {
        }
        return logBuilder;
    }
}