package starter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import configuration.Configuration;
import configuration.KeyboardConfig;
import controller.AbstractController;
import controller.SystemController;
import ecs.components.Component;
import ecs.components.MissingComponentException;
import ecs.components.PositionComponent;
import ecs.components.xp.XPComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.entities.Monster;
import ecs.systems.*;
import graphic.DungeonCamera;
import graphic.Painter;
import graphic.hud.MainMenu;
import graphic.hud.PauseMenu;
import level.IOnLevelLoader;
import level.LevelAPI;
import level.elements.ILevel;
import level.elements.tile.Tile;
import level.generator.AlternatingGeneratorStrategy;
import level.generator.IGenerator;
import level.generator.maze.MazeGenerator;
import level.generator.postGeneration.WallGenerator;
import level.generator.randomwalk.RandomWalkGenerator;
import level.tools.LevelElement;
import level.tools.LevelSize;
import tools.Constants;
import tools.Point;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static logging.LoggerConfig.initBaseLogger;

/** The heart of the framework. From here all strings are pulled. */
public class Game extends ScreenAdapter implements IOnLevelLoader {

    private final LevelSize LEVELSIZE = LevelSize.MEDIUM;

    /**
     * The batch is necessary to draw ALL the stuff. Every object that uses draw need to know the
     * batch.
     */
    protected SpriteBatch batch;

    /** Contains all Controller of the Dungeon */
    protected List<AbstractController<?>> controller;

    public static DungeonCamera camera;
    /** Draws objects */
    protected Painter painter;

    protected LevelAPI levelAPI;
    /** Generates the level */
    protected IGenerator generator;

    private boolean doSetup = true;
    private static boolean showPauseMenu = false;
    private static boolean showMainMenu = false;

    /** All entities that are currently active in the dungeon */
    private static final Set<Entity> entities = new HashSet<>();
    /** All entities to be removed from the dungeon in the next frame */
    private static final Set<Entity> entitiesToRemove = new HashSet<>();
    /** All entities to be added from the dungeon in the next frame */
    private static final Set<Entity> entitiesToAdd = new HashSet<>();

    /** List of all Systems in the ECS */
    public static SystemController systems;

    public static ILevel currentLevel;
    private static PauseMenu<Actor> pauseMenu;
    private static MainMenu<Actor> mainMenu;
    private static Entity hero;
    private Logger gameLogger;
    private static int levelStage = 1;
    public static boolean gameLoaded, gameOver;

    private transient Sound sound;

    private MonsterBuilder monsterBuilder;

