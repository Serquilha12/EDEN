package mz.ac.ucm.eden.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import mz.ac.ucm.eden.controls.VirtualController;
import mz.ac.ucm.eden.entities.Player;
import mz.ac.ucm.eden.entities.SuperPlataform;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Array<SuperPlataform> platforms;
    private VirtualController controller;
    private Game game;
    private Texture background;

    private static final float PARALLAX_FACTOR = 0.35f;

    public GameScreen() {
    }

    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        background = new Texture(Gdx.files.internal("Morioh.png"));

        player = new Player(new Vector2(100, Player.GROUND_Y));
        platforms = new Array<>();
        controller = new VirtualController();
        Gdx.input.setInputProcessor(controller.getStage()); // Regista o input aqui, não no construtor do VirtualController

        // Montagem do nível (Chão alinhado ao piso da imagem Morioh + plataformas suspensas no ar)
        platforms.add(new SuperPlataform(0, 0, 100000, Player.GROUND_Y, false)); // Chão contínuo invisível no nível do piso
        platforms.add(new SuperPlataform(300, 150, 200, 20));    // Plataforma suspensa 1
        platforms.add(new SuperPlataform(600, 230, 180, 20));    // Plataforma suspensa 2
        platforms.add(new SuperPlataform(850, 180, 250, 20));    // Plataforma suspensa 3
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

        // Fundo (Morioh) — efeito parallax com repetição horizontal conforme o personagem anda
        float bgHeight = camera.viewportHeight;
        float bgWidth = bgHeight * (background.getWidth() / (float) background.getHeight());
        float camLeft = camera.position.x - (camera.viewportWidth / 2f);
        float camBottom = camera.position.y - (camera.viewportHeight / 2f);

        // Deslocamento parallax baseado na posição do jogador (inicia em x=100)
        float bgOffset = ((player.getPosition().x - 100f) * PARALLAX_FACTOR) % bgWidth;
        if (bgOffset < 0) {
            bgOffset += bgWidth;
        }

        // Desenha repetições contínuas para cobrir toda a extensão visível da viewport
        float startX = camLeft - bgOffset;
        for (float x = startX; x < camLeft + camera.viewportWidth; x += bgWidth) {
            batch.draw(background, x, camBottom, bgWidth, bgHeight);
        }

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
