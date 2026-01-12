package org.akash.cryptomanagerdesktop.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSofParserService {

    public List<String> extractInputFileNames(
            File pdfFile,
            List<String> extensions) throws IOException {

        Set<String> fileNames = new HashSet<>();

        String extRegex = extensions.stream()
                .map(Pattern::quote)
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        Pattern pattern = Pattern.compile(
                "\\b[\\w\\-]+\\.(" + extRegex + ")\\b"
        );

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                fileNames.add(matcher.group());
            }
        }
        return new ArrayList<>(fileNames);
    }
}
