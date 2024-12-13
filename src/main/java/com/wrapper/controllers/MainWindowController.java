package com.wrapper.controllers;

import com.wrapper.business.VirtualBoxManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MainWindowController {

    @FXML
    private TextField vmNameField;   // Champ pour le nom de la VM

    @FXML
    private TextField isoPathField;  // Champ pour le chemin de l'ISO

    @FXML
    private TextField vramField;      // Champ pour la RAM

    @FXML
    private TextField memoryField;  // Champ pour le stockage

    @FXML
    private Button createButton;     // Bouton pour créer la VM

    // Instance de VirtualBoxManager pour gérer la logique métier
    private VirtualBoxManager virtualBoxManager;

    // Constructeur
    public MainWindowController() {
        this.virtualBoxManager = new VirtualBoxManager();
    }

    // Méthode pour gérer la création de la VM
    @FXML
    private void handleCreateVM(ActionEvent event) {
        String vmName = vmNameField.getText();
        String isoPath = isoPathField.getText();
        String ram = vramField.getText();
        String storage = memoryField.getText();

        // Vérifie que les champs ne sont pas vides
        if (vmName.isEmpty() || isoPath.isEmpty() || ram.isEmpty() || storage.isEmpty()) {
            System.out.println("Please fill in all fields!");
            return;
        }

        try {
            // Utilise VirtualBoxManager pour créer la VM
            virtualBoxManager.createVM(vmName, isoPath, ram, storage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating VM: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
