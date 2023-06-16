package graphic.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Bolt extends Image {
    private int orderNumber;
    private Label label;
    private boolean moved;

    public Bolt(Drawable drawable, Label label) {
        super(drawable);
        this.label = label;
    }

    public void setOrder(int number) {
        if (label != null) {
            orderNumber = number;
            label.setText(Integer.toString(number));
        }
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public Label getLabel() {
        return label;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
