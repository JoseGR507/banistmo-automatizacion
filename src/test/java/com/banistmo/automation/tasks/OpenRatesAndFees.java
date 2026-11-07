
package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class OpenRatesAndFees implements Task {

    private static final Target RATES_AND_FEES_TAB =
            Target.the("pestaña Tasas y tarifas")
                    .locatedBy(
                        "li[data-testid*='Tabs > Tasas y tarifas']"
                    );

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(
                WaitUntil.the(RATES_AND_FEES_TAB, isVisible())
                        .forNoMoreThan(20).seconds(),

                Click.on(RATES_AND_FEES_TAB)
        );
    }

    public static OpenRatesAndFees now() {
        return new OpenRatesAndFees();
    }
}