    public static void main(String[] args) {
        // start the game
        try {
            Configuration.loadAndGetConfiguration("dungeon_config.json", KeyboardConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DesktopLauncher.run(new Game());
    }

    /**
     * Main game loop. Redraws the dungeon and calls the own implementation (beginFrame, endFrame
     * and onLevelLoad).
     *
     * @param delta Time since last loop.
     */
    @Override
    public void render(float delta) {
        if (doSetup) setup();
        batch.setProjectionMatrix(camera.combined);
        frame();
        clearScreen();
        levelAPI.update();
        controller.forEach(AbstractController::update);
        camera.update();
    }

    /** Called once at the beginning of the game. */
    protected void setup() {
        doSetup = false;
        setupCameras();
        painter = new Painter(batch, camera);
        initBaseLogger();
        gameLogger = Logger.getLogger(this.getClass().getName());
        systems = new SystemController();

        var generators = new ArrayList<IGenerator>();
        generators.add(new WallGenerator(new RandomWalkGenerator()));
        generators.add(new MazeGenerator(true, true));

        levelAPI = new LevelAPI(batch, painter, new AlternatingGeneratorStrategy(), generators, this);
        levelAPI.loadLevel(LEVELSIZE);

        controller = new ArrayList<>();
        controller.add(systems);
        pauseMenu = new PauseMenu<>();
        controller.add(pauseMenu);
        mainMenu = new MainMenu<>(levelAPI);
        controller.add(mainMenu);

        hero = new Hero();
        monsterBuilder = new MonsterBuilder();

        createSystems();

        /*
        * Open main menu on start.
        * */
        toggleMainMenu();
    }

    /** Called at the beginning of each frame. Before the controllers call <code>update</code>. */
    protected void frame() {
        setCameraFocus();
        manageEntitiesSets();
        getHero().ifPresent(this::loadNextLevelIfEntityIsOnEndTile);

        // if the time for the slow trap is zero, reset speed
        Hero h = (Hero) hero;
        if (h.getTrapTimer() != null
            && h.getTrapTimer().isFinished()) {
            h.resetSpeed();
            systems.update();
        }

        if (h.getCurrentHealth() <= 0 && !gameOver) {
            mainMenu.onGameOver();
            toggleMainMenu();
        }

        if(h.getCurrentMana() < h.getMana()){
            h.setCurrentMana(Math.min(h.getCurrentMana() + (0.5f / Constants.FRAME_RATE), 100));
        }

        //if (Gdx.input.isKeyJustPressed(Input.Keys.P)) togglePauseMenu();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !MainMenu.isInitialState() && !gameOver)
            toggleMainMenu();
    }

    @Override
    public void onLevelLoad() {
        currentLevel = levelAPI.getCurrentLevel();
        entities.clear();
        getHero().ifPresent(this::placeOnLevelStart);

        int monsterAmount = ThreadLocalRandom.current().nextInt(5, 11);
        monsterBuilder = new MonsterBuilder();
        for(int i = 0; i < monsterAmount; i++){
            Monster monster = monsterBuilder.createRandomMonster();
            PositionComponent pc = (PositionComponent) monster.getComponent(PositionComponent.class).get();
            pc.setPosition(currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinateAsPoint());
        }

    }

    private void manageEntitiesSets() {
        entities.removeAll(entitiesToRemove);
        entities.addAll(entitiesToAdd);
        for (Entity entity : entitiesToRemove) {
            gameLogger.info("Entity '" + entity.getClass().getSimpleName() + "' was deleted.");
        }
        for (Entity entity : entitiesToAdd) {
            gameLogger.info("Entity '" + entity.getClass().getSimpleName() + "' was added.");
        }
        entitiesToRemove.clear();
        entitiesToAdd.clear();
    }

    private void setCameraFocus() {
        if (getHero().isPresent()) {
            PositionComponent pc =
                (PositionComponent)
                    getHero()
                        .get()
                        .getComponent(PositionComponent.class)
                        .orElseThrow(
                            () ->
                                new MissingComponentException(
                                    "PositionComponent"));
            camera.setFocusPoint(pc.getPosition());

        } else camera.setFocusPoint(new Point(0, 0));
    }

    public void loadNextLevelIfEntityIsOnEndTile(Entity hero) {
        if (isOnEndTile(hero)){
            levelStage++;
            System.out.println("Ebene: "+levelStage);

            try{
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/effect/door.mp3"));
                sound.play(0.5f);

            }catch (Exception e){
                System.out.println("Sounddatei konnte nicht gefunden werden");
            }

            if(levelStage % 2 == 0){
                Optional<Component> xp = hero.getComponent(XPComponent.class);

                if(xp.isPresent()){
                    XPComponent x = (XPComponent) xp.get();
                    x.setCurrentLevel(x.getCurrentLevel() + 1);

                    System.out.println("Level up! Level: " + x.getCurrentLevel());
                    Hero h = (Hero) Game.getHero().get();
                    h.setCurrentLevel(x.getCurrentLevel());

                    if(x.getCurrentLevel() == 2){
                        System.out.println("Skill Speed Up Freigeschaltet! (Aktivierung: F)");
                    } else if (x.getCurrentLevel() == 3) {
                        System.out.println("Skill Cure Freigeschaltet! (Aktivierung: G)");
                    } else if (x.getCurrentLevel() == 4) {
                        System.out.println("Skill Healing Freigeschaltet! (Aktivierung: H)");
                    }

                }
            }

            levelAPI.loadLevel(LEVELSIZE);
        }
    }

    private boolean isOnEndTile(Entity entity) {
        PositionComponent pc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        Tile currentTile = currentLevel.getTileAt(pc.getPosition().toCoordinate());
        return currentTile.equals(currentLevel.getEndTile());
    }

    private void placeOnLevelStart(Entity hero) {
        entities.add(hero);
        PositionComponent pc =
            (PositionComponent)
                hero.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        pc.setPosition(currentLevel.getStartTile().getCoordinate().toPoint());
    }

    /** Toggle between pause and run */
    public static void togglePauseMenu() {
        showPauseMenu = !showPauseMenu;
        if (systems != null) {
            systems.forEach(ECS_System::toggleRun);
        }
        if (pauseMenu != null) {
            if (showPauseMenu) pauseMenu.showMenu();
            else pauseMenu.hideMenu();
        }
    }

    /** Switch between main menu and game mode */
    public static void toggleMainMenu() {
        showMainMenu = !showMainMenu;
        if (systems != null) {
            systems.forEach(ECS_System::toggleRun);
        }
        if (mainMenu != null) {
            if (showMainMenu) mainMenu.showMenu();
            else mainMenu.hideMenu();
        }
    }

    /**
     * Given entity will be added to the game in the next frame
     *
     * @param entity will be added to the game next frame
     */
    public static void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    /**
     * Given entity will be removed from the game in the next frame
     *
     * @param entity will be removed from the game next frame
     */
    public static void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    /**
     * @return Set with all entities currently in game
     */
    public static Set<Entity> getEntities() {
        return entities;
    }

    /**
     * @return Set with all entities that will be added to the game next frame
     */
    public static Set<Entity> getEntitiesToAdd() {
        return entitiesToAdd;
    }

    /**
     * @return Set with all entities that will be removed from the game next frame
     */
    public static Set<Entity> getEntitiesToRemove() {
        return entitiesToRemove;
    }

    /**
     * @return the player character, can be null if not initialized
     */
    public static Optional<Entity> getHero() {
        return Optional.ofNullable(hero);
    }

    /**
     * set the reference of the playable character careful: old hero will not be removed from the
     * game
     *
     * @param hero new reference of hero
     */
    public static void setHero(Entity hero) {
        Game.hero = hero;
    }

    public void setSpriteBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void setupCameras() {
        camera = new DungeonCamera(null, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.zoom = Constants.DEFAULT_ZOOM_FACTOR;

        // See also:
        // https://stackoverflow.com/questions/52011592/libgdx-set-ortho-camera
    }

    private void createSystems() {
        new VelocitySystem();
        new DrawSystem(painter);
        new PlayerSystem();
        new AISystem();
        new CollisionSystem();
        new HealthSystem();
        new XPSystem();
        new SkillSystem();
        new ProjectileSystem();
    }

    /** Getter & Setter ************************/

    public static int getLevelStage() {
        return levelStage;
    }

    public void setLevelStage(int levelStage) {
        this.levelStage = levelStage;
    }
}
