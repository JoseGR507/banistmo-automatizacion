
package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class NavigateToRegularAutoLoans implements Task {

    private static final Target PRODUCTS_MENU =
            Target.the("menú Productos y servicios")
                    .locatedBy(
                        "//*[self::a or self::button or @role='button']" +
                        "[contains(translate(normalize-space(.)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚ'," +
                        "'abcdefghijklmnopqrstuvwxyzáéíóú'),'productos')" +
                        " and " +
                        "contains(translate(normalize-space(.)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚ'," +
                        "'abcdefghijklmnopqrstuvwxyzáéíóú'),'servicios')]"
                    );

    private static final Target AUTO_LOANS_MENU =
            Target.the("opción Préstamos de autos")
                    .locatedBy(
                        "//*[self::a or self::button]" +
                        "[contains(translate(normalize-space(.)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚ'," +
                        "'abcdefghijklmnopqrstuvwxyzáéíóú')," +
                        "'préstamos de autos')]"
                    );

    @Override
    public <T extends Actor> void performAs(T actor) {

        // 1. Obtener navegador
        var driver = BrowseTheWeb.as(actor).getDriver();

        // 2. Cerrar aviso de privacidad
        actor.attemptsTo(
                DismissPrivacyNotice.ifPresent()
        );

        // 3. Cerrar ventana promocional
        actor.attemptsTo(
                DismissPromotionalPopup.ifPresent()
        );

        // 4. Entrar a Productos y servicios
        actor.attemptsTo(
                WaitUntil.the(PRODUCTS_MENU, isVisible())
                        .forNoMoreThan(20).seconds(),
                Click.on(PRODUCTS_MENU)
        );

        // 5. Seleccionar Préstamos de autos
        actor.attemptsTo(
                WaitUntil.the(AUTO_LOANS_MENU, isVisible())
                        .forNoMoreThan(15).seconds(),
                Click.on(AUTO_LOANS_MENU)
        );

        // 6. Esperar que cargue la página de préstamos
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        System.out.println("\n========== INSPECCIÓN BANISTMO ==========");
        System.out.println("URL: " + driver.getCurrentUrl());

        System.out.println("\nCONTENIDO:");
        System.out.println(
                driver.findElement(By.tagName("body")).getText()
        );

        // 7. Localizar los botones Descubre más
        List<WebElement> botones = driver.findElements(
                By.cssSelector(
                    "button[data-testid='ml-card-actions-button']"
                )
        );

        System.out.println("\n========== TARJETAS DE PRÉSTAMOS ==========");

        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int i = 0; i < botones.size(); i++) {

            WebElement boton = botones.get(i);

            System.out.println("\nBOTÓN " + (i + 1));
            System.out.println("Texto: " + boton.getText());
            System.out.println("Visible: " + boton.isDisplayed());

            String informacion = (String) js.executeScript(
                "let e = arguments[0];" +
                "let resultado = '';" +
                "for(let i=0; e && i<6; i++, e=e.parentElement) {" +
                " resultado += '\\nNivel ' + i +" +
                " ' | TAG: ' + e.tagName +" +
                " ' | CLASE: ' + e.className +" +
                " ' | TEXTO: ' + e.innerText;" +
                "}" +
                "return resultado;",
                boton
            );

            System.out.println(informacion);
        }

        System.out.println("\n========== FIN INSPECCIÓN ==========");

        // 8. Buscar la tarjeta de préstamos regulares
        List<WebElement> botonesActualizados = driver.findElements(
                By.cssSelector(
                    "button[data-testid='ml-card-actions-button']"
                )
        );

        WebElement botonRegular = null;

        for (WebElement boton : botonesActualizados) {

            String textoTarjeta = (String) js.executeScript(
                "let e = arguments[0];" +
                "for(let i=0; e && i<6; i++, e=e.parentElement) {" +
                " if((e.innerText || '').toLowerCase().includes('préstamos de automóviles regulares')) {" +
                "   return e.innerText;" +
                " }" +
                "}" +
                "return '';",
                boton
            );

            if (textoTarjeta != null &&
                textoTarjeta.toLowerCase().contains(
                    "préstamos de automóviles regulares"
                )) {

                botonRegular = boton;
                break;
            }
        }

        // 9. Desplazar hasta el botón y hacer clic
        if (botonRegular == null) {
            throw new AssertionError(
                    "No se encontró el botón Descubre más " +
                    "asociado a Préstamos de Automóviles Regulares. " +
                    "Revisa la inspección de las tarjetas."
            );
        }

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                botonRegular
        );

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        botonRegular.click();

        System.out.println(
                "Se hizo clic en Descubre más de Préstamos de Automóviles Regulares."
        );
    }

    public static NavigateToRegularAutoLoans fromMenu() {
        return new NavigateToRegularAutoLoans();
    }
}
