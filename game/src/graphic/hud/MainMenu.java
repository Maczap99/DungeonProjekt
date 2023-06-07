package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import ecs.entities.Entity;
import ecs.entities.Hero;
import graphic.effects.RainbowLayerDrawable;
import level.LevelAPI;
import level.tools.LevelSize;
import starter.Game;
import tools.EntityFileSystem;
import java.util.Random;

import java.util.HashMap;
import java.util.logging.Logger;

public class MainMenu<T extends Actor> extends ScreenController<T> {

    private LevelAPI levelAPI;
    private Table table;
    private TextButton newButton;
    private TextButton saveButton;
    private TextButton loadButton;
    private transient Music music;
    private transient Sound sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/effect/gameOver.mp3"));;
    private Label gameOverLabel;
    private transient Logger soundLogger;
    private transient Logger saveLoadLogger;

    private static boolean initialState = true;

    /**
     * Constructs a MainMenu object with a given Game instance and LevelAPI instance.
     *
     * @param levelAPI The LevelAPI instance.
     */
    public MainMenu(LevelAPI levelAPI) {
        this(new SpriteBatch());
        this.levelAPI = levelAPI;
    }

    /**
     * Constructs a MainMenu object with a given SpriteBatch instance.
     *
     * @param batch The SpriteBatch instance.
     */
    public MainMenu(SpriteBatch batch) {
        super(batch);

        // Initialize button font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game/assets/fonts/pixelplay.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParameter.size = 46;
        BitmapFont buttonFont = generator.generateFont(buttonParameter);

        // Initialize button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.GREEN;
        buttonStyle.downFontColor = Color.YELLOW;
        buttonStyle.overFontColor = Color.ORANGE;
        buttonStyle.disabledFontColor = Color.GRAY;

        // Create the table for UI elements
        table = new Table();

        // try picture else set background to black
        try{
            table.setBackground(new RainbowLayerDrawable(400, 400));
        }catch (Exception e){
            var backgroundColor = new Color(0f, 0f, 0f, 1f);
            var backgroundDrawable = new ColorBackground(backgroundColor);
            table.setBackground(backgroundDrawable);
        }

        table.setFillParent(true);

        int secretSound = getSoundNumber(0,29);

        try{
            sound.stop();
            if(secretSound != 7){
                // start menu soundtrack
                music = Gdx.audio.newMusic(Gdx.files.internal("game/sounds/menu/menu1.mp3"));
                music.setLooping(true);
                music.setVolume(0.2f);
                music.play();
            }else{
                // start menu soundtrack
                music = Gdx.audio.newMusic(Gdx.files.internal("game/sounds/menu/menu2.mp3"));
                music.setLooping(true);
                music.setVolume(0.2f);
                music.play();
            }


        }catch (Exception e){
            soundLogger = Logger.getLogger("Sounddatei konnte nicht gefunden werden");
        }

        // Create buttons and add listeners
        newButton = new TextButton("New", buttonStyle);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!newButton.isDisabled()) {
                    EntityFileSystem.deleteSaveGame();

                    music.stop();

                    try{
                        // start menu soundtrack
                        sound.stop();
                        music = Gdx.audio.newMusic(Gdx.files.internal("game/sounds/dungeon/dungeon"+ getSoundNumber(0,4)+".wav"));
                        music.setLooping(true);
                        music.setVolume(0.2f);
                        music.play();

                    }catch (Exception e){
                        soundLogger = Logger.getLogger("Sounddatei konnte nicht gefunden werden");
                    }

                    Game.setHero(new Hero());
                    levelAPI.loadLevel(LevelSize.MEDIUM);

                    Game.toggleMainMenu();

                    Game.gameLoaded = false;

                    Game.gameOver = false;

                    refreshUI();
                }
            }
        });

        saveButton = new TextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!saveButton.isDisabled()) {
                    for (Entity entity : Game.getEntities()) {
                        saveLoadLogger = Logger.getLogger("Entität gespeichert: " + entity.getClass().getName() + " ID: " + entity.id);
                    }

                    EntityFileSystem.saveEntities(Game.getEntities());

                    refreshUI();
                    Game.toggleMainMenu();
                }
            }
        });
        saveButton.setDisabled(true);

        loadButton = new TextButton("Load", buttonStyle);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!loadButton.isDisabled()) {
                    var entities = EntityFileSystem.loadEntities();

                    music.stop();

                    try{
                        // start menu soundtrack
                        sound.stop();
                        music = Gdx.audio.newMusic(Gdx.files.internal("game/sounds/dungeon/dungeon"+ getSoundNumber(0,4)+".wav"));
                        music.setLooping(true);
                        music.setVolume(0.2f);
                        music.play();

                    }catch (Exception e){
                        soundLogger = Logger.getLogger("Sounddatei konnte nicht gefunden werden");
                    }

                    Game.getEntities().clear();

                    for (Entity entity : entities) {
                        entity.components = new HashMap<>();
                        if (entity instanceof Hero) {
                            ((Hero) entity).setup();
                            Game.setHero(entity);
                        }

                        saveLoadLogger = Logger.getLogger("Entität geladen: " + entity.getClass().getName() + " ID: " + entity.id);
                    }

                    Game.toggleMainMenu();

                    Game.gameLoaded = true;

                    Game.gameOver = false;

                    levelAPI.loadLevel(LevelSize.MEDIUM);

                    refreshUI();
                }
            }
        });

        var exitButton = new TextButton("Exit", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Initialize button font
        generator = new FreeTypeFontGenerator(Gdx.files.internal("game/assets/fonts/BLOODY.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter labelParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        labelParameter.size = 56;
        BitmapFont labelFont = generator.generateFont(labelParameter);
        generator.dispose();

        // Creates "Game Over" label
        Label.LabelStyle gameOverLabelStyle = new Label.LabelStyle();
        gameOverLabelStyle.font = labelFont;
        gameOverLabelStyle.fontColor = Color.RED;

        gameOverLabel = new Label("Game Over", gameOverLabelStyle);
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.setVisible(false);

        // Add "Game Over" label
        table.add(gameOverLabel).width(Gdx.graphics.getWidth()).row();

        // Add buttons to the table
        table.add(newButton).width(Gdx.graphics.getWidth()).row();
        table.add(saveButton).width(Gdx.graphics.getWidth()).row();
        table.add(loadButton).width(Gdx.graphics.getWidth()).row();
        table.add(exitButton).width(Gdx.graphics.getWidth()).row();
        table.align(Align.center);

        // Add the table to the stage
        add((T) table);

        // Hide the menu initially
        hideMenu();
    }

    /**
     * Refreshes the UI elements of the main menu.
     * Updates the button states and background appearance.
     */
    private void refreshUI() {
        loadButton.setDisabled(!EntityFileSystem.saveGameExists());

        if (Game.gameOver) {
            saveButton.setDisabled(true);
        } else {
            saveButton.setDisabled(false);
        }

        gameOverLabel.setVisible(Game.gameOver);

        var backgroundColor = new Color(0f, 0f, 0f, .8f);
        var backgroundDrawable = new RainbowLayerDrawable(400, 400);
        table.setBackground(backgroundDrawable);

        initialState = false;
    }

    /**
     * Called when the game is over.
     * Displays the menu and shows the "Game Over" label.
     */
    public void onGameOver() {
        if (!Game.gameOver) {
            Game.gameOver = true;

            try {
                // start menu soundtrack
                music.stop();
                sound.play(0.3f);

            } catch (Exception e) {
                soundLogger = Logger.getLogger("Sounddatei 'GameOver.mp3' konnte nicht gefunden werden");
            }

            refreshUI();
        }
    }

    /**
     * Generates a random number between the specified range.
     *
     * @param first The first number of the range.
     * @param last  The last number of the range.
     * @return The generated random number.
     */
    public int getSoundNumber(int first, int last) {
        Random random = new Random();
        int value = random.nextInt(last + first) + 1;

        return value;
    }

    /**
     * Shows the menu by setting all actors visible.
     */
    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    /**
     * Hides the menu by setting all actors invisible.
     */
    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }

    /*
    * Getter ans Setter
    * */
    public static boolean isInitialState() {
        return initialState;
    }
}
