
package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DismissPromotionalPopup implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {

        var driver = BrowseTheWeb.as(actor).getDriver();

        // Buscar botones de cierre de la ventana promocional
        var closeButtons = driver.findElements(By.xpath(
            "//button[@aria-label='Cerrar' or " +
            "@aria-label='Close' or " +
            "@title='Cerrar' or " +
            "@title='Close' or " +
            "@aria-label='Close modal' or " +
            "@aria-label='Cerrar modal']"
        ));

        for (WebElement button : closeButtons) {
            if (button.isDisplayed() && button.isEnabled()) {
                button.click();
                return;
            }
        }

        // Si no se encuentra un botón identificado,
        // informar para revisar el selector.
        System.out.println(
            "No se encontró el botón de cierre del popup promocional."
        );
    }

    public static DismissPromotionalPopup ifPresent() {
        return instrumented(DismissPromotionalPopup.class);
    }
}
