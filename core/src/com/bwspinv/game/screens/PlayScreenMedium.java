package com.bwspinv.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;



import com.bwspinv.game.MainGame;
import com.bwspinv.game.model.Explosion;
import com.bwspinv.game.util.Format;
import com.bwspinv.game.util.Preferencias;
/**
 * Created by wp90195 on 25/07/2017.
 */
public class PlayScreenMedium extends BaseScreen {

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera; // Cámara para crear la sensación de 2D

    private Stage escenario; // Stage (escenario) es el escenario del juego

    private Stage informacion; // Stage para imprimir información en la pantalla

    private Image jugador; // Es una implementación de Actor, que son los actores del juego.

    private Label lbPuntuacion;
    private Label lbBonus;
    private Label lbGameOver;
    private Label lbMayorPuntuacion;
    private Label lbPausado;
    private Texture jugadorTextura;
    private Texture jugadorMuereTextura;
    //private Array<Texture> jugadorMuereTextura= new Array<Texture>();
    private Image jugadorDie;

    private Texture jugadorIzqTex;
    private Texture JugTexDer;
    private Texture asteroideTextura1;
    private Texture asteroideTextura2;
    private Texture tiroTextura;
    private Texture bonusTextura;
    private Array<Texture> explosionTextura = new Array<Texture>();
    private Array<Image> tiros = new Array<Image>();
    private Array<Image> asteroides = new Array<Image>();
    private Array<Explosion> explosiones = new Array<Explosion>();
    private Array<Image> bonus = new Array<Image>();
    private Music musicFondo;
    private Music musicaBonus;
    private Sound sonidoExplosion;
    private Sound sonidoGameOver;
    private Sound sonidoTiro;
    private Music sonidoBonus;
    private float velocidadJugador = 250;
    private float velocidadeTiro = 250;
    private float velocidadeBonus = 15;
    private float velocidadeAsteroide1 = 100;
    private float velocidadeAsteroide2 = 120;
    private int maxAsteroides = 12;
    private int intervaloTiros = 380;
    private boolean paraLaIzquierda = false;
    private boolean paraLaDerecha = false;
    private boolean disparando = false;
    private boolean gameOver = false;
    private boolean pausado = false;
    private int puntuacion = 0;
    private final float tiempoPorElBonus = 15;
    private float tiempoBonus = 0;

    public PlayScreenMedium(MainGame game) {
        super(game);
    }

    /**
     * Llamando al screen cuando es la primera vez
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        escenario = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        informacion = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));

        initTexturas();
        initPlayer();
        iniInforma();
        initSons();
        // changePlayer();
        System.out.println("NIVEL MEDIO");

    }




    private void initTexturas() {

        tiroTextura = new Texture("sprites/shot.png");
        asteroideTextura1 = new Texture("sprites/enemie-1.png");
        asteroideTextura2 = new Texture("sprites/enemie-2.png");
        bonusTextura = new Texture("sprites/bonus.png");
        for (int i = 1; i <= 17; i++) {
            Texture explota = new Texture("sprites/explosion-" + i + ".png");
            explosionTextura.add(explota);
        }
    }

    private void initSons() {
        musicFondo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicFondo.setLooping(true);
        musicFondo.play();
        musicaBonus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus-background.mp3"));
        musicaBonus.setLooping(true);
        sonidoExplosion = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        sonidoGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        sonidoTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        sonidoBonus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus.mp3"));
        sonidoBonus.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                musicFondo.stop();
                musicaBonus.play();
            }
        });
    }

    private void iniInforma() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/contrast.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(32 * Gdx.graphics.getDensity());
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        font = generator.generateFont(params);
        generator.dispose();



        // Crea un estilo para Label

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        lbPuntuacion = new Label("0 puntos", style);
        informacion.addActor(lbPuntuacion);

        lbGameOver = new Label("Fin del juego", style);
        lbGameOver.setVisible(false);
        informacion.addActor(lbGameOver);

        lbPausado = new Label("Pausa", style);
        lbPausado.setVisible(true);
        informacion.addActor(lbPausado);

        lbMayorPuntuacion = new Label("Puntuación Maxima: 0", style);
        lbMayorPuntuacion.setVisible(false);
        informacion.addActor(lbMayorPuntuacion);

        lbBonus = new Label("Tiempo Bonus: 0", style);
        lbBonus.setVisible(false);
        informacion.addActor(lbBonus);
    }

    private void changePlayer() {
/*
        for (int i = 1; i <= 17; i++) {
            Texture jugadorDie = new Texture("sprites/invader" + i + ".png");
            jugadorMuereTextura.add(jugadorDie);
            escenario.addActor(jugadorMuereTextura);

        }

*/


