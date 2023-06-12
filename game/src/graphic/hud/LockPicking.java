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

import java.util.Random;

public class LockPicking<T extends Actor> extends ScreenController<T> {
    private static final Random RANDOM = new Random();
    private static final float MOVEMENT_SPEED = 100f;
    private static final float MAX_MOVEMENT_DISTANCE = 20f;
    private final Image circleImage;
    private final Image squareImage1;
    private final Image squareImage2;
    private final Image[] bolts;
    private float movementDistance;
    private float movementAngle;

    private boolean solved;

    public LockPicking() {
        super(new SpriteBatch());

        // Random
        System.out.println(RANDOM.nextInt(4, 8));

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
        squareImage2 = new Image(new TextureRegionDrawable(createSquareTexture(Color.GREEN, 100)));

        // Set position and scaling for the square
        squareImage2.setPosition(300, 100);
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

        int numBolts = RANDOM.nextInt(4, 8);

        bolts = new Image[numBolts];

        float windowWidth = Constants.WINDOW_WIDTH;
        float windowHeight = Constants.WINDOW_HEIGHT;

        float boltWidth = windowWidth / (numBolts + (numBolts + 1) * 0.2f); // +1 for spacing between bolts
        float boltHeight = windowHeight * 0.4f;

        float spacing = windowWidth * 0.2f / (numBolts + 1);

        float startY = (windowHeight - boltHeight) / 2f;

        for (int i = 0; i < numBolts; i++) {
            float startX = (i + 1) * spacing + i * boltWidth;
            bolts[i] = createBolt(startX, startY, boltWidth, boltHeight);
            addBoltListener(bolts[i]);
            add((T) bolts[i]);
        }

        //add((T) circleImage);

        //add((T) squareImage1);

        //add((T) squareImage2);

        hide();
    }

    private Image createBolt(float x, float y, float width, float height) {
        Image boltImage = new Image(new TextureRegionDrawable(createBoltTexture(Color.GRAY, (int) width, (int) height)));
        boltImage.setPosition(x, y);
        boltImage.setScaling(Scaling.none);
        return boltImage;
    }

    private Texture createBoltTexture(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void addBoltListener(Image boltImage) {
        boltImage.addListener(new InputListener() {
            private boolean clicked;
            private float initialY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!clicked) {
                    clicked = true;
                    initialY = boltImage.getY();
                    if (button == 0) // Left mouse button
                        moveBoltUp(boltImage);
                    else if (button == 1) // Right mouse button
                        moveBoltDown(boltImage);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (clicked) {
                    clicked = false;
                    resetBoltPosition(boltImage, initialY);
                }
            }
        });
    }

    private void moveBoltUp(Image boltImage) {
        float targetY = boltImage.getY() + 50; // Move up by 50 units
        float duration = 0.5f; // Duration of the animation in seconds

        boltImage.clearActions(); // Clear any ongoing actions
        boltImage.addAction(Actions.moveTo(boltImage.getX(), targetY, duration));
    }

    private void moveBoltDown(Image boltImage) {
        float targetY = boltImage.getY() - 50; // Move down by 50 units
        float duration = 0.5f; // Duration of the animation in seconds

        boltImage.clearActions(); // Clear any ongoing actions
        boltImage.addAction(Actions.moveTo(boltImage.getX(), targetY, duration));
    }

    private void resetBoltPosition(Image boltImage, float initialY) {
        if (boltImage.getActions().size == 0) { // Check if no other action is ongoing
            float duration = 0.5f; // Duration of the animation in seconds

            boltImage.clearActions(); // Clear any ongoing actions
            boltImage.addAction(Actions.moveTo(boltImage.getX(), initialY, duration));
        }
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

        if (checkOverlap(squareImage1, squareImage2)) {
            System.out.println("Does overlap");
        }
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

    private boolean checkOverlap(Image square1, Image square2) {
        float x1 = square1.getX();
        float y1 = square1.getY();
        float width1 = square1.getWidth();
        float height1 = square1.getHeight();

        float x2 = square2.getX();
        float y2 = square2.getY();
        float width2 = square2.getWidth();
        float height2 = square2.getHeight();

        // Überprüfung auf Überlappung
        if (x1 < x2 + width2 && x1 + width1 > x2 && y1 < y2 + height2 && y1 + height1 > y2) {
            // Überlappung gefunden
            return true;
        }

        // Keine Überlappung
        return false;
    }


    public void show() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    public void hide() {
        this.forEach((Actor s) -> s.setVisible(false));
    }

    public boolean isSolved() {
        return solved;
    }
}
