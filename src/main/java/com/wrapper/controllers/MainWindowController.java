package com.wrapper.controllers;

import com.wrapper.business.VirtualBoxManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;

public class MainWindowController {

    @FXML
    private TextField vmNameField; // Field for the VM name

    @FXML
    private TextField isoPathField; // Field for the ISO path

    @FXML
    private TextField vramField; // Field for the RAM

    @FXML
    private TextField memoryField; // Field for the storage

    @FXML
    private Button createButton; // Button to create the VM

    // Instance of VirtualBoxManager to handle business logic
    private VirtualBoxManager virtualBoxManager;

    // Constructor
    public MainWindowController() {
        this.virtualBoxManager = new VirtualBoxManager();
    }

    /**
     * Handle the browse ISO button click event
     */
    @FXML
    public void handleBrowseIso() {
        // Initialize a file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ISO File");

        // Add a filter to show only ISO files
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ISO Files", "*.iso"));

        // Open the dialog to select a file
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        // If a file has been selected, update the text field
        if (selectedFile != null) {
            isoPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Handle the create VM button click event
     * 
     * @param event
     */
    @FXML
    private void handleCreateVM(ActionEvent event) {
        String vmName = vmNameField.getText();
        String isoPath = isoPathField.getText();
        String ram = vramField.getText();
        String storage = memoryField.getText();

        // Check that the fields are not empty
        if (vmName.isEmpty() || isoPath.isEmpty() || ram.isEmpty() || storage.isEmpty()) {
            System.out.println("Please fill in all fields!");
            return;
        }

        try {
            // Use VirtualBoxManager to create the VM
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
