package mz.ac.ucm.eden.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {
    private Stage stage;
    private Game game;
    private Texture btnUpTexture;
    private Texture btnDownTexture;
    private BitmapFont font;

    public MenuScreen(Game game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(800, 480));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        // Estilo básico para o título
        font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("EDEN", labelStyle);
        titleLabel.setFontScale(3f);

        // Estilo básico para o botão JOGAR
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = createButtonBackground(Color.DARK_GRAY);
        buttonStyle.down = createButtonBackground(Color.GRAY);

        TextButton btnPlay = new TextButton("JOGAR", buttonStyle);

        // Ação de clique: Transita para o NÍVEL 1 (GameScreen)
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        // Monta o layout do menu
        table.add(titleLabel).padBottom(40).row();
        table.add(btnPlay).size(200, 60);

        stage.addActor(table);
    }

    private TextureRegionDrawable createButtonBackground(Color color) {
        Pixmap pixmap = new Pixmap(200, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        if (btnUpTexture == null) {
            btnUpTexture = texture;
        } else {
            btnDownTexture = texture;
        }
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (font != null) font.dispose();
        if (btnUpTexture != null) btnUpTexture.dispose();
        if (btnDownTexture != null) btnDownTexture.dispose();
    }
}
