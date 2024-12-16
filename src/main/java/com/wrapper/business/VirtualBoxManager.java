package com.wrapper.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VirtualBoxManager {
    /**
     * Allows you to clone an existing VM
     * 
     * @param vmName
     * @param cloneName
     */
    public void clone(String vmName, String cloneName) {
        try {
            executeCommand(new String[] { "VBoxManage", "clonevm", vmName, "--name", cloneName, "--register" });

        } catch (IOException e) {
            throw new IllegalStateException("Error cloning VM: " + e.getMessage(), e);
        }
    }

    /**
     * Allows you to execute a command in the terminal
     * 
     * @param command
     * @throws IOException
     */
    private void executeCommand(String[] commandArray) throws IOException {
        Process process = new ProcessBuilder(commandArray).start();

        System.out.println("Executing command: " + String.join(" ", commandArray));

        Thread outputThread = new Thread(() -> printStream(process.getInputStream(), "OUTPUT"));
        Thread errorThread = new Thread(() -> printStream(process.getErrorStream(), "ERROR"));

        outputThread.start();
        errorThread.start();

        try {
            int exitCode = process.waitFor();
            outputThread.join();
            errorThread.join();
            if (exitCode != 0) {
                throw new IllegalStateException(
                        "Command failed with exit code " + exitCode + ": " + String.join(" ", commandArray));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Command execution interrupted: " + String.join(" ", commandArray), e);
        }
    }

    /**
     * Method to print the output of the command
     * 
     * @param inputStream
     * @param streamType
     */
    private void printStream(InputStream inputStream, String streamType) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[" + streamType + "] " + line);
            }
        } catch (IOException e) {
            System.err.println("Error reading " + streamType + ": " + e.getMessage());
        }
    }
}
