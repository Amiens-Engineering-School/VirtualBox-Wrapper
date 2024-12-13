package com.wrapper.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VirtualBoxManager {
    public void createVM(String vmName, String osType, int memory, int vram) {
        try {
            // Commande pour créer une VM
            String command = String.format(
                "VBoxManage createvm --name %s --ostype %s --register", vmName, osType
            );
            executeCommand(command);

            // Commande pour configurer la VM
            command = String.format(
                "VBoxManage modifyvm %s --memory %d --vram %d --cpus 2 --nic1 nat", 
                vmName, memory, vram
            );
            executeCommand(command);
            
            System.out.println("VM created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode);
        }
    }
}
