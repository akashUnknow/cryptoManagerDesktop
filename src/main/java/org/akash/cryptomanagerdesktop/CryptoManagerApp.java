package org.akash.cryptomanagerdesktop;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.security.Security;

public class CryptoManagerApp extends Application {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml")
        );

        Scene scene = new Scene(loader.load(), 1400, 900);

        // Load CSS
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("CryptoManager - Professional Cryptography Suite");
        primaryStage.setScene(scene);

        // Set application icon
//        try {
//            primaryStage.getIcons().add(
//                    new Image(getClass().getResourceAsStream("/images/icon.png"))
//            );
//        } catch (Exception e) {
//            // Icon loading failed, continue without it
//        }
        Screen screen= Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        if (screenWidth <= 1600) {
            // LAPTOP — USE FULL SCREEN
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
        } else {
            // DESKTOP — USE 70% WINDOW SIZE
            double newWidth = screenWidth * 0.70;
            double newHeight = screenHeight * 0.80;

            primaryStage.setWidth(newWidth);
            primaryStage.setHeight(newHeight);

            // Center window
            primaryStage.setX(bounds.getMinX() + (screenWidth - newWidth) / 2);
            primaryStage.setY(bounds.getMinY() + (screenHeight - newHeight) / 2);
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}