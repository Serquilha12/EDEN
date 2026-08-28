package mz.ac.ucm.eden.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mz.ac.ucm.eden.entities.Player;
import mz.ac.ucm.eden.entities.SuperPlataform;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private SuperPlataform platform;

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Resolução virtual de teste

        // Instancia o jogador e uma plataforma de teste
        player = new Player(new Vector2(100, 200));
        platform = new SuperPlataform(50, 100, 300, 20);
    }

    @Override
    public void render(float delta) {
        // Atualiza a lógica do jogo
        player.update(delta);
        checkCollisions();

        // Limpa a tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderiza os objetos
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        platform.render(batch);
        player.render(batch);
        batch.end();
    }

    private void checkCollisions() {
        // Colisão básica entre o jogador e a plataforma
        if (player.getBounds().overlaps(platform.getBounds())) {
            player.onGroundCollision(platform.getBounds().y + platform.getBounds().height);
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        platform.dispose();
    }
}
