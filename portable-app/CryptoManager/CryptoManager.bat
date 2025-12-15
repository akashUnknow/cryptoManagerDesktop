@echo off
cd /d "%~dp0"
jre\bin\java.exe -Xms128m -Xmx512m --module-path lib --add-modules javafx.controls,javafx.fxml -cp "cryptoManagerDesktop-1.0.0.jar;lib\*" org.akash.cryptomanagerdesktop.CryptoManagerApp
