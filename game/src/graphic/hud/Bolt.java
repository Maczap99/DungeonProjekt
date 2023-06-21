package graphic.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * The Bolt class represents a graphical bolt object that extends the Image class.
 */
public class Bolt extends Image {
    private int orderNumber;
    private Label label;
    private boolean moved;
    private float originY;

    /**
     * Constructs a new Bolt object with the specified drawable, label, and origin Y-coordinate.
     *
     * @param drawable The drawable representing the visual appearance of the bolt.
     * @param label    The label associated with the bolt.
     * @param originY  The Y-coordinate origin of the bolt.
     */
    public Bolt(Drawable drawable, Label label, float originY) {
        super(drawable);
        this.label = label;
        this.originY = originY;
    }

    /**
     * Sets the order number of the bolt and updates the associated label text.
     *
     * @param number The order number to be set.
     */
    public void setOrder(int number) {
        if (label != null) {
            orderNumber = number;
            label.setText(Integer.toString(number));
        }
    }

    /**
     * Returns the order number of the bolt.
     *
     * @return The order number of the bolt.
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * Returns the label associated with the bolt.
     *
     * @return The label associated with the bolt.
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Checks if the bolt has been moved.
     *
     * @return {@code true} if the bolt has been moved, {@code false} otherwise.
     */
    public boolean isMoved() {
        return moved;
    }

    /**
     * Sets the moved status of the bolt.
     *
     * @param moved {@code true} if the bolt has been moved, {@code false} otherwise.
     */
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    /**
     * Returns the Y-coordinate origin of the bolt.
     *
     * @return The Y-coordinate origin of the bolt.
     */
    @Override
    public float getOriginY() {
        return originY;
    }
}
