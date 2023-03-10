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
 * Created by wp90195 on 21/07/2017.
 */
public class PlayScreen extends BaseScreen {

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera; // cámara para crear la sensación de 2D

    private Stage escenario; // Stage (escenario) es el escenario del juego

    private Stage informaciones; // Stage para imprimir información en la pantalla

    private Image jugador; // Image es una implementación de Actor, que son los actores del juego.

    private Label lbPuntuacion;
    private Label lbBonus;
    private Label lbGameOver;
    private Label lbMayorPuntuacion;
    private Label lbPausado;
    private Texture jugadorTextura;
    private Texture jugadorTexturaIzquierda;
    private Texture jugadorTexturaDerecha;
    private Texture asteroideTextura1;
    private Texture asteroideTextura2;
    private Texture tiroTextura;
    private Texture bonusTextura;
    private Array<Texture> explosionesTextura = new Array<Texture>();
    private Array<Image> tiros = new Array<Image>();
    private Array<Image> asteroides = new Array<Image>();
    private Array<Explosion> explosiones = new Array<Explosion>();
    private Array<Image> bonus = new Array<Image>();
    private Music musicaFondo;
    private Music musicaBonus;
    private Sound sonidoExplosion;
    private Sound sonidoGameOver;
    private Sound sonidoTiro;
    private Music sonidoBonus;
    private float velocidadJugador = 200;
    private float velocidadTiro = 250;
    private float velocidadBonus = 50;
    private float velocidadAsteroide = 100;
    private float velocidadAsteroide2 = 150;
    private int maxAsteroides = 10;
    private int intervaloTiros = 350;
    private boolean yendoIzquierda = false;
    private boolean yendoDerecha = false;
    private boolean disparando = false;
    private boolean gameOver = false;
    private boolean pausado = false;
    private int puntuacion = 0;
    private final float tiempoPorBonus = 10;
    private float tiempoBonus = 0;

    public PlayScreen(MainGame game) {
        super(game);
    }

