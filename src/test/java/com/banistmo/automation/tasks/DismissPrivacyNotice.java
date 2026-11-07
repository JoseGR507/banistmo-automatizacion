
package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DismissPrivacyNotice implements Task {

    private static final Target BUTTON_ENTENDIDO =
        Target.the("botón Entendido del aviso de privacidad")
            .locatedBy("//button[normalize-space()='Entendido']");

    @Override
    public <T extends Actor> void performAs(T actor) {

        var driver = BrowseTheWeb.as(actor).getDriver();

        var buttons = driver.findElements(
            By.xpath("//button[normalize-space()='Entendido']")
        );

        if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
            actor.attemptsTo(
                Click.on(BUTTON_ENTENDIDO)
            );
        }
    }

    public static DismissPrivacyNotice ifPresent() {
        return instrumented(DismissPrivacyNotice.class);
    }
}
