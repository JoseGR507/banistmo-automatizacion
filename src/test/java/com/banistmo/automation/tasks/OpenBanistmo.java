package com.banistmo.automation.tasks;

import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

public class OpenBanistmo {
    public static Task home() {
        return Task.where("{0} abre el portal de personas de Banistmo",
                Open.url("https://www.banistmo.com/personas/"));
    }
}
