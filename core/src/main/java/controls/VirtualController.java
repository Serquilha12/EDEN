package controls;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class VirtualController {
    private final Stage stage;
    private final Viewport viewport;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean jumpPressed;

    public VirtualController() {
        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);

        // O InputProcessor é registado externamente (no GameScreen.show())
        // para evitar conflito com o InputProcessor do MenuScreen

        Table table = new Table();
        table.left().bottom(); // Posiciona os controles na parte inferior
        table.setFillParent(true);

        // Texturas temporárias para os botões (geradas por código)
        TextureRegionDrawable btnLeftTex = createButtonDrawable(Color.RED);
        TextureRegionDrawable btnRightTex = createButtonDrawable(Color.GREEN);
        TextureRegionDrawable btnJumpTex = createButtonDrawable(Color.BLUE);

        ImageButton btnLeft = new ImageButton(btnLeftTex);
        ImageButton btnRight = new ImageButton(btnRightTex);
        ImageButton btnJump = new ImageButton(btnJumpTex);

        // Listeners para detectar quando o botão está sendo segurado ou solto
        btnLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        btnRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        btnJump.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                jumpPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                jumpPressed = false;
            }
        });

        // Montagem do Layout dos Botões na Tela
        table.add(btnLeft).size(64, 64).pad(10);
        table.add(btnRight).size(64, 64).pad(10);
        table.add().expandX(); // Espaçamento entre os controles direcionais e o botão de pulo
        table.add(btnJump).size(64, 64).pad(10);

        stage.addActor(table);
    }

    // Cria blocos de cores sólidas simples para os botões virtuais
    private TextureRegionDrawable createButtonDrawable(Color color) {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(32, 32, 30);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public void draw() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    // Getters para checar o estado dos botões
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }

    /** Expõe o Stage para que o GameScreen possa registar o InputProcessor no momento correto. */
    public Stage getStage() { return stage; }

    public void dispose() {
        stage.dispose();
    }
}
