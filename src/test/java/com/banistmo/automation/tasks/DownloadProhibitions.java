
package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Duration;
import java.util.List;

public class DownloadProhibitions implements Task {

    private static final By PROHIBITIONS_LINK =
            By.cssSelector(
                    "section[data-testid='_tabContent'] " +
                    "a[aria-label='Prohibiciones'][href*='Prohibiciones.pdf']"
            );

    @Override
    public <T extends Actor> void performAs(T actor) {

        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );

        System.out.println("\n========== DESCARGA PROHIBICIONES ==========");

        // Esperar a que aparezca el enlace dentro de Tasas y tarifas.
        wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        PROHIBITIONS_LINK
                )
        );

        List<WebElement> enlaces =
                driver.findElements(PROHIBITIONS_LINK);

        WebElement enlace = null;

        // Seleccionar el enlace visible.
        for (WebElement elemento : enlaces) {

            if (elemento.isDisplayed()) {
                enlace = elemento;
                break;
            }
        }

        if (enlace == null) {
            throw new AssertionError(
                    "No se encontró el enlace visible de Prohibiciones."
            );
        }

        final WebElement enlaceFinal = enlace;

        // Obtener la dirección real del PDF.
        String href = enlaceFinal.getAttribute("href");

        if (href == null || href.isBlank()) {
            throw new AssertionError(
                    "El enlace Prohibiciones no contiene una dirección URL."
            );
        }

        if (href.startsWith("//")) {
            href = "https:" + href;
        }

        System.out.println("URL del PDF: " + href);

        // Desplazar el enlace al centro de la pantalla.
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].scrollIntoView({" +
                        "block:'center'," +
                        "inline:'center'" +
                        "});",
                enlaceFinal
        );

        wait.until(d -> enlaceFinal.isDisplayed());

        // Ejecutar el clic sobre el enlace real.
        // Se evita el problema de interceptación de Selenium.
        js.executeScript(
                "arguments[0].click();",
                enlaceFinal
        );

        System.out.println("Clic ejecutado sobre Prohibiciones.");

        // Preparar la carpeta Descargas de Windows.
        Path carpetaDescargas = Path.of(
                System.getProperty("user.home"),
                "Downloads"
        );

        Path archivoPdf = carpetaDescargas.resolve(
                "Prohibiciones.pdf"
        );

        try {

            Files.createDirectories(carpetaDescargas);

            // Descargar el recurso PDF de Banistmo.
            HttpClient cliente = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(href))
                    .timeout(Duration.ofSeconds(40))
                    .header(
                            "User-Agent",
                            "Mozilla/5.0"
                    )
                    .GET()
                    .build();

            HttpResponse<byte[]> respuesta = cliente.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            System.out.println(
                    "Código HTTP: " + respuesta.statusCode()
            );

            System.out.println(
                    "Content-Type: " +
                    respuesta.headers()
                            .firstValue("Content-Type")
                            .orElse("No especificado")
            );

            if (respuesta.statusCode() != 200) {
                throw new AssertionError(
                        "No se pudo descargar el PDF. HTTP: " +
                        respuesta.statusCode()
                );
            }

            byte[] contenido = respuesta.body();

            // Comprobar que la respuesta realmente sea un PDF.
            if (contenido.length < 5) {
                throw new AssertionError(
                        "El archivo descargado está vacío o incompleto."
                );
            }

            String firma = new String(
                    contenido,
                    0,
                    5,
                    StandardCharsets.US_ASCII
            );

            if (!firma.equals("%PDF-")) {
                throw new AssertionError(
                        "El recurso recibido no tiene una firma PDF válida."
                );
            }

            // Guardar el PDF.
            Files.write(archivoPdf, contenido);

            System.out.println(
                    "PDF guardado correctamente: " + archivoPdf
            );

            System.out.println(
                    "Tamaño: " + Files.size(archivoPdf) + " bytes"
            );

        } catch (Exception e) {

            throw new AssertionError(
                    "Error durante la descarga de Prohibiciones: " +
                    e.getMessage(),
                    e
            );
        }

        System.out.println(
                "========== FIN DESCARGA PROHIBICIONES ==========\n"
        );
    }

    public static DownloadProhibitions now() {
        return new DownloadProhibitions();
    }
}
