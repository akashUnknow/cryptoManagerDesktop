module org.akash.cryptomanagerdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // If you use BouncyCastle
    requires org.bouncycastle.provider;

    // JavaFX must access controllers via reflection
    opens org.akash.cryptomanagerdesktop.controller to javafx.fxml;
    opens org.akash.cryptomanagerdesktop to javafx.fxml;

    // Export only if other modules need it
    exports org.akash.cryptomanagerdesktop;
}
