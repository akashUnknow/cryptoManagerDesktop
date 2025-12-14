package org.akash.cryptomanagerdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Objects;

public class CryptoManagerApp extends Application {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            System.out.println("BouncyCastle provider registered successfully");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png"))));
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml")
        );

        Scene scene = new Scene(loader.load(), 1400, 900);

        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("CryptoManager - Professional Cryptography Suite");
        primaryStage.setScene(scene);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        if (screenWidth <= 1600) {
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
        } else {
            double newWidth = screenWidth * 0.70;
            double newHeight = screenHeight * 0.80;
            primaryStage.setWidth(newWidth);
            primaryStage.setHeight(newHeight);
            primaryStage.setX(bounds.getMinX() + (screenWidth - newWidth) / 2);
            primaryStage.setY(bounds.getMinY() + (screenHeight - newHeight) / 2);
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}