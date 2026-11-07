
package com.banistmo.automation.stepdefinitions;

import com.banistmo.automation.questions.DownloadedPdfIsValid;
import com.banistmo.automation.tasks.DownloadProhibitions;
import com.banistmo.automation.tasks.NavigateToRegularAutoLoans;
import com.banistmo.automation.tasks.OpenBanistmo;
import com.banistmo.automation.tasks.OpenRatesAndFees;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.is;

@ExtendWith(SerenityJUnit5Extension.class)
class BanistmoProhibitionsTest {

    @net.serenitybdd.annotations.Managed(driver = "edge")
    WebDriver browser;

    private Actor usuario;

    @BeforeEach
    void prepararActor() {
        usuario = Actor.named("Usuario Banistmo");
        usuario.can(BrowseTheWeb.with(browser));
    }

    @Test
    void debe_descargar_y_validar_el_pdf_prohibiciones() {

        usuario.attemptsTo(
                // 1. Abrir Banistmo
                OpenBanistmo.home(),

                // 2. Navegar a préstamos de automóviles regulares
                NavigateToRegularAutoLoans.fromMenu(),

                // 3. Abrir la pestaña Tasas y tarifas
                OpenRatesAndFees.now(),

                // 4. Descargar Prohibiciones
                DownloadProhibitions.now()
        );

        // 5. Validar el PDF descargado
        then(usuario).should(seeThat(
                "el PDF descargado corresponde a Prohibiciones",
                DownloadedPdfIsValid.inDownloads(),
                is(true)
        ));
    }
}
