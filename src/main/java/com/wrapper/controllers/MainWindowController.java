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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindowController {
    @FXML
    private Hyperlink githubLink = new Hyperlink();
    @FXML
    private ComboBox<String> vmList = new ComboBox<>();
    @FXML
    private TextField vmNameEditionField = new TextField();
    @FXML
    private TextField memoryEditionField = new TextField();
    @FXML
    private TextField cpuField = new TextField();

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
        this.loadVirtualMachines();
    }

    private void loadVirtualMachines() {
        try {
            // Command to list virtual machines
            Process process = Runtime.getRuntime().exec("VBoxManage list vms");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract the name of the virtual machine
                String vmName = line.split("\"")[1];
                vmList.getItems().add(vmName);
            }
        } catch (IOException e) {
            // showError("Failed to load virtual machines: " + e.getMessage());
            System.out.println("Failed to load virtual machines: " + e.getMessage());
            ;
        }
    }

    @FXML
    private void applyChanges() {
        String selectedVM = vmList.getValue();
        String newName = vmNameEditionField.getText();
        String memory = memoryEditionField.getText();
        String cpuCount = cpuField.getText();

        if (selectedVM == null) {
            showError("Please select a virtual machine.");
            return;
        }

        try {
            // Modify the VM name
            if (!newName.isEmpty()) {
                Process renameProcess = Runtime.getRuntime().exec(
                        "VBoxManage modifyvm \"" + selectedVM + "\" --name \""
                                + newName + "\"");
                renameProcess.waitFor();
            }

            // Modify the VM memory
            if (!memory.isEmpty()) {
                Process memoryProcess = Runtime.getRuntime().exec(
                        "VBoxManage modifyvm \"" + selectedVM + "\" --memory "
                                + memory);
                memoryProcess.waitFor();
            }

            // Modify the VM CPU count
            if (!cpuCount.isEmpty()) {
                Process cpuProcess = Runtime.getRuntime().exec(
                        "VBoxManage modifyvm \"" + selectedVM + "\" --cpus "
                                + cpuCount);
                cpuProcess.waitFor();
            }

            vmList.getItems().clear();
            vmNameEditionField.clear();
            memoryEditionField.clear();
            cpuField.clear();

            showSuccess("Changes applied successfully!");
        } catch (IOException | InterruptedException e) {
            showError("Failed to apply changes: " + e.getMessage());
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
}