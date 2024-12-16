package com.wrapper.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

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
     * Allows you to retrieve the list of available virtual machines
     * 
     * @return List of available virtual machines
     */
    public List<String> getAvailableVirtualMachines() {
        List<String> vmList = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("VBoxManage", "list", "vms").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                // Extract VM name from the line and add to the list
                String vmName = line.split("\"")[1];
                vmList.add(vmName);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Error getting VMs: " + e.getMessage(), e);
        }

        return vmList;
    }

    /**
     * Allows you to edit a VM in VirtualBox
     */
    public void editVirtualMachine(String originalVmName, String newVmName, String newMemory, String newCpuCount) {
        try {
            // Modify the VM name
            if (newVmName != null && !newVmName.isEmpty())
                executeCommand(new String[] { "VBoxManage", "modifyvm", originalVmName, "--name", newVmName });

            // Modify the VM memory
            if (newVmName != null && !newVmName.isEmpty())
                executeCommand(new String[] { "VBoxManage", "modifyvm", originalVmName, "--memory", newVmName });

            // Modify the VM CPU count
            if (newVmName != null && !newVmName.isEmpty())
                executeCommand(new String[] { "VBoxManage", "modifyvm", originalVmName, "--cpus", newVmName });

        } catch (IOException e) {
            throw new IllegalStateException("Error editing VM: " + e.getMessage(), e);
        }
    }

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
