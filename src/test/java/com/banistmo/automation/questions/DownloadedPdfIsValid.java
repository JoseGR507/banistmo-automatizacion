
package com.banistmo.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadedPdfIsValid implements Question<Boolean> {

    private static final String PDF_NAME = "Prohibiciones.pdf";

    public static Question<Boolean> inDownloads() {
        return new DownloadedPdfIsValid();
    }

    @Override
    public Boolean answeredBy(Actor actor) {

        Path pdfPath = Path.of(
            System.getProperty("user.home"),
            "Downloads",
            PDF_NAME
        );

        System.out.println("\n========== VALIDACIÓN DEL PDF ==========");
        System.out.println("Ruta esperada: " + pdfPath);

        try {
            // 1. Verificar que el archivo exista
            if (!Files.exists(pdfPath)) {
                System.out.println("ERROR: No se encontró el PDF.");
                return false;
            }

            // 2. Verificar que no esté vacío
            long fileSize = Files.size(pdfPath);

            System.out.println("Tamaño: " + fileSize + " bytes");

            if (fileSize <= 1024) {
                System.out.println("ERROR: El archivo está vacío o incompleto.");
                return false;
            }

            // 3. Verificar la firma del PDF
            byte[] signature = Files.readAllBytes(pdfPath);

            String header = new String(
                signature,
                0,
                Math.min(signature.length, 5),
                java.nio.charset.StandardCharsets.US_ASCII
            );

            if (!header.equals("%PDF-")) {
                System.out.println("ERROR: El archivo no tiene una firma PDF válida.");
                return false;
            }

            // 4. Abrir y validar la estructura del documento
            try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

                int pages = document.getNumberOfPages();

                System.out.println("Páginas: " + pages);

                if (pages <= 0) {
                    System.out.println("ERROR: El PDF no contiene páginas.");
                    return false;
                }

                // 5. Extraer texto y comprobar el contenido
                PDFTextStripper stripper = new PDFTextStripper();
                String content = stripper.getText(document);

                boolean containsProhibitions =
                    content.toLowerCase().contains("prohibiciones");

                System.out.println(
                    "Contiene la palabra 'Prohibiciones': "
                    + containsProhibitions
                );

                System.out.println("========== FIN VALIDACIÓN ==========\n");

                return containsProhibitions;
            }

        } catch (Exception e) {
            System.out.println("ERROR validando el PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
