package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import tools.Constants;
import tools.Point;

public class MainMenu<T extends Actor> extends ScreenController<T> {

    /** Creates a new PauseMenu with a new Spritebatch */
    public MainMenu() {
        this(new SpriteBatch());
    }

    /** Creates a new PauseMenu with a given Spritebatch */
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

        Table table = new Table();

        var backgroundColor = new Color(0f, 0f, 0f, 0.8f);

        var colorDrawable = new ColorDrawable(backgroundColor);

        table.setBackground(colorDrawable);

        table.setFillParent(true);

        var newButton = new TextButton("New Game", buttonStyle);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("new button clicked");
            }
        });

        var saveButton = new TextButton("Save Game", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("save button clicked");
            }
        });

        var loadButton = new TextButton("Load Game", buttonStyle);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("load button clicked");
            }
        });

        table.add(newButton).width(Gdx.graphics.getWidth()).row();
        table.add(saveButton).width(Gdx.graphics.getWidth()).row();
        table.add(loadButton).width(Gdx.graphics.getWidth()).row();

        table.align(Align.center);

        //table.add().expandY();

        add((T) table);

        hideMenu();
    }

    /** shows the Menu */
    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    /** hides the Menu */
    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
