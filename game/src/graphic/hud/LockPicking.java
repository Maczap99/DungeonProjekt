package graphic.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

/**
 * The LockPicking class represents a screen controller for a lock picking game.
 * @param <T> The type of actor used in the screen controller.
 */
public class LockPicking<T extends Actor> extends ScreenController<T> {
    private static final Random RANDOM = new Random();
    private final Image squareImage1;
    private final Bolt[] bolts;
    private final Image background;
    private final Label statusLabel;
    private final int[] orderNumbers;
    private int currentBoltIndex = 1;
    private boolean actionsLocked;
    private static boolean solved;

    /**
     * Constructs a new LockPicking screen controller.
     */
    public LockPicking() {
        super(new SpriteBatch());

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

        // Create status label
        Label.LabelStyle statusLabelStyle = new Label.LabelStyle();
        statusLabelStyle.font = new BitmapFont();
        statusLabelStyle.font.getData().setScale(4f);
        statusLabelStyle.fontColor = Color.RED;
        statusLabel = new Label("locked!", statusLabelStyle);
        statusLabel.setPosition(Constants.WINDOW_WIDTH / 2f - 10f, Constants.WINDOW_HEIGHT - 50f, Align.center);

        int numBolts = RANDOM.nextInt(4, 8);

        String difficultyLabel = getDifficultyLabel(numBolts);
        Label difficultyLevelLabel = createDifficultyLevelLabel(difficultyLabel);
        difficultyLevelLabel.setPosition(10, Constants.WINDOW_HEIGHT - 20);

        bolts = new Bolt[numBolts];

        float windowWidth = Constants.WINDOW_WIDTH;
        float windowHeight = Constants.WINDOW_HEIGHT;

        float spacing = windowWidth * 0.7f / (numBolts + 1);
        float boltWidth = (windowWidth - spacing * (numBolts + 1)) / numBolts;
        float boltHeight = windowHeight * 0.2f;

        float startY = (windowHeight - boltHeight) / 2f - 50f;

        // Adds background
        background = new Image(new Texture("hud/chest_background2.png"));
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
            int orderNumber = orderNumbers[i];
            float startX = spacing * (i + 1) + boltWidth * i;
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

        //add((T) squareImage1);

        hide();
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
                    if (button == 0) {
                        if (!bolt.isMoved()) {
                            moveBoltUp(bolt);
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
            Actions.run(() -> onBoltReachedUpperPosition(bolt))
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
        } else {
            bolt.setColor(Color.RED);
            resetBoltPosition();
        }
        bolt.setMoved(true);

        updateStatusLabel();
    }

    private void resetBoltPosition() {
        actionsLocked = true;

        for (Bolt bolt : bolts) {
            if (bolt.isMoved()) {
                // Duration of the animation in seconds
                float duration = .5f;

                // Clear any ongoing actions
                bolt.clearActions();
                bolt.addAction(Actions.sequence(
                    Actions.moveTo(bolt.getX(), bolt.getOriginY(), duration),
                    Actions.run(() -> onBoltReachedLowerPosition())
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

        //if (checkOverlap(squareImage1, squareImage2)) {
        //    System.out.println("Does overlap");
        //}
    }

    private String getDifficultyLabel(int numBolts) {
        String difficultyLabel;
        switch (numBolts) {
            case 4:
                difficultyLabel = "Difficulty Level: Easy";
                break;
            case 5:
                difficultyLabel = "Difficulty Level: Normal";
                break;
            case 6:
                difficultyLabel = "Difficulty Level: Hard";
                break;
            case 7:
                difficultyLabel = "Difficulty Level: Very Hard";
                break;
            default:
                difficultyLabel = "Difficulty Level: Unknown";
                break;
        }
        return difficultyLabel;
    }

    private Label createDifficultyLevelLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        return label;
    }

    private void updateStatusLabel() {
        if (solved) {
            statusLabel.setText("Unlocked!");
            statusLabel.getStyle().fontColor = Color.GREEN;
        }
    }

    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = RANDOM.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Makes all the actors in the LockPicking screen controller visible.
     */
    public void show() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    /**
     * Makes all the actors in the LockPicking screen controller invisible.
     */
    public void hide() {
        this.forEach((Actor s) -> s.setVisible(false));
    }

    /**
     * Checks if the lock picking game has been solved.
     *
     * @return True if the game is solved, false otherwise.
     */
    public static boolean isSolved() {
        return solved;
    }
}
