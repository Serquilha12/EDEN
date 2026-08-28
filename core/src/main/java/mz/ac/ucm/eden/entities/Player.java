package mz.ac.ucm.eden.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private Texture texture;

    private static final float GRAVITY = -1500f;
    private static final float JUMP_VELOCITY = 600f;
    private static final float MOVE_SPEED = 250f;
    private boolean isGrounded = false;

    public Player(Vector2 startPosition) {
        this.position = startPosition;
        this.velocity = new Vector2(0, 0);

        // Textura temporária (pode substituir pelo sprite do Rony depois)
        this.texture = new Texture(Gdx.files.internal("libgdx.png"));
        this.bounds = new Rectangle(position.x, position.y, 32, 48);
    }

    public void update(float delta) {
        // Entrada de teclado para testes no Desktop
        handleInput();

        // Aplicar Gravidade
        velocity.y += GRAVITY * delta;

        // Atualizar Posição
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Atualizar Hitbox de Colisão
        bounds.setPosition(position.x, position.y);
    }

    private void handleInput() {
        velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = MOVE_SPEED;
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W)) && isGrounded) {
            velocity.y = JUMP_VELOCITY;
            isGrounded = false;
        }
    }

    public void onGroundCollision(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        isGrounded = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
