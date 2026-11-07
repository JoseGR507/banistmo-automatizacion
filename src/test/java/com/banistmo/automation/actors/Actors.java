package com.banistmo.automation.actors;

import net.serenitybdd.screenplay.Actor;

public final class Actors {
    private Actors() {}

    public static Actor cliente() {
        return Actor.named("Usuario Banistmo");
    }
}
