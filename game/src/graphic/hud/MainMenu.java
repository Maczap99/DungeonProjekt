package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import ecs.entities.Entity;
import starter.Game;
import tools.EntityFileSystem;

public class MainMenu<T extends Actor> extends ScreenController<T> {

    private Table table;
    private TextButton newButton;
    private TextButton saveButton;
    private TextButton loadButton;

    public MainMenu() {
        this(new SpriteBatch());
    }

    public MainMenu(SpriteBatch batch) {
        super(batch);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game/assets/fonts/pixelplay.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 46;

        BitmapFont buttonFont = generator.generateFont(parameter);

        /*
        * Close the generator to save memory
        * */
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = buttonFont;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = labelStyle.font;
        buttonStyle.fontColor = Color.GREEN;
        buttonStyle.downFontColor = Color.YELLOW;
        buttonStyle.overFontColor = Color.RED;
        buttonStyle.disabledFontColor = Color.GRAY;

        table = new Table();

        var backgroundColor = new Color(0f, 0f, 0f, 1f);

        var backgroundDrawable = new ColorBackground(backgroundColor);

        table.setBackground(backgroundDrawable);

        table.setFillParent(true);

        newButton = new TextButton("New", buttonStyle);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityFileSystem.deleteSaveGame();

                refreshUI();
            }
        });

        saveButton = new TextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (Entity entity : Game.getEntities()) {
                    System.out.println("test " + entity.getClass().getName());
                }

                EntityFileSystem.saveEntities(Game.getEntities());

                refreshUI();
            }
        });
        saveButton.setDisabled(true);

        loadButton = new TextButton("Load", buttonStyle);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var entities = EntityFileSystem.loadEntities();

                for (Entity entity : entities) {
                    System.out.println("test " + entity.getClass().getName());
                }

                refreshUI();
            }
        });

        table.add(newButton).width(Gdx.graphics.getWidth()).row();
        table.add(saveButton).width(Gdx.graphics.getWidth()).row();
        table.add(loadButton).width(Gdx.graphics.getWidth()).row();

        table.align(Align.center);

        add((T) table);
    }

    private void refreshUI() {
        loadButton.setDisabled(!EntityFileSystem.saveGameExists());
        saveButton.setDisabled(false);

        var backgroundColor = new Color(0f, 0f, 0f, .8f);
        var backgroundDrawable = new ColorBackground(backgroundColor);
        table.setBackground(backgroundDrawable);

        hideMenu();
    }

    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
