package mz.ac.ucm.eden.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import mz.ac.ucm.eden.controls.VirtualController;

public class Player {
    public enum State {
        IDLE, RUNNING, JUMPING, FALLING
    }

    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;

    private static final float GRAVITY = -1500f;
    private static final float JUMP_VELOCITY = 600f;
    private static final float MOVE_SPEED = 250f;
    private boolean isGrounded = false;

    // Animações e Texturas
    private State currentState = State.IDLE;
    private float stateTime = 0f;
    private boolean facingRight = true;

    private Texture idleTexture;
    private Texture jumpUpTexture;
    private Texture jumpFallTexture;
    private Texture[] runTextures;
    private Animation<TextureRegion> runAnimation;
    private TextureRegion idleRegion;
    private TextureRegion jumpUpRegion;
    private TextureRegion jumpFallRegion;

    // Dimensões visuais — ajustadas para os frame_raw que têm proporção ~1:1.8 (largura:altura)
    // DRAW_WIDTH / DRAW_HEIGHT definem o tamanho em unidades de jogo em que a sprite é renderizada
    private static final float DRAW_WIDTH  = 72f;
    private static final float DRAW_HEIGHT = 110f; // Maior para não cortar a cabeça
    // Offset X centraliza a sprite sobre a hitbox; Offset Y eleva a sprite para a cabeça ficar visível
    private static final float DRAW_OFFSET_X = -18f;
    private static final float DRAW_OFFSET_Y = -8f; // ligeiro ajuste para alinhar pés com a plataforma

    public Player(Vector2 startPosition) {
        this.position = startPosition;
        this.velocity = new Vector2(0, 0);
        this.bounds = new Rectangle(position.x, position.y, 36, 58);

        loadAssets();
    }

    private void loadAssets() {
        // Usamos os frame_raw que contêm o personagem completo (com cabeça)
        // frame_raw_0 = idle (97x176), frame_raw_9 = jump_up (113x190), frame_raw_10 = jump_fall (83x156)
        idleTexture     = new Texture(Gdx.files.internal("player/frame_raw_0.png"));
        jumpUpTexture   = new Texture(Gdx.files.internal("player/frame_raw_9.png"));
        jumpFallTexture = new Texture(Gdx.files.internal("player/frame_raw_10.png"));

        idleRegion     = new TextureRegion(idleTexture);
        jumpUpRegion   = new TextureRegion(jumpUpTexture);
        jumpFallRegion = new TextureRegion(jumpFallTexture);

        // Carrega frames de corrida: frame_raw_1 a frame_raw_5 (animação de corrida com cabeça)
        runTextures = new Texture[5];
        Array<TextureRegion> runFrames = new Array<>();
        for (int i = 0; i < 5; i++) {
            runTextures[i] = new Texture(Gdx.files.internal("player/frame_raw_" + (i + 1) + ".png"));
            runFrames.add(new TextureRegion(runTextures[i]));
        }
        runAnimation = new Animation<>(0.10f, runFrames, Animation.PlayMode.LOOP);
    }

    public void update(float delta, VirtualController controller) {
        stateTime += delta;

        handleInput(controller);

        velocity.y += GRAVITY * delta;
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        bounds.setPosition(position.x, position.y);

        // Atualiza estado do jogador
        if (!isGrounded) {
            if (velocity.y > 50) {
                currentState = State.JUMPING;
            } else {
                currentState = State.FALLING;
            }
        } else if (Math.abs(velocity.x) > 10) {
            currentState = State.RUNNING;
        } else {
            currentState = State.IDLE;
        }
    }

    public void handleInput(VirtualController controller) {
        velocity.x = 0;

        // Movimento Esquerda
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || (controller != null && controller.isLeftPressed())) {
            velocity.x = -MOVE_SPEED;
            facingRight = false;
        }

        // Movimento Direita
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || (controller != null && controller.isRightPressed())) {
            velocity.x = MOVE_SPEED;
            facingRight = true;
        }

        // Pulo
        boolean jumpRequested = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W) || (controller != null && controller.isJumpPressed());
        if (jumpRequested && isGrounded) {
            velocity.y = JUMP_VELOCITY;
            isGrounded = false;
        }
    }

    public void handlePlatformCollision(Rectangle platform) {
        // Colisão por cima (Piso)
        if (velocity.y <= 0 && position.y + 12 >= platform.y + platform.height) {
            position.y = platform.y + platform.height;
            velocity.y = 0;
            isGrounded = true;
        }
        // Colisão por baixo (Teto)
        else if (velocity.y > 0 && position.y + bounds.height - 12 <= platform.y) {
            position.y = platform.y - bounds.height;
            velocity.y = 0;
        }
        bounds.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentRegion;

        switch (currentState) {
            case RUNNING:
                currentRegion = runAnimation.getKeyFrame(stateTime, true);
                break;
            case JUMPING:
                currentRegion = jumpUpRegion;
                break;
            case FALLING:
                currentRegion = jumpFallRegion;
                break;
            case IDLE:
            default:
                currentRegion = idleRegion;
                break;
        }

        // Ajusta orientação (flip) para a direção que o personagem está olhando
        if (facingRight && currentRegion.isFlipX()) {
            currentRegion.flip(true, false);
        } else if (!facingRight && !currentRegion.isFlipX()) {
            currentRegion.flip(true, false);
        }

        // Desenha a sprite posicionada sobre a hitbox do personagem
        batch.draw(currentRegion,
                position.x + DRAW_OFFSET_X,
                position.y + DRAW_OFFSET_Y,
                DRAW_WIDTH,
                DRAW_HEIGHT);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void dispose() {
        if (idleTexture != null) idleTexture.dispose();
        if (jumpUpTexture != null) jumpUpTexture.dispose();
        if (jumpFallTexture != null) jumpFallTexture.dispose();
        if (runTextures != null) {
            for (Texture t : runTextures) {
                if (t != null) t.dispose();
            }
        }
    }
}
