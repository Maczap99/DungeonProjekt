package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import controller.ScreenController;
import tools.Constants;

public class LockPicking<T extends Actor> extends ScreenController<T> {
    private static final float MOVEMENT_SPEED = 100f;
    private static final float MAX_MOVEMENT_DISTANCE = 20f;
    private final Image circleImage;
    private final Image squareImage1;
    private final Image squareImage2;
    private float movementDistance;
    private float movementAngle;

    public LockPicking() {
        super(new SpriteBatch());

        // Calculates the center position of the window
        float centerX = Constants.WINDOW_WIDTH / 2f;
        float centerY = Constants.WINDOW_HEIGHT / 2f;

        // Defines the radius of the circle based on the window size
        float circleRadius = Math.min(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT) * 0.2f;

        // Calculates the position for the circle's top-left corner to center it
        float circleX = centerX - circleRadius;
        float circleY = centerY - circleRadius;

        // Creates a circle image
        circleImage = new Image(new TextureRegionDrawable(createCircleTexture(Color.RED, (int) circleRadius)));

        // Sets position and scaling for the circle
        circleImage.setPosition(circleX, circleY);
        circleImage.setScaling(Scaling.none);

        // Adds click listener to the circle
        circleImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Circle clicked!");
                return true;
            }
        });

        // Create a square image
        squareImage1 = new Image(new TextureRegionDrawable(createSquareTexture(Color.BLUE, 100)));

        // Set position and scaling for the square
        squareImage1.setPosition(100, 100);
        squareImage1.setScaling(Scaling.none);
        //squareImage.setBounds(squareImage.getX(), squareImage.getY(), squareImage.getWidth(), squareImage.getHeight());

        squareImage1.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                float newX = squareImage1.getX() + x - squareImage1.getWidth() / 2f;
                float newY = squareImage1.getY() + y - squareImage1.getHeight() / 2f;
                squareImage1.setPosition(newX, newY);
            }
        });

        // Create a square image
        squareImage2 = new Image(new TextureRegionDrawable(createSquareTexture(Color.BLUE, 100)));

        // Set position and scaling for the square
        squareImage2.setPosition(100, 100);
        squareImage2.setScaling(Scaling.none);

        squareImage2.addListener(new InputListener() {
            private boolean clicked;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!clicked) {
                    clicked = true;
                    moveSquareUp();
                }
                return true;
            }
        });

        //add((T) circleImage);

        //add((T) squareImage1);

        add((T) squareImage2);

        hide();
    }

    private static Texture createCircleTexture(Color color, int radius) {
        Pixmap pixmap = new Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(radius, radius, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static Texture createSquareTexture(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, size, size);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void update() {
        super.update();

        // Move the circle
        moveCircle();
    }

    private void moveCircle() {
        if (movementDistance <= 0) {
            // Generate new movement parameters
            movementDistance = MathUtils.random(10f, MAX_MOVEMENT_DISTANCE);
            movementAngle = MathUtils.random(0f, MathUtils.PI2);
        }

        // Calculate the movement delta
        float delta = Math.min(movementDistance, MOVEMENT_SPEED * Gdx.graphics.getDeltaTime());

        // Calculate the new position
        float deltaX = delta * MathUtils.cos(movementAngle);
        float deltaY = delta * MathUtils.sin(movementAngle);
        float newX = circleImage.getX() + deltaX;
        float newY = circleImage.getY() + deltaY;

        // Update the position of the circle
        circleImage.setPosition(newX, newY);

        // Update the movement distance
        movementDistance -= delta;
    }

    private void moveSquareUp() {
        float targetY = squareImage2.getY() + 100; // Move up by 50 units
        float duration = 1.0f; // Duration of the animation in seconds

        squareImage2.addAction(Actions.moveTo(squareImage2.getX(), targetY, duration));
    }

    public void show() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    public void hide() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
