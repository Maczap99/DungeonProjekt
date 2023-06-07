package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import ecs.components.xp.ILevelUp;
import ecs.components.xp.XPComponent;
import graphic.Animation;
import tools.TrapTimer;

import java.util.Optional;

/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity implements ILevelUp {

    private final int fireballCoolDown = 1;
    private final int healCoolDown = 5;
    private final int cureCoolDown = 1;
    private final int speedCoolDown = 5;
    private float xSpeed = 0.2f;
    private float ySpeed = 0.2f;
    private float mana = 100f;
    private float currentMana = 100f;
    private int health = 100;
    private int currentHealth = 100;
    private long level = 1;
    private long currentLevel = 1;
    private transient TrapTimer trapTimer;
    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private transient Skill firstSkill;
    private transient Skill healSkill;
    private transient Skill cureSkill;
    private transient Skill speedSkill;


    /**
     * Entity with Components
     */
    public Hero() {
        super();

        setup();
    }

    public void setup() {
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        PlayableComponent pc = new PlayableComponent(this);
        setupXPComponent();


        setupFireballSkill();
        pc.setSkillSlot1(firstSkill);

        setupHealSkill();
        pc.setSkillSlot2(healSkill);

        setupCureSkill();
        pc.setSkillSlot3(cureSkill);

        setupSpeedSkill();
        pc.setSkillSlot4(speedSkill);

        setupSkillComponent();

        HealthComponent hc = new HealthComponent(this);
        hc.setMaximalHealthpoints(health);
        hc.setCurrentHealthpoints(currentHealth);

        /*
        * Inventory
        * */
        var inventory = new InventoryComponent(this, 3);
    }

    private void setupXPComponent(){
        XPComponent xpcomponent = new XPComponent(this);
        xpcomponent.setCurrentLevel(currentLevel);
    }

    public void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
        Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
        new AnimationComponent(this, idleLeft, idleRight);
    }

    private void setupSkillComponent(){
        SkillComponent sc = new SkillComponent(this);
        sc.addSkill(firstSkill);
        sc.addSkill(healSkill);
        sc.addSkill(cureSkill);
        sc.addSkill(speedSkill);
    }

    private void setupFireballSkill() {
        firstSkill =
            new Skill(
                new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown, 0);
    }
    private void setupHealSkill() {
        healSkill =
            new Skill(
                new HealSkill(), healCoolDown,  4);
    }

    private void setupCureSkill() {
        cureSkill =
            new Skill(
                new CureSkill(), cureCoolDown,  3);
    }

    private void setupSpeedSkill() {
        speedSkill =
            new Skill(
                new SpeedSkill(), speedCoolDown, 2);
    }
    private void setupHitboxComponent() {
        new HitboxComponent(
            this,
            (you, other, direction) -> System.out.println("heroCollisionEnter"),
            (you, other, direction) -> System.out.println("heroCollisionLeave"));
    }

    public void resetSpeed() {
        Optional<Component> ve = this.getComponent(VelocityComponent.class);
        if (ve.isPresent()) {
            VelocityComponent v = (VelocityComponent) ve.get();
            v.setYVelocity(0.1f);
            v.setXVelocity(0.1f);
        }
    }

    public void startTrapTimer(int time) {
        trapTimer = new TrapTimer(time);

        Thread thread = new Thread(trapTimer);
        thread.start();
    }

    /**
     * Getter & Setter
     ********************/

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public TrapTimer getTrapTimer() {
        return trapTimer;
    }

    public void setTrapTimer(TrapTimer trapTimer) {
        this.trapTimer = trapTimer;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(float currentMana) {
        this.currentMana = currentMana;
    }

    @Override
    public void onLevelUp(long nexLevel) {

    }

    public long getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(long currentLevel) {
        this.currentLevel = currentLevel;
    }
}
