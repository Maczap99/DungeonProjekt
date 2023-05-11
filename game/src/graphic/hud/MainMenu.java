package graphic.hud;

import com.badlogic.gdx.Gdx;
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
import level.LevelAPI;
import level.tools.LevelSize;
import starter.Game;
import tools.EntityFileSystem;

import java.util.HashMap;

public class MainMenu<T extends Actor> extends ScreenController<T> {

    private Game game;
    private LevelAPI levelAPI;
    private Table table;
    private TextButton newButton;
    private TextButton saveButton;
    private TextButton loadButton;

    private static boolean initialState = true;

    /**
     * Constructs a MainMenu object with a given Game instance and LevelAPI instance.
     *
     * @param game     The Game instance.
     * @param levelAPI The LevelAPI instance.
     */
    public MainMenu(Game game, LevelAPI levelAPI) {
        this(new SpriteBatch());
        this.game = game;
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
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 46;
        BitmapFont buttonFont = generator.generateFont(parameter);
        generator.dispose();

        // Initialize label and button styles
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = buttonFont;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = labelStyle.font;
        buttonStyle.fontColor = Color.GREEN;
        buttonStyle.downFontColor = Color.YELLOW;
        buttonStyle.overFontColor = Color.RED;
        buttonStyle.disabledFontColor = Color.GRAY;

        // Create the table for UI elements
        table = new Table();

        try{
            table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("game/assets/menu/start1.png"))));
        }catch (Exception e){
            var backgroundColor = new Color(0f, 0f, 0f, 1f);
            var backgroundDrawable = new ColorBackground(backgroundColor);
            table.setBackground(backgroundDrawable);
        }


        table.setFillParent(true);

        // Create buttons and add listeners
        newButton = new TextButton("New", buttonStyle);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!newButton.isDisabled()) {
                    EntityFileSystem.deleteSaveGame();

                    Game.setHero(new Hero());
                    levelAPI.loadLevel(LevelSize.MEDIUM);

                    refreshUI();
                    Game.toggleMainMenu();

                    Game.gameLoaded = false;
                }
            }
        });

        saveButton = new TextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!saveButton.isDisabled()) {
                    for (Entity entity : Game.getEntities()) {
                        System.out.println("Entität gespeichert: " + entity.getClass().getName() + " ID: " + entity.id);
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

                    Game.getEntities().clear();

                    for (Entity entity : entities) {
                        entity.components = new HashMap<>();
                        if (entity instanceof Hero) {
                            ((Hero) entity).setup();
                            Game.setHero(entity);
                        }
                        System.out.println("Entität geladen: " + entity.getClass().getName() + " ID: " + entity.id);
                    }

                    refreshUI();
                    Game.toggleMainMenu();

                    Game.gameLoaded = true;

                    levelAPI.loadLevel(LevelSize.MEDIUM);
                }
            }
        });

        // Add buttons to the table
        table.add(newButton).width(Gdx.graphics.getWidth()).row();
        table.add(saveButton).width(Gdx.graphics.getWidth()).row();
        table.add(loadButton).width(Gdx.graphics.getWidth()).row();
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
        saveButton.setDisabled(false);

        var backgroundColor = new Color(0f, 0f, 0f, .8f);
        var backgroundDrawable = new ColorBackground(backgroundColor);
        table.setBackground(backgroundDrawable);

        initialState = false;
    }

    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

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
