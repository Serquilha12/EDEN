package screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import controls.VirtualController;
import entities.Player;
import entities.SuperPlataform;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Array<SuperPlataform> platforms;
    private VirtualController controller;
    private Game game;
    private Texture background;

    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        background = new Texture(Gdx.files.internal("Morioh.png"));

        player = new Player(new Vector2(100, 200));
        platforms = new Array<>();
        controller = new VirtualController();
        Gdx.input.setInputProcessor(controller.getStage()); // Regista o input aqui, não no construtor do VirtualController

        // Montagem do nível de teste (Chão contínuo + plataformas suspensas)
        platforms.add(new SuperPlataform(0, 50, 1200, 30));      // Chão principal
        platforms.add(new SuperPlataform(300, 150, 200, 20));    // Plataforma 1
        platforms.add(new SuperPlataform(600, 230, 180, 20));    // Plataforma 2
        platforms.add(new SuperPlataform(850, 180, 250, 20));    // Plataforma 3
    }

    @Override
    public void render(float delta) {
        // Lógica de Atualização
        player.update(delta, controller);
        checkCollisions();

        // Câmera segue o jogador no eixo X (com limite mínimo na origem)
        camera.position.x = Math.max(player.getPosition().x + 100, 400);
        camera.update();

        // Limpeza de Tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderização
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fundo — desenhado primeiro e posicionado relativo à câmera para acompanhar a tela
        float bgX = camera.position.x - (camera.viewportWidth / 2f);
        float bgY = camera.position.y - (camera.viewportHeight / 2f);
        batch.draw(background, bgX, bgY, camera.viewportWidth, camera.viewportHeight);

        for (SuperPlataform platform : platforms) {
            platform.render(batch);
        }
        player.render(batch);
        batch.end();

// Desenha os botões virtuais por cima da cena
        controller.draw();
    }

    private void checkCollisions() {
        for (SuperPlataform platform : platforms) {
            if (player.getBounds().overlaps(platform.getBounds())) {
                player.handlePlatformCollision(platform.getBounds());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        controller.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        player.dispose();
        controller.dispose();
        for (SuperPlataform platform : platforms) {
            platform.dispose();
        }
    }
}
