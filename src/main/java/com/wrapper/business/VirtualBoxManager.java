package com.wrapper.business;

import java.io.IOException;

public class VirtualBoxManager {

    // Méthode pour créer une VM
    public void createVM(String vmName, String isoPath, String ram, String storage) throws IOException {
        if (vmName == null || vmName.isEmpty() || isoPath == null || isoPath.isEmpty() || ram == null || ram.isEmpty()
                || storage == null || storage.isEmpty()) {
            throw new IllegalArgumentException("VM Name, ISO Path, RAM and Storage cannot be empty");
        }

        // Commandes pour créer la VM et attacher l'ISO
        String createVMCommand = String.format("VBoxManage createvm --name %s --register", vmName);
        String setMemoryCommand = String.format("VBoxManage modifyvm %s --memory %s", vmName, ram); // Ajout de la RAM
        String setStorageCommand = String.format(
                "VBoxManage createhd --filename \"%s/VirtualBox-VMs/%s/%s.vdi\" --size %s",
                System.getProperty("user.home"), vmName, vmName, storage); // Création du disque dur
                                                                           // virtuel
        String attachIsoCommand = String
                .format("VBoxManage storagectl %s --name \"IDE Controller\" --add ide --controller PIIX4", vmName); // Contrôleur
                                                                                                                    // IDE
        String attachIsoDriveCommand = String.format(
                "VBoxManage storageattach %s --storagectl \"IDE Controller\" --port 0 --device 0 --type dvddrive --medium \"%s\"",
                vmName, isoPath); // Attachement de l'ISO

        // Exécution des commandes
        executeCommand(createVMCommand);
        executeCommand(setMemoryCommand); // Allocation de la RAM
        executeCommand(setStorageCommand); // Création du disque dur virtuel
        executeCommand(attachIsoCommand);
        executeCommand(attachIsoDriveCommand);

        System.out.println("VM Created Successfully: " + vmName);
    }

    // Méthode pour exécuter une commande shell
    private void executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);

        System.out.println("Executing command: " + command);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
