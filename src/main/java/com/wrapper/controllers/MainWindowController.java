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

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindowController {
    @FXML
    private Hyperlink githubLink = new Hyperlink();

    @FXML
    private ComboBox<String> vmList = new ComboBox<>();

    @FXML
    private TextField vmNameField = new TextField();

    @FXML
    private TextField memoryField = new TextField();

    private TextField vmNameEditionField = new TextField();

    @FXML
    private TextField memoryEditionField = new TextField();

    @FXML
    private TextField cpuField = new TextField();

    @FXML
    private TextField originalVmNameField; // Field to enter the name of the original VM for cloning

    @FXML
    private TextField cloneVmNameField; // Field to enter the name of the cloned VM

    @FXML
    private TextField vmNameCreationField; // Field for the VM name during creation

    @FXML
    private TextField isoPathField; // Field for the ISO path during creation

    @FXML
    private TextField vramField; // Field for the RAM during creation

    @FXML
    private TextField memoryCreationField; // Field for the storage during creation

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
        String vmName = vmNameCreationField.getText();
        String isoPath = isoPathField.getText();
        String ram = vramField.getText();
        String storage = memoryCreationField.getText();

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

    @FXML
    public void onGithubLinkClicked() {
        String url = "https://github.com/Amiens-Engineering-School/virtualbox-wrapper";
        githubLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize() {
        // Load the list of virtual machines
        vmList.getItems().clear();
        this.addVirtualMachinesToCombobox();
    }

    private void addVirtualMachinesToCombobox() {
        try {
            // Retrieve the list of available virtual machines
            vmList.getItems().addAll(virtualBoxManager.getAvailableVirtualMachines());

        } catch (IllegalStateException e) {
            showError("Failed to load virtual machines: " + e.getMessage());
        }
    }

    /**
     * Handle the edit VM button click event
     */
    @FXML
    private void handleEditVirtualMachineButton() {
        String vmName = vmList.getValue();
        String newVmName = vmNameEditionField.getText();
        String newMemory = memoryEditionField.getText();
        String newCpuCount = cpuField.getText();

        if (vmName == null || vmName.isEmpty()) {
            showError("Please select a VM to edit.");
            return;
        }

        try {
            virtualBoxManager.editVirtualMachine(vmName, newVmName, newMemory, newCpuCount);
            showSuccess("VM edited successfully!");

        } catch (IllegalStateException e) {
            showError("Failed to edit VM: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleCloneVirtualMachine() {
        String originalVmName = originalVmNameField.getText();
        String cloneVmName = cloneVmNameField.getText();

        if (originalVmName.isEmpty() || cloneVmName.isEmpty()) {
            showError("Please enter the original VM name and the clone VM name.");
            return;
        }

        try {
            virtualBoxManager.clone(originalVmName, cloneVmName);
            showSuccess("VM cloned successfully!");

        } catch (IllegalStateException e) {
            showError("Failed to clone VM: " + e.getMessage());
        }
    }
}