    /**
     * Llamada cuando la pantalla se elimina la primera vez

     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        escenario = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        informaciones = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));

        initTexturas();
        initPlayer();
        iniciarInformaciones();
        inicializarSonidos();
    }

    private void initTexturas() {
        tiroTextura = new Texture("sprites/shot.png");
        asteroideTextura1 = new Texture("sprites/enemie-1.png");
        asteroideTextura2 = new Texture("sprites/enemie-2.png");
        bonusTextura = new Texture("sprites/bonus.png");
        for (int i = 1; i <= 17; i++) {
            Texture explosion = new Texture("sprites/explosion-" + i + ".png");
            explosionesTextura.add(explosion);
        }
    }

    private void inicializarSonidos() {
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.play();
        musicaBonus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus-background.mp3"));
        musicaBonus.setLooping(true);
        sonidoExplosion = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        sonidoGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        sonidoTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        sonidoBonus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus.mp3"));
        sonidoBonus.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                musicaFondo.stop();
                musicaBonus.play();
            }
        });
    }

    private void iniciarInformaciones() {
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
        informaciones.addActor(lbPuntuacion);

        lbGameOver = new Label("GAME OVER :'(", style);
        lbGameOver.setVisible(false);
        informaciones.addActor(lbGameOver);

        lbPausado = new Label("STOP!", style);
        lbPausado.setVisible(true);
        informaciones.addActor(lbPausado);

        lbMayorPuntuacion = new Label("PUNTUACION MAXIMA: 0", style);
        lbMayorPuntuacion.setVisible(false);
        informaciones.addActor(lbMayorPuntuacion);

        lbBonus = new Label("El Bonus Termina en: 0", style);
        lbBonus.setVisible(false);
        informaciones.addActor(lbBonus);
    }

    private void initPlayer() {
        jugadorTextura = new Texture("sprites/player.png");
        jugadorTexturaIzquierda = new Texture("sprites/player-left.png");
        jugadorTexturaDerecha = new Texture("sprites/player-right.png");
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
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
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
        atualizarexplosiones(delta);
        actualizarInformaciones(delta);

        // Dibuja el escenario en la pantalla
        escenario.act(delta);
        escenario.draw();

        // dibuja las explosiones

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for (Explosion exp : explosiones) {
            int i = exp.getEstaciones() - 1;
            batch.draw(explosionesTextura.get(i), exp.getX(), exp.getY());
        }
        batch.end();

        // dibuja la información en la pantalla
        informaciones.act(delta);
        informaciones.draw();
    }

    private void atualizarSons() {
        if (tiempoBonus == 0) {
            if (musicaBonus.isPlaying()) {
                musicaBonus.stop();
            }
            if (!musicaFondo.isPlaying()) {
                musicaFondo.play();
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
            float y = bonus.getY() - velocidadBonus * delta;
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
            setPosicionAleatoria(bonus);
            escenario.addActor(bonus);
            this.bonus.add(bonus);
        }
    }

    private void setPosicionAleatoria(Actor actor) {
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
        lbBonus.setText("Bonus: " + (int) tiempoBonus + " segundos");
    }

    private void atualizarexplosiones(float delta) {
        for (Explosion exp : explosiones) {
            exp.atualizar(delta);
            // comprueba si la explosión ha pasado todas las etapas
            if (exp.getEstaciones() > 17) {
                explosiones.removeValue(exp, true);
            }
        }
    }

    private void actualizarJugador(float delta) {
        if (yendoDerecha) {
            // Impide que el jugador salga de la pantalla
            if (jugador.getX() + jugador.getWidth() < camera.viewportWidth) {
                jugador.setX(jugador.getX() + (velocidadJugador * delta));
            }
            jugador.setDrawable(new SpriteDrawable(new Sprite(jugadorTexturaDerecha)));
        } else if (yendoIzquierda) {
            // Impide que el jugador salga de la pantalla
            if (jugador.getX() > 0) {
                jugador.setX(jugador.getX() - (velocidadJugador * delta));
            }
            jugador.setDrawable(new SpriteDrawable(new Sprite(jugadorTexturaIzquierda)));
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
        Rectangle limitesAsteroide = new Rectangle();
        Rectangle limitesDisparo = new Rectangle();
        Rectangle limitesBonus = new Rectangle();
        Rectangle limitesJugador = new Rectangle(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());
        for (Image asteroid : asteroides) {
            limitesAsteroide.set(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (limitesAsteroide.overlaps(limitesJugador)) {
                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                if (tiempoBonus == 0) {
                    crearExplosiones(jugador.getX() + jugador.getWidth() / 2, jugador.getY() + jugador.getHeight() / 2);
                    jugador.setVisible(false);
                    sonidoGameOver.play();
                    musicaFondo.stop();
                    musicaBonus.stop();
                    gameOver();
                    return;
                } else {
                    crearExplosiones(asteroid.getX() + asteroid.getWidth() / 2, asteroid.getY() + asteroid.getHeight() / 2);
                    incrementaExplosion(asteroid);
                    sonidoExplosion.play();
                }
            }
            for (Image tiro : tiros) {
                limitesDisparo.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
                // verifica colision
                if (limitesAsteroide.overlaps(limitesDisparo)) {
                    asteroid.remove();
                    asteroides.removeValue(asteroid, true);
                    tiro.remove();
                    tiros.removeValue(tiro, true);
                    crearExplosiones(tiro.getX(), tiro.getY());
                    incrementaExplosion(asteroid);
                    sonidoExplosion.play();
                }
            }
        }
        for (Image bonus : this.bonus) {
            limitesBonus.set(bonus.getX(), bonus.getY(), bonus.getWidth(), bonus.getHeight());
            if (limitesBonus.overlaps(limitesJugador)) {
                bonus.remove();
                this.bonus.removeValue(bonus, true);
                tiempoBonus += tiempoPorBonus;
                sonidoBonus.stop();
                sonidoBonus.play();
                musicaBonus.pause();
                musicaFondo.stop();
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        if (puntuacion > Preferencias.getMayorPuntuacion()) {
            Preferencias.setMayorPuntuacion(puntuacion);
        }
    }

    private void incrementaExplosion(Image asteroid) {
        // comprueba el tipo del asteroide para incrementar la puntuación
        if ("1".equals(asteroid.getName())) {
            puntuacion += 5;
        } else {
            puntuacion += 10;
        }
    }

    private void crearExplosiones(float x, float y) {
        // La imagen de la explosión mide 96 por 96 píxeles
        Explosion explosion = new Explosion();
        explosion.setX(x - 96 / 2);
        explosion.setY(y - 96 / 2);
        explosiones.add(explosion);
    }

    private void atualizarAsteroides(float delta) {
        for (Image asteroid : asteroides) {
            float velocidades;
            // comprueba el tipo del asteroid para decidir la velocidad
            if ("1".equals(asteroid.getName())) {
                velocidades = velocidadAsteroide;
            } else {
                velocidades = velocidadAsteroide2;
            }
            // mueve el asteroid hacia el jugador
            float x = asteroid.getX();
            float y = asteroid.getY() - velocidades * delta;
            asteroid.setPosition(x, y);
            // quita el asteroid que ya salió de la pantalla
            if (asteroid.getY() + asteroid.getHeight() < 0) {
                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                decrementaPuntuacion(asteroid);
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
            setPosicionAleatoria(asteroid);
            asteroides.add(asteroid);
            escenario.addActor(asteroid);
        }
    }


    private void decrementaPuntuacion(Image asteroid) {
// comprueba el tipo del asteroide para decrementar la puntuación
        if ("1".equals(asteroid.getName())) {
            puntuacion -= 15;
        } else {
            puntuacion -= 30;
        }
    }


    private void atualizarTiros(float delta) {
        for (Image tiro : tiros) {
            // mueve el tiro hacia la parte superior de la pantalla
            float x = tiro.getX();
            float y = tiro.getY() + velocidadTiro * delta;
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
        yendoDerecha = false;
        yendoIzquierda = false;
        disparando = false;
        // comprueba si la flecha hacia la izquierda está presionada
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || toqueIzquierda()) {
            yendoIzquierda = true;
        }
        // comprueba si la flecha hacia la derecha está presionada
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || toqueDerecha()) {
            yendoDerecha = true;
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
        velocidadAsteroide = camera.viewportHeight / 6;
        velocidadAsteroide2 = camera.viewportHeight / 4;
        velocidadJugador = camera.viewportWidth / 1.8f;
        velocidadTiro = camera.viewportHeight / 2.5f;
        velocidadBonus = camera.viewportHeight / 8;
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
        jugadorTexturaDerecha.dispose();
        jugadorTexturaIzquierda.dispose();
        tiroTextura.dispose();
        asteroideTextura1.dispose();
        asteroideTextura2.dispose();
        bonusTextura.dispose();
        for (Texture t : explosionesTextura) {
            t.dispose();
        }
        informaciones.dispose();
        musicaFondo.dispose();
        musicaBonus.dispose();
        sonidoExplosion.dispose();
        sonidoGameOver.dispose();
        sonidoTiro.dispose();
        sonidoBonus.dispose();
    }
}
