package com.bwspinv.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;

import com.bwspinv.game.MainGame;
import com.bwspinv.game.util.Format;
import com.bwspinv.game.util.Preferencias;

/**
 * Created by wp90195 on 28/07/2017.
 */
public class MenuScreen extends BaseScreen {

    private OrthographicCamera camera;
    private Stage escenario;
    private BitmapFont fontBoton;
    private BitmapFont fontTitulo;
    private Texture botonTextura;
    private Texture botonPresionadoTextura;
    private ImageTextButton btnIniciar;
    private Label lbTitulo;
    private Label lbPuntuacion;



    public MenuScreen(MainGame game){
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        escenario = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        Gdx.input.setInputProcessor(escenario);

        initFonts();
        initTexturas();
        initLabels();
        initBotones();
    }

    private void initLabels() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = fontTitulo;
        lbTitulo = new Label("Black WSP Invaders", labelStyle);
        escenario.addActor(lbTitulo);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = fontBoton;
        lbPuntuacion = new Label("Puntuación: " + Format.format(Preferencias.getMayorPuntuacion()), labelStyle);
        escenario.addActor(lbPuntuacion);
    }

    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(32 * Gdx.graphics.getDensity());
        params.color = Color.GREEN;
        fontBoton = generator.generateFont(params);

        params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(48 * Gdx.graphics.getDensity());
        params.color = new Color(.25f, .25f, .85f, 1);
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        params.shadowColor = Color.BLACK;
        fontTitulo = generator.generateFont(params);

        generator.dispose();
    }

    private void initTexturas() {
        botonTextura = new Texture(Gdx.files.internal("buttons/button.png"));
        botonPresionadoTextura = new Texture(Gdx.files.internal("buttons/button-down.png"));
    }

    private void initBotones() {
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.font = fontBoton;
        style.up = new SpriteDrawable(new Sprite(botonTextura));
        style.down = new SpriteDrawable(new Sprite(botonPresionadoTextura));

        btnIniciar = new ImageTextButton("  Easy  ", style);
        btnIniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });
        escenario.addActor(btnIniciar);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizarBotoes();

        escenario.act(delta);
        escenario.draw();
    }

    private void atualizarBotoes() {
        lbTitulo.setPosition(camera.viewportWidth / 2 - lbTitulo.getPrefWidth() / 2, camera.viewportHeight - lbTitulo.getPrefHeight() - 50);

        btnIniciar.setPosition(camera.viewportWidth / 2 - btnIniciar.getPrefWidth() / 2, camera.viewportHeight / 2 - btnIniciar.getPrefHeight() / 2);

        lbPuntuacion.setPosition(camera.viewportWidth / 2 - lbPuntuacion.getPrefWidth() / 2, 100);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        escenario.getViewport().setScreenSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        escenario.dispose();
        fontBoton.dispose();
        fontTitulo.dispose();
        botonTextura.dispose();
        botonPresionadoTextura.dispose();
    }
}
