package graphic.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class RainbowLayerDrawable implements Drawable {
    private Sprite sprite;
    private float hue; // Farbton-Wert
    private float hueSpeed; // Geschwindigkeit der Farbton-Animation

    public RainbowLayerDrawable(float width, float height) {
        sprite = createSpiralSprite(width, height);
        hue = 0; // Startwert für den Farbton
        hueSpeed = 0.1f; // Geschwindigkeit der Farbton-Animation
    }

    private Sprite createSpiralSprite(float width, float height) {
        int size = (int) Math.max(width, height);
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Hintergrund transparent setzen
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        int centerX = size / 2;
        int centerY = size / 2;
        int radius = Math.min(centerX, centerY);

        int maxSegments = (int) (2 * MathUtils.PI * radius);
        float anglePerSegment = 360f / maxSegments;
        float segmentLength = 2f;

        for (int i = 0; i < maxSegments; i++) {
            // Farbton aktualisieren
            hue += hueSpeed;
            if (hue > 1) {
                hue -= 1;
            }

            // Farbe für das Segment berechnen
            Color rainbowColor = convertHSBtoRGB(hue, 1, 1);
            pixmap.setColor(rainbowColor);

            float angle = i * anglePerSegment;
            int segmentWidth = (int) (segmentLength * i);

            // Kreisbogen zeichnen
            int x = centerX + (int) (Math.cos(Math.toRadians(angle)) * (radius + segmentWidth));
            int y = centerY + (int) (Math.sin(Math.toRadians(angle)) * (radius + segmentWidth));
            pixmap.drawLine(centerX, centerY, x, y);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegion textureRegion = new TextureRegion(texture, (int) width, (int) height);
        return new Sprite(textureRegion);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        sprite.setBounds(x, y, width, height);
        sprite.draw(batch);
    }

    @Override
    public float getLeftWidth() {
        return 0;
    }

    @Override
    public void setLeftWidth(float leftWidth) {
    }

    @Override
    public float getRightWidth() {
        return 0;
    }

    @Override
    public void setRightWidth(float rightWidth) {
    }

    @Override
    public float getTopHeight() {
        return 0;
    }

    @Override
    public void setTopHeight(float topHeight) {
    }

    @Override
    public float getBottomHeight() {
        return 0;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
    }

    @Override
    public float getMinWidth() {
        return 0;
    }

    @Override
    public void setMinWidth(float minWidth) {
    }

    @Override
    public float getMinHeight() {
        return 0;
    }

    @Override
    public void setMinHeight(float minHeight) {
    }

    private Color convertHSBtoRGB(float hue, float saturation, float brightness) {
        float r, g, b;
        int i = (int) (hue * 6);
        float f = hue * 6 - i;
        float p = brightness * (1 - saturation);
        float q = brightness * (1 - f * saturation);
        float t = brightness * (1 - (1 - f) * saturation);
        switch (i % 6) {
            case 0:
                r = brightness;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = brightness;
                b = p;
                break;
            case 2:
                r = p;
                g = brightness;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = brightness;
                break;
            case 4:
                r = t;
                g = p;
                b = brightness;
                break;
            case 5:
                r = brightness;
                g = p;
                b = q;
                break;
            default:
                r = 0;
                g = 0;
                b = 0;
                break;
        }
        return new Color(r, g, b, 1f);
    }
}
