package com.wrapper.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindowController
{
    @FXML
    private Hyperlink githubLink = new Hyperlink();
    @FXML
    private ComboBox<String> vmList = new ComboBox<>();
    @FXML
    private TextField vmNameField = new TextField();
    @FXML
    private TextField memoryField = new TextField();
    @FXML
    private TextField cpuField = new TextField();

    @FXML
    public void onGithubLinkClicked()
    {
        String url = "https://github.com/Amiens-Engineering-School/virtualbox-wrapper";
        githubLink.setOnAction(e -> {
            try
            {
                Desktop.getDesktop().browse(new URI(url));
            }
            catch (IOException | URISyntaxException ex)
            {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize()
    {
        // Load the list of virtual machines
        vmList.getItems().clear();
        this.loadVirtualMachines();
    }

    private void loadVirtualMachines() {
        try
        {
            // Command to list virtual machines
            Process process = Runtime.getRuntime().exec("C:/Program Files/Oracle/VirtualBox/VBoxManage list vms");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
            {
                // Extract the name of the virtual machine
                String vmName = line.split("\"")[1];
                vmList.getItems().add(vmName);
            }
        }
        catch (IOException e)
        {
            //showError("Failed to load virtual machines: " + e.getMessage());
            System.out.println("Failed to load virtual machines: " + e.getMessage());;
        }
    }


    @FXML
    private void applyChanges()
    {
        String selectedVM = vmList.getValue();
        String newName = vmNameField.getText();
        String memory = memoryField.getText();
        String cpuCount = cpuField.getText();

        if (selectedVM == null)
        {
            showError("Please select a virtual machine.");
            return;
        }

        try
        {
            // Modify the VM name
            if (!newName.isEmpty())
            {
                Process renameProcess = Runtime.getRuntime().exec(
                        "C:/Program Files/Oracle/VirtualBox/VBoxManage modifyvm \"" + selectedVM + "\" --name \"" + newName + "\""
                );
                renameProcess.waitFor();
            }

            // Modify the VM memory
            if (!memory.isEmpty())
            {
                Process memoryProcess = Runtime.getRuntime().exec(
                        "C:/Program Files/Oracle/VirtualBox/VBoxManage modifyvm \"" + selectedVM + "\" --memory " + memory
                );
                memoryProcess.waitFor();
            }

            // Modify the VM CPU count
            if (!cpuCount.isEmpty())
            {
                Process cpuProcess = Runtime.getRuntime().exec(
                        "C:/Program Files/Oracle/VirtualBox/VBoxManage modifyvm \"" + selectedVM + "\" --cpus " + cpuCount
                );
                cpuProcess.waitFor();
            }

            vmList.getItems().clear();
            vmNameField.clear();
            memoryField.clear();
            cpuField.clear();

            showSuccess("Changes applied successfully!");
        }
        catch (IOException | InterruptedException e)
        {
            showError("Failed to apply changes: " + e.getMessage());
        }
    }

    private void showError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
