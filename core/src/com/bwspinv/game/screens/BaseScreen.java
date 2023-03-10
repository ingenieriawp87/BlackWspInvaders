package com.bwspinv.game.screens;

import com.badlogic.gdx.Screen;
import com.bwspinv.game.MainGame;

/**
 * Created by wp90195 on 21/07/2017.
 */
public abstract class BaseScreen implements Screen {

    protected final MainGame game;

    public BaseScreen(MainGame game){
        this.game = game;
    }



    @Override
    public void hide() {
        dispose();
    }

}
