package com.libman.display.controller;

// clang-format off
import java.net.URL;
import java.util.ResourceBundle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
// clang-format on

public abstract class StageController implements Initializable
{
    @FXML private Label clock;

    @Override public void initialize(URL address, ResourceBundle Resource)
    {
        // Create clock using FXAnimation and simple lambda expression
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0), event -> {
                LocalTime currentTime = LocalTime.now();
                this.clock.setText(currentTime.format(DateTimeFormatter.ofPattern("h:mm a")).toUpperCase());
            }), new KeyFrame(Duration.seconds(1)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        try
        {
            this.show();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    protected void show() throws Exception{};
}
