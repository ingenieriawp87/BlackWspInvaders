package com.bwspinv.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by wp90195 on 28/07/2017.
 */
public class Preferencias {

    private static Preferences prefs;

    public static int getMayorPuntuacion(){
        return getPrefs().getInteger("Mayor_Puntuación");
    }

    public static void setMayorPuntuacion(int puntuacion){
        getPrefs().putInteger("Mayor_Puntuación", puntuacion);
        getPrefs().flush();
    }

    private static Preferences getPrefs() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("Black WSP Invaders");
        }
        return prefs;
    }
}
