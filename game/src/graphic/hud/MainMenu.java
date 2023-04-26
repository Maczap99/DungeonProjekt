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

public class MainMenu<T extends Actor> extends ScreenController<T> {

    public MainMenu() {
        this(new SpriteBatch());
    }

    public MainMenu(SpriteBatch batch) {
        super(batch);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game/assets/fonts/pixelplay.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 46;

        BitmapFont buttonFont = generator.generateFont(parameter);

        //BitmapFont labelFont = generator.generateFont(parameter);

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

        Table table = new Table();

        var backgroundColor = new Color(0f, 0f, 0f, 0.8f);

        var backgroundDrawable = new ColorBackground(backgroundColor);

        table.setBackground(backgroundDrawable);

        table.setFillParent(true);

        var newButton = new TextButton("New", buttonStyle);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("new button clicked");
            }
        });

        var saveButton = new TextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("save button clicked");
            }
        });

        var loadButton = new TextButton("Load", buttonStyle);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("load button clicked");
            }
        });

        var exitButton = new TextButton("Exit", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("exit button clicked");
            }
        });

        newButton.setDisabled(true);
        exitButton.setDisabled(true);

        table.add(newButton).width(Gdx.graphics.getWidth()).row();
        table.add(saveButton).width(Gdx.graphics.getWidth()).row();
        table.add(loadButton).width(Gdx.graphics.getWidth()).row();
        table.add(exitButton).width(Gdx.graphics.getWidth()).row();

        table.align(Align.center);

        add((T) table);

        hideMenu();
    }

    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
