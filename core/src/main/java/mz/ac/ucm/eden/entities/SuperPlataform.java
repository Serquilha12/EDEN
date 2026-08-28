package mz.ac.ucm.eden.entities;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SuperPlataform {
    private Rectangle bounds;
    private Texture texture;

    public SuperPlataform(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);

        // Criando uma textura de bloco de cor sólida simples para prototipagem
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.3f, 0.7f, 0.3f, 1f); // Verde
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
