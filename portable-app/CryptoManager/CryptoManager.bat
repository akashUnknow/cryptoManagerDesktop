@echo off
cd /d "%~dp0"
set LOG_DIR=logs
if not exist %LOG_DIR% mkdir %LOG_DIR%
set LOG_FILE=%LOG_DIR%\cryptomanager.log
start "" jre\bin\javaw.exe      -Xms128m -Xmx512m      --module-path lib      --add-modules javafx.controls,javafx.fxml      -cp "cryptoManagerDesktop-1.0.0.jar;lib\*"      org.akash.cryptomanagerdesktop.CryptoManagerApp      > "%LOG_FILE%" 2>&1
