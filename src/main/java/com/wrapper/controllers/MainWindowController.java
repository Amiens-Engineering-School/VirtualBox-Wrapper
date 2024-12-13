package com.wrapper.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindowController
{
    @FXML
    private Hyperlink githubLink = new Hyperlink();

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
}
