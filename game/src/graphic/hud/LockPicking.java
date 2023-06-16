package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import controller.ScreenController;
import tools.Constants;

import java.util.Random;

public class LockPicking<T extends Actor> extends ScreenController<T> {
    private static final Random RANDOM = new Random();
    private Label statusLabel;
    private static final float MOVEMENT_SPEED = 100f;
    private static final float MAX_MOVEMENT_DISTANCE = 20f;
    private final Image circleImage;
    private final Image squareImage1;
    private final Image squareImage2;
    private final Bolt[] bolts;
    private int[] orderNumbers;
    private int currentBoltIndex = 1;
    private final Image background;
    private boolean actionsLocked;
    private float movementDistance;
    private float movementAngle;
    private boolean solved;

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

        // Create status label
        Label.LabelStyle statusLabelStyle = new Label.LabelStyle();
        statusLabelStyle.font = new BitmapFont();
        statusLabelStyle.font.getData().setScale(4f);
        statusLabelStyle.fontColor = Color.RED;
        statusLabel = new Label("locked!", statusLabelStyle);
        statusLabel.setPosition(Constants.WINDOW_WIDTH / 2f, Constants.WINDOW_HEIGHT - 50f, Align.center);

        int numBolts = RANDOM.nextInt(4, 8);

        String difficultyLabel = getDifficultyLabel(numBolts);
        Label difficultyLevelLabel = createDifficultyLevelLabel(difficultyLabel);
        difficultyLevelLabel.setPosition(10, Constants.WINDOW_HEIGHT - 20);

        bolts = new Bolt[numBolts];

        float windowWidth = Constants.WINDOW_WIDTH;
        float windowHeight = Constants.WINDOW_HEIGHT;

        float boltWidth = windowWidth / (numBolts + (numBolts + 1) * 0.2f); // +1 for spacing between bolts
        float boltHeight = windowHeight * 0.4f;

        float spacing = windowWidth * 0.2f / (numBolts + 1);

        float startY = (windowHeight - boltHeight) / 2f;

        // Adds background
        background = new Image(new Texture("hud/chest_background.png"));
        background.setBounds(0, 0, windowWidth, windowHeight);

        add((T) background);

        add((T) statusLabel);

        add((T) difficultyLevelLabel);

        // Generate random order numbers for the bolts
        orderNumbers = new int[numBolts];
        for (int i = 0; i < bolts.length; i++) {
            orderNumbers[i] = i + 1;
        }

        // Shuffle the order numbers
        shuffleArray(orderNumbers);

        for (int i = 0; i < bolts.length; i++) {
            float startX = (i + 1) * spacing + i * boltWidth;
            int orderNumber = orderNumbers[i];
            bolts[i] = createBolt(
                startX,
                startY,
                boltWidth,
                boltHeight,
                orderNumber
            );
            addBoltListener(bolts[i]);

            add((T) bolts[i]);
            //add((T) bolts[i].getLabel());
        }

        //add((T) circleImage);

        //add((T) squareImage1);

        //add((T) squareImage2);

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

    private Bolt createBolt(float x, float y, float width, float height, int orderNumber) {
        Bolt bolt = new Bolt(createBoltTexture((int) width, (int) height),
            createBoltLabel(x, y, width, height, Integer.toString(orderNumber)),
            y
        );
        bolt.setPosition(x, y);
        bolt.setScaling(Scaling.none);
        bolt.setOrder(orderNumber);
        bolt.setColor(Color.GRAY);
        return bolt;
    }

    private TextureRegionDrawable createBoltTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private void addBoltListener(Bolt bolt) {
        bolt.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!bolt.isMoved() && !actionsLocked) {
                    if (button == 0) { // Left mouse button
                        if (!bolt.isMoved()) {
                            moveBoltUp(bolt);
                        }
                    } else if (button == 1) { // Right mouse button
                        if (!bolt.isMoved()) {
                            bolt.setMoved(true);
                            currentBoltIndex--;
                        }
                    }
                }
                return true;
            }
        });
    }

    private void moveBoltUp(Bolt bolt) {
        float targetY = bolt.getY() + 50; // Move up by 50 units
        float duration = 0.5f; // Duration of the animation in seconds

        bolt.clearActions(); // Clear any ongoing actions
        bolt.addAction(Actions.sequence(
            Actions.moveTo(bolt.getX(), targetY, duration),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    onBoltReachedUpperPosition(bolt);
                }
            })
        ));

        Label boltLabel = bolt.getLabel();
        boltLabel.clearActions();
        boltLabel.addAction(Actions.moveTo(boltLabel.getX(), targetY + bolt.getHeight() + 10, duration));

        bolt.setMoved(true);
    }

    private void onBoltReachedUpperPosition(Bolt bolt) {
        if (bolt.getOrderNumber() == currentBoltIndex) {
            bolt.setColor(Color.GREEN);
            if (currentBoltIndex == bolts.length) {
                solved = true;
            }
            currentBoltIndex++;
        } else{
            bolt.setColor(Color.RED);
            resetBoltPosition();
        }
        bolt.setMoved(true);

        updateStatusLabel();
    }

    private void resetBoltPosition() {
        actionsLocked = true;

        for (Bolt bolt: bolts) {
            if (bolt.isMoved()) {
                float duration = .5f; // Duration of the animation in seconds

                bolt.clearActions(); // Clear any ongoing actions
                bolt.addAction(Actions.sequence(
                    Actions.moveTo(bolt.getX(), bolt.getOriginY(), duration),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            onBoltReachedLowerPosition();
                        }
                    })
                ));

                Label boltLabel = bolt.getLabel();
                boltLabel.clearActions();
                boltLabel.addAction(Actions.moveTo(boltLabel.getX(), bolt.getOriginY() + bolt.getHeight() + 10, duration));
            }
        }
    }

    private void onBoltReachedLowerPosition() {
        for (Bolt bolt : bolts) {
            if (bolt.isMoved()) {
                bolt.setColor(Color.GRAY);
                currentBoltIndex = 1;
                bolt.setMoved(false);
            }
        }

        actionsLocked = false;
    }

    private Label createBoltLabel(float x, float y, float width, float height, String number) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();

        Label boltLabel = new Label(number, labelStyle);
        boltLabel.setPosition(x + width / 2f, y + height + 10f);
        boltLabel.setAlignment(Align.center);
        boltLabel.setOrigin(Align.center);
        return boltLabel;
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

        return x1 < x2 + width2 && x1 + width1 > x2 && y1 < y2 + height2 && y1 + height1 > y2;
    }

    private String getDifficultyLabel(int numBolts) {
        String difficultyLabel;
        switch (numBolts) {
            case 4:
                difficultyLabel = "Schwierigkeitsstufe: einfach";
                break;
            case 5:
                difficultyLabel = "Schwierigkeitsstufe: normal";
                break;
            case 6:
                difficultyLabel = "Schwierigkeitsstufe: schwer";
                break;
            case 7:
                difficultyLabel = "Schwierigkeitsstufe: sehr schwer";
                break;
            default:
                difficultyLabel = "Schwierigkeitsstufe: unbekannt";
                break;
        }
        return difficultyLabel;
    }

    private Label createDifficultyLevelLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;
        //labelStyle.font.getData().setScale(1.5f);

        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        return label;
    }

    private void updateStatusLabel() {
        if (solved) {
            statusLabel.setText("unlocked!");
            statusLabel.getStyle().fontColor = Color.GREEN;
        }
    }

    // Helper method to shuffle the array
    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = RANDOM.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
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
