package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import org.akash.cryptomanagerdesktop.service.FileMatchService;
import org.akash.cryptomanagerdesktop.service.PdfSofParserService;
import org.akash.cryptomanagerdesktop.service.RarExtractorService;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SofController {
    public TextArea resultArea;
    public Label zipFileLabel;
    public Label sofPdfLabel;


    private File sofPdfFile;
    private File rarFile;

    public SofController( TextArea resultArea, Label sofPdfLabel, Label zipFileLabel) {
        this.resultArea = resultArea;
        this.sofPdfLabel = sofPdfLabel;
        this.zipFileLabel = zipFileLabel;
    }

    public void uploadSofPdf() {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files","*.pdf")

        );

        File file = chooser.showOpenDialog(null);
        if (file!=null){
            sofPdfFile=file;
            sofPdfLabel.setText(file.getName());
        }else {
            sofPdfLabel.setText("");
        }
    }

    public void uploadRar() {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP Files","*.zip")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            rarFile = file;
            zipFileLabel.setText(file.getName());
        } else {
            zipFileLabel.setText("");
        }
    }

    public void validateFiles() {
        if(sofPdfFile==null || rarFile==null){
            resultArea.setText("❌ Please upload both SOF PDF and RAR file.");
            return;
        }
        List<String> extensions = askUserForExtensions();
        if (extensions.isEmpty()) {
            resultArea.setText("❌ No extensions provided.");
            return;
        }

        try {
            PdfSofParserService pdfService = new PdfSofParserService();
            RarExtractorService rarService = new RarExtractorService();
            FileMatchService matchService = new FileMatchService();
            List<String> sofFileNames=pdfService.extractInputFileNames(sofPdfFile, extensions);
            List<String> rarFileNames = rarService.extractFileNames(rarFile);
            String report = matchService.compare(sofFileNames, rarFileNames);
            resultArea.setText(report);
        }catch (Exception e){
            resultArea.setText("error: "+e.getMessage());
        }
    }

    private List<String> askUserForExtensions() {
        TextInputDialog dialog = new TextInputDialog("dat,inp");
        dialog.setTitle("File Extension Input");
        dialog.setHeaderText("Enter allowed file extensions");
        dialog.setContentText("Extensions (comma separated):");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(result.get().split(","))
                .map(String::trim)
                .toList();
    }
}
