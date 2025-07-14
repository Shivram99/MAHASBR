package com.mahasbr.util;

import java.io.FileWriter;
import java.io.IOException;

public class ErrorInfoFile {

	private void writeErrorLog(String errorMessages) {
        if (!errorMessages.isEmpty()) {
            try (FileWriter fileWriter = new FileWriter("error_log.txt")) {
                fileWriter.write(errorMessages);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