        jugadorMuereTextura = new Texture("sprites/invader1.png");
        jugadorDie = new Image(jugadorMuereTextura);
        System.out.println("Invader ");
//        jugadorDie.setPosition((camera.viewportWidth / 2) - jugador.getWidth() / 2, 10);
        escenario.addActor(jugadorDie);
        //jugadorDie.setPosition(jugador.getX(),jugador.getY());
        jugadorDie.setPosition(camera.viewportWidth / 2 - jugadorDie.getPrefWidth() / 2, camera.viewportHeight / 4 - jugadorDie.getPrefHeight() / 112);
        //jugadorDie.setPosition(0,0);





    }

    private void initPlayer() {
        jugadorTextura = new Texture("sprites/player.png");
        jugadorIzqTex = new Texture("sprites/player-left.png");
        JugTexDer = new Texture("sprites/player-right.png");
        jugador = new Image(jugadorTextura);
        jugador.setPosition((camera.viewportWidth / 2) - jugador.getWidth() / 2, 10);
        escenario.addActor(jugador);
    }

    /**
     * Llamado a todo el cuadro (marco)
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
//Fondo del juego
        //Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClearColor(.103f, .5f, .5f, .0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturarTeclas(delta);
        if (!gameOver) {
            if (!pausado) {
                atualizarSons();
                capturarTeclasJuego(delta);
                actualizarJugador(delta);
                atualizarAsteroides(delta);
                atualizarTiros(delta);
                atualizarBonus(delta);
                detectarColisiones(delta);
            }
        }
        actualizarExplosiones(delta);
        actualizarInformaciones(delta);

        // Dibuja el escenario en la pantalla

        escenario.act(delta);
        escenario.draw();

        // dibuja las explosiones

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for (Explosion exp : explosiones) {
            int i = exp.getTrabajo() - 1;
            batch.draw(explosionTextura.get(i), exp.getX(), exp.getY());
        }
        batch.end();

        // dibuja la información en la pantalla

        informacion.act(delta);
        informacion.draw();
    }

    private void atualizarSons() {
        if (tiempoBonus == 0) {
            if (musicaBonus.isPlaying()) {
                musicaBonus.stop();
            }
            if (!musicFondo.isPlaying()) {
                musicFondo.play();
            }
        }
    }

    private int bonusMeta = 500;
    private int bonusLevel = 0;

    private void atualizarBonus(final float delta) {
        // atualiza o tempo de bonus
        tiempoBonus -= delta;
        if (tiempoBonus < 0)
            tiempoBonus = 0;

        for (Image bonus : this.bonus) {
            // mueve el tiro hacia la parte superior de la pantalla

            float x = bonus.getX();
            float y = bonus.getY() - velocidadeBonus * delta;
            bonus.setPosition(x, y);
            // Comprueba si el disparo ya ha salido de la pantalla

            if (bonus.getY() + bonus.getHeight() < 0) {
                bonus.remove();
                this.bonus.removeValue(bonus, true);
            }
        }
// calcula el nivel actual de la puntuación del usuario
        int bonusLevel = (int) ((float) puntuacion / (float) bonusMeta);
// comprueba si el nivel actual es mayor que el último nivel alcanzado
        if (bonusLevel > this.bonusLevel) {
            this.bonusLevel = bonusLevel;
            Image bonus = new Image(bonusTextura);
            posicionAleatoria(bonus);
            escenario.addActor(bonus);
            this.bonus.add(bonus);
        }
    }

    private void posicionAleatoria(Actor actor) {
        float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
        float x = MathUtils.random(0, camera.viewportWidth - actor.getWidth());
        actor.setPosition(x, y);
    }

    private void capturarTeclas(float delta) {
        // comprueba si se ha presionado el botón PAUSE

        if (!gameOver && Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            pausado = !pausado;
        }
// comprueba si se ha presionado el botón para reiniciar el juego
        if (gameOver && (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched())) {
            reiniciarJuego();
        }
    }

    private void reiniciarJuego() {
        game.setScreen(new MenuScreen(game));
    }

    /**
     * Cumple información escrita en la pantalla
     *
     * @param delta
     */
    private void actualizarInformaciones(float delta) {
        lbPuntuacion.setText(Format.format(puntuacion) + " puntos");
        lbPuntuacion.setPosition(10, camera.viewportHeight - lbPuntuacion.getPrefHeight() - 10);

        lbGameOver.setVisible(gameOver);
        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2 - lbGameOver.getHeight() / 2);

        lbPausado.setVisible(pausado);
        lbPausado.setPosition(camera.viewportWidth / 2 - lbPausado.getWidth() / 2, camera.viewportHeight / 2 - lbPausado.getHeight() / 2);

        lbMayorPuntuacion.setText("Puntuación máxima: " + Format.format(Preferencias.getMayorPuntuacion()));
        lbMayorPuntuacion.setVisible(gameOver);
        lbMayorPuntuacion.setPosition(camera.viewportWidth / 2 - lbMayorPuntuacion.getPrefWidth() / 2, lbGameOver.getY() - 100);

        lbBonus.setPosition(camera.viewportWidth - lbBonus.getPrefWidth() - 10, lbPuntuacion.getY());
        lbBonus.setVisible(tiempoBonus > 0);
        lbBonus.setText("Bonus: " + (int) tiempoBonus + " s");
    }

    private void actualizarExplosiones(float delta) {
        for (Explosion exp : explosiones) {
            exp.atualizar(delta);
// comprueba si la explosión ha pasado todas las etapas

            if (exp.getTrabajo() > 17) {
                explosiones.removeValue(exp, true);
            }
        }
    }

    private void actualizarJugador(float delta) {
        if (paraLaDerecha) {
            // Impide que el jugador salga de la pantalla

            if (jugador.getX() + jugador.getWidth() < camera.viewportWidth) {
                jugador.setX(jugador.getX() + (velocidadJugador * delta));
            }
            jugador.setDrawable(new SpriteDrawable(new Sprite(JugTexDer)));
        } else if (paraLaIzquierda) {
            // Impide que el jugador salga de la pantalla
            if (jugador.getX() > 0) {
                jugador.setX(jugador.getX() - (velocidadJugador * delta));
            }
            jugador.setDrawable(new SpriteDrawable(new Sprite(jugadorIzqTex)));
        } else {
            jugador.setDrawable(new SpriteDrawable(new Sprite(jugadorTextura)));
        }
        // actializa el bonus del jugadopr
        if (tiempoBonus == 0 && jugador.hasActions()) {
            jugador.clearActions();
            jugador.addAction(Actions.alpha(1));
        }
        if (tiempoBonus > 0 && !jugador.hasActions()) {
            jugador.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(0.2f), Actions.fadeIn(0.2f))));
        }
    }

    private void detectarColisiones(float delta) {
        Rectangle boundsAsteroid = new Rectangle();
        Rectangle boundsShoot = new Rectangle();
        Rectangle boundsBonus = new Rectangle();
        Rectangle boundsJugador = new Rectangle(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());
        for (Image asteroid : asteroides) {
            boundsAsteroid.set(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (boundsAsteroid.overlaps(boundsJugador)) {
                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                if (tiempoBonus == 0) {
                    crearExplosion(jugador.getX() + jugador.getWidth() / 2, jugador.getY() + jugador.getHeight() / 2);
                    changePlayer();

                    jugador.setVisible(false);
                    sonidoGameOver.play();
                    musicFondo.stop();
                    musicaBonus.stop();

                    gameOver();
                    return;
                } else {
                    crearExplosion(asteroid.getX() + asteroid.getWidth() / 2, asteroid.getY() + asteroid.getHeight() / 2);
                    incrementaPuntuacion(asteroid);
                    sonidoExplosion.play();
                }
            }
            for (Image tiro : tiros) {
                boundsShoot.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
//verifica colision
                if (boundsAsteroid.overlaps(boundsShoot)) {
                    asteroid.remove();
                    asteroides.removeValue(asteroid, true);
                    tiro.remove();
                    tiros.removeValue(tiro, true);
                    crearExplosion(tiro.getX(), tiro.getY());
                    incrementaPuntuacion(asteroid);
                    sonidoExplosion.play();

                }
            }
        }
        for (Image bonus : this.bonus) {
            boundsBonus.set(bonus.getX(), bonus.getY(), bonus.getWidth(), bonus.getHeight());
            if (boundsBonus.overlaps(boundsJugador)) {
                bonus.remove();
                this.bonus.removeValue(bonus, true);
                tiempoBonus += tiempoPorElBonus;
                sonidoBonus.stop();
                sonidoBonus.play();
                musicaBonus.pause();
                musicFondo.stop();
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        if (puntuacion > Preferencias.getMayorPuntuacion()) {
            Preferencias.setMayorPuntuacion(puntuacion);
        }
    }

    private void incrementaPuntuacion(Image asteroid) {
        // comprueba el tipo del asteroide para incrementar la puntuación

        if ("1".equals(asteroid.getName())) {
            puntuacion += 5;

        } else {
            puntuacion += 10;

        }
    }

    private void crearExplosion(float x, float y) {
        // La imagen de la explosión mide 96 por 96 píxeles
        Explosion explosion = new Explosion();
        explosion.setX(x - 96 / 2);
        explosion.setY(y - 96 / 2);
        explosiones.add(explosion);
    }

    private void atualizarAsteroides(float delta) {
        for (Image asteroid : asteroides) {
            float velocidade;
// comprueba el tipo del asteroid para decidir la velocidad
            if ("1".equals(asteroid.getName())) {
                velocidade = velocidadeAsteroide1;
            } else {
                velocidade = velocidadeAsteroide2;
            }
// mueve el asteroid hacia el jugador
            float x = asteroid.getX();
            float y = asteroid.getY() - velocidade * delta;
            asteroid.setPosition(x, y);
// quita el asteroid que ya salió de la pantalla
            if (asteroid.getY() + asteroid.getHeight() < 0) {

                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                decrementaLaPuntuacion(asteroid);


            }
        }
// crea nuevos asteroides si es necesario
        if (asteroides.size < maxAsteroides) {
            Image asteroid;
// decide aleatoriamente entre crear asteroides tipo 1 o 2
            int tipo = MathUtils.random(1, 3);
            if (tipo == 1) {
                asteroid = new Image(asteroideTextura1);
                asteroid.setName("1");
            } else {
                asteroid = new Image(asteroideTextura2);
                asteroid.setName("2");
            }
// configura posiciones aleatorias para los asteroides
            posicionAleatoria(asteroid);
            asteroides.add(asteroid);
            escenario.addActor(asteroid);
        }
    }


    private void decrementaLaPuntuacion(Image asteroid) {
// comprueba el tipo del asteroide para decrementar la puntuación
        if ("1".equals(asteroid.getName())) {
            puntuacion -= 15;
            sonidoGameOver.stop();
            sonidoGameOver.play();

            System.out.println("Pierde 1");
        } else {
            puntuacion -= 30;
            sonidoGameOver.stop();
            sonidoGameOver.play();

            System.out.println("Pierde 2");
        }
    }

    private void atualizarTiros(float delta) {
        for (Image tiro : tiros) {
// mueve el tiro hacia la parte superior de la pantalla
            float x = tiro.getX();
            float y = tiro.getY() + velocidadeTiro * delta;
            tiro.setPosition(x, y);
// comprueba si el disparo ya ha salido de la pantalla
            if (tiro.getY() > camera.viewportHeight) {
                tiro.remove();
                tiros.removeValue(tiro, true);
            }
        }
// crea nuevos tiros si es necesario
        if (disparando) {
// comprueba si el último disparo fue disparado a 400 milisegundos atrás
            if (System.currentTimeMillis() - ultimoTiro >= intervaloTiros) {
                Image tiro = new Image(tiroTextura);
                float x = jugador.getX() + jugador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jugador.getY() + jugador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                escenario.addActor(tiro);
                ultimoTiro = System.currentTimeMillis();
                sonidoTiro.play();
            }
        }
    }

    private void capturarTeclasJuego(float delta) {
        paraLaDerecha = false;
        paraLaIzquierda = false;
        disparando = false;
// comprueba si la flecha hacia la izquierda está presionada
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || toqueIzquierda()) {
            paraLaIzquierda = true;
        }
// comprueba si la flecha hacia la derecha está presionada
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || toqueDerecha()) {
            paraLaDerecha = true;
        }
// comprueba si el espacio está presionado
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.app.getType() == Application.ApplicationType.Android) {
            disparando = true;
        }
    }

    private boolean toqueDerecha() {
        return Gdx.input.isTouched() && Gdx.input.getX() > camera.viewportWidth / 2;
    }

    private boolean toqueIzquierda() {
        return Gdx.input.isTouched() && Gdx.input.getX() < camera.viewportWidth / 2;
    }

    private long ultimoTiro = 0;

    /**
     * Llamada siempre que la pantalla cambia de tamaño (rotación de la pantalla)
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();

        atualizaVelocidades();
    }

    /**
     * Calcula las velocidades de acuerdo con el tamaño de la pantalla
     */
    private void atualizaVelocidades() {
        velocidadeAsteroide1 = camera.viewportHeight / 6;
        velocidadeAsteroide2 = camera.viewportHeight / 4;
        velocidadJugador = camera.viewportWidth / 1.8f;
        velocidadeTiro = camera.viewportHeight / 2.5f;
        velocidadeBonus = camera.viewportHeight / 8;
    }

    /**
     * Llamada cuando la pantalla se minimiza, o se lleva al fondo
     */
    @Override
    public void pause() {

    }

    /**
     * Llamada cuando la pantalla se restaura o vuelve al primer plano
     */
    @Override
    public void resume() {

    }

    /**
     * Llamada cuando la pantalla está cerrada (destuida)
     */
    @Override
    public void dispose() {
        escenario.dispose();
        jugadorTextura.dispose();
        JugTexDer.dispose();
        jugadorIzqTex.dispose();
        tiroTextura.dispose();
        asteroideTextura1.dispose();
        asteroideTextura2.dispose();
        bonusTextura.dispose();
        for (Texture t : explosionTextura) {
            t.dispose();
        }
        informacion.dispose();
        musicFondo.dispose();
        musicaBonus.dispose();
        sonidoExplosion.dispose();
        sonidoGameOver.dispose();
        sonidoTiro.dispose();
        sonidoBonus.dispose();
    }
}
