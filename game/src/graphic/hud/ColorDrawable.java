package graphic.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ColorDrawable implements Drawable {
    private Color color;

    public ColorDrawable(Color color) {
        this.color = color;
    }

    public static Texture createTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.setColor(color);
        batch.draw(createTexture(color), x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    @Override
    public float getLeftWidth() {
        return 0;
    }

    @Override
    public void setLeftWidth(float leftWidth) {}

    @Override
    public float getRightWidth() {
        return 0;
    }

    @Override
    public void setRightWidth(float rightWidth) {}

    @Override
    public float getTopHeight() {
        return 0;
    }

    @Override
    public void setTopHeight(float topHeight) {}

    @Override
    public float getBottomHeight() {
        return 0;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {}

    @Override
    public float getMinWidth() {
        return 0;
    }

    @Override
    public void setMinWidth(float minWidth) {}

    @Override
    public float getMinHeight() {
        return 0;
    }

    @Override
    public void setMinHeight(float minHeight) {}
}
