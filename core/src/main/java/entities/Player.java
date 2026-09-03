package entities;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import controls.VirtualController;

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
        this.texture = new Texture(Gdx.files.internal("libgdx.png"));
        this.bounds = new Rectangle(position.x, position.y, 32, 48);
    }

    public void update(float delta, VirtualController controller) {
        isGrounded = false; // Reset a cada frame; a colisão com plataforma restaura para true
        handleInput(controller);

        velocity.y += GRAVITY * delta;
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        bounds.setPosition(position.x, position.y);
    }

    public void handleInput(VirtualController controller) {
        velocity.x = 0;

        // Movimento Esquerda (Teclado ou Touch)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || controller.isLeftPressed()) {
            velocity.x = -MOVE_SPEED;
        }

        // Movimento Direita (Teclado ou Touch)
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || controller.isRightPressed()) {
            velocity.x = MOVE_SPEED;
        }

        // Pulo (Teclado ou Touch)
        boolean jumpRequested = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W) || controller.isJumpPressed();
        if (jumpRequested && isGrounded) {
            velocity.y = JUMP_VELOCITY;
            isGrounded = false;
        }
    }

    public void handlePlatformCollision(Rectangle platform) {
        // Colisão por cima (Piso)
        if (velocity.y < 0 && position.y + 10 >= platform.y + platform.height) {
            position.y = platform.y + platform.height;
            velocity.y = 0;
            isGrounded = true;
        }
        // Colisão por baixo (Teto)
        else if (velocity.y > 0 && position.y + bounds.height - 10 <= platform.y) {
            position.y = platform.y - bounds.height;
            velocity.y = 0;
        }
        bounds.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
