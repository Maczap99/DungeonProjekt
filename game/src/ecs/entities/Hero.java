package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import graphic.Animation;
import tools.TrapTimer;

/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity {

    private final int fireballCoolDown = 1;
    private float xSpeed = 0.2f;
    private float ySpeed = 0.2f;
    private int mana = 1000;
    private int health = 10;
    private int currentHealth = 10;
    private TrapTimer trapTimer;
    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private Skill firstSkill;
    private Skill healSkill;


    /**
     * Entity with Components
     */
    public Hero() {
        super();
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        PlayableComponent pc = new PlayableComponent(this);
        setupFireballSkill();
        pc.setSkillSlot1(firstSkill);

        HealthComponent hc = new HealthComponent(this);
        hc.setMaximalHealthpoints(health);
        hc.setCurrentHealthpoints(currentHealth);
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

    private void setupFireballSkill() {
        firstSkill =
            new Skill(
                new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
    }

    /**
     * private void setupHealSkill(){
     * healSkill =
     * new Skill(
     * new HealSkill();
     * )
     * }
     **/

    private void setupHitboxComponent() {
        new HitboxComponent(
            this,
            (you, other, direction) -> System.out.println("heroCollisionEnter"),
            (you, other, direction) -> System.out.println("heroCollisionLeave"));
    }

    public void resetSpeed() {
        setySpeed(0.2f);
        setxSpeed(0.2f);
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
}
