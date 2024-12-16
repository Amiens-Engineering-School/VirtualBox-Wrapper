package com.wrapper.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VirtualBoxManager {

    /**
     * Method to create a VM in VirtualBox
     * 
     * @param vmName
     * @param isoPath
     * @param ram
     * @param storage
     * @throws IOException
     */
    public void createVM(String vmName, String isoPath, String ram, String storage) throws IOException {
        if (vmName == null || vmName.isEmpty() || isoPath == null || isoPath.isEmpty() || ram == null || ram.isEmpty()
                || storage == null || storage.isEmpty()) {
            throw new IllegalArgumentException("VM Name, ISO Path, RAM and Storage cannot be empty");
        }

        String vdiPath = String.format("%s/VirtualBox-VMs/%s/%s.vdi", System.getProperty("user.home"), vmName, vmName);
        if (Files.exists(Paths.get(vdiPath))) {
            throw new IllegalStateException("The virtual disk already exists at: " + vdiPath);
        }

        String[] createVMCommand = { "VBoxManage", "createvm", "--name", vmName, "--register" };
        String[] setMemoryCommand = { "VBoxManage", "modifyvm", vmName, "--memory", ram };
        String[] setStorageCommand = { "VBoxManage", "createhd", "--filename", vdiPath, "--size", storage };
        String[] attachIsoCommand = { "VBoxManage", "storagectl", vmName, "--name", "IDE Controller", "--add", "ide",
                "--controller", "PIIX4" };
        String[] attachIsoDriveCommand = { "VBoxManage", "storageattach", vmName, "--storagectl", "IDE Controller",
                "--port", "0",
                "--device", "0", "--type", "dvddrive", "--medium", isoPath };

        executeCommand(createVMCommand);
        executeCommand(setMemoryCommand);
        executeCommand(setStorageCommand);
        executeCommand(attachIsoCommand);
        executeCommand(attachIsoDriveCommand);

        System.out.println("VM Created Successfully: " + vmName);
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
