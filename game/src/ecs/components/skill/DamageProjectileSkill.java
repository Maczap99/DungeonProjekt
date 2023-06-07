package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import ecs.entities.Hero;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/***
 * This Class handle the Projectile for Skills and Weapons
 *
 */

public abstract class DamageProjectileSkill implements ISkillFunction, Serializable {

    private String skillName;
    private String pathToTexturesOfProjectile;
    private float projectileSpeed;
    private float projectileRange;
    private Damage projectileDamage;
    private Point projectileHitboxSize;
    private ITargetSelection selectionFunction;
    private float manaCost;
    private transient Sound sound;
    private transient Logger   fireballSkillLogger = Logger.getLogger(this.getClass().getName());
    private transient Logger bowWeaponLogger = Logger.getLogger(this.getClass().getName());
    private transient Logger soundLogger = Logger.getLogger(this.getClass().getName());
    private boolean isCollide = false;

    /**
     * @param skillName
     * @param pathToTexturesOfProjectile
     * @param projectileSpeed
     * @param projectileDamage
     * @param projectileHitboxSize
     * @param selectionFunction
     * @param projectileRange
     * @param manaCost
     */
    public DamageProjectileSkill(
        String skillName,
        String pathToTexturesOfProjectile,
        float projectileSpeed,
        Damage projectileDamage,
        Point projectileHitboxSize,
        ITargetSelection selectionFunction,
        float projectileRange,
        float manaCost) {
        this.skillName = skillName;
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.manaCost = manaCost;
    }

    /***
     *
     * @param entity which uses the skill
     */
    @Override
    public void execute(Entity entity) {

        if (skillName.equals("fireball")) {
            fireball(entity);
        } else if (skillName.equals("boomerang")) {
            boomerang(entity);
        } else if (skillName.equals("bow")) {
            bow(entity);
        }else if(skillName.equals("sword")){
            sword(entity);
        }

    }

    /**
     * @param entity This Method handles the fireball entity
     */
    private void fireball(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        if (manaCost <= hero.getCurrentMana()) {

            Entity projectile = new Entity();
            PositionComponent epc =
                (PositionComponent)
                    entity.getComponent(PositionComponent.class)
                        .orElseThrow(
                            () -> new MissingComponentException("PositionComponent"));
            new PositionComponent(projectile, epc.getPosition());


            Point aimedOn = selectionFunction.selectTargetPoint();
            Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                    epc.getPosition(), aimedOn, projectileRange);

            Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + animationFix(targetPoint, entity));
            new AnimationComponent(projectile, animation);

            Point velocity =
                SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
            VelocityComponent vc =
                new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
            new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
            ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                            .ifPresent(
                                hc -> {
                                    ((HealthComponent) hc).receiveHit(projectileDamage);
                                    Game.removeEntity(projectile);
                                });
                        b.getComponent(PositionComponent.class)
                            .ifPresent(
                                bpc -> {
                                    PositionComponent entityComp = (PositionComponent) bpc;
                                    knockback((PositionComponent) projectile.getComponent(PositionComponent.class).get(), entityComp, 1.5f);
                                });
                    }
                };

            new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);

            // reduce mana
            hero.setCurrentMana(hero.getCurrentMana() - manaCost);
            fireballSkillLogger.info("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());

            try {
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/fireball1.mp3"));
                sound.play(0.5f);

            } catch (Exception e) {
                soundLogger.info("Sounddatei 'Fireball1.mp3' konnte nicht gefunden werden");
            }
        } else {
            fireballSkillLogger.info("Nicht genug Mana!");
            fireballSkillLogger.info("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());

        }
    }

    /**
     * This Method handle the Boomerang entity and his behaviour
     *
     * @param entity
     */
    private void boomerang(Entity entity) {
        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
            SkillTools.calculateLastPositionInRange(
                epc.getPosition(), aimedOn, projectileRange);

        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        Point velocity =
            SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        VelocityComponent vc =
            new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
        ICollide collide =
            (a, b, from) -> {
                if (b != entity) {
                    b.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> {
                                ((HealthComponent) hc).receiveHit(projectileDamage);
                                Game.removeEntity(projectile);
                                isCollide = true;
                            });
                    b.getComponent(PositionComponent.class)
                        .ifPresent(
                            bpc -> {
                                PositionComponent entityComp = (PositionComponent) bpc;
                                knockback((PositionComponent) projectile.getComponent(PositionComponent.class).get(), entityComp, 1.5f);
                            });
                }
            };

        new HitboxComponent(
            projectile, new Point(0f, 0f), projectileHitboxSize, collide, null);

        checkThrowBack(entity, epc.getPosition(), targetPoint);
    }

    /**
     * This Method handles the boomerang throw back
     *
     * @param entity
     * @param targetPoint
     * @param position
     */
    private void throwBack(Entity entity, Point targetPoint, Point position) {
        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));


        new PositionComponent(projectile, position);

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        Point velocity =
            SkillTools.calculateVelocity(position, targetPoint, projectileSpeed);
        VelocityComponent vc =
            new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, position, targetPoint);
        ICollide collide =
            (a, b, from) -> {
                if (b != entity) {
                    b.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> {
                                ((HealthComponent) hc).receiveHit(projectileDamage);
                                Game.removeEntity(projectile);
                            });

                }
            };

        new HitboxComponent(
            projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);

        isCollide = false;
    }

    /**
     * This Method handle the bow entity and the Deviation behaviors for the arrows
     *
     * @param entity
     */
    private void bow(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        float xC = 0;

        if (hero.getCurrentAmmo() > 0) {

            Entity projectile = new Entity();
            PositionComponent epc =
                (PositionComponent)
                    entity.getComponent(PositionComponent.class)
                        .orElseThrow(
                            () -> new MissingComponentException("PositionComponent"));

            new PositionComponent(projectile, epc.getPosition());

            Point aimedOn = selectionFunction.selectTargetPoint();
            Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                    epc.getPosition(), aimedOn, projectileRange);

            Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + animationFix(targetPoint, entity));
            new AnimationComponent(projectile, animation);

            xC = 2f - getDeviationNumber();

            if (xC > 0) {
                targetPoint.x = targetPoint.x - (2f - xC);
            } else {
                xC = xC * -1;
                targetPoint.x = targetPoint.x + (2f - xC);
            }

            targetPoint.y = targetPoint.y - (2f - getDeviationNumber());


            Point velocity =
                SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
            VelocityComponent vc =
                new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
            new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
            ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                            .ifPresent(
                                hc -> {
                                    ((HealthComponent) hc).receiveHit(projectileDamage);
                                    Game.removeEntity(projectile);
                                });
                        b.getComponent(PositionComponent.class)
                            .ifPresent(
                                bpc -> {
                                    PositionComponent entityComp = (PositionComponent) bpc;
                                    knockback((PositionComponent) projectile.getComponent(PositionComponent.class).get(), entityComp, 1.5f);
                                });
                    }
                };

            new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);


            hero.setCurrentAmmo(hero.getCurrentAmmo() - 1);

            bowWeaponLogger.info(hero.getCurrentAmmo() +"/"+hero.getAmmo()+ " Pfeile uebrig");

            try {
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/bow.mp3"));
                sound.play(0.7f);

            } catch (Exception e) {
                soundLogger.info("Sounddatei 'bow.mp3' konnte nicht gefunden werden");
            }
        } else {
            bowWeaponLogger.info("Keine Pfeile mehr!");
        }
    }

    /**
     * This Method handles the sword entity and it's behaviour
     * @param entity
     */
    private void sword(Entity entity){
        int dir = SkillTools.getCursorPositionAsRelative4WayDirection();
        Point pointDir;
        Logger.getAnonymousLogger().log(new LogRecord(Level.INFO,"Sword struck towards Direction " + dir));
        String pathSuffix = "";

        if(dir == 0){
            pointDir = new Point(0,1);
            pathSuffix += "up";
        } else if(dir == 1){
            pointDir = new Point(1,0);
            pathSuffix += "right";
        } else if(dir == 2){
            pointDir = new Point(0,-1);
            pathSuffix += "down";
        } else{
            pointDir = new Point(-1,0);
            pathSuffix += "left";
        }

        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());
        Point targetPoint = new Point(epc.getPosition().x + pointDir.x, epc.getPosition().y + pointDir.y);

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + pathSuffix);
        new AnimationComponent(projectile, animation);


        Point velocity =
            SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        VelocityComponent vc =
            new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);

        new ProjectileComponent(projectile, epc.getPosition(), targetPoint);

        ICollide collide =
            (a, b, from) -> {
                if (b != entity) {
                    b.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> {
                                ((HealthComponent) hc).receiveHit(projectileDamage);
                                Game.removeEntity(projectile);
                            });
                }
            };

        new HitboxComponent(
            projectile, new Point(0f, 0f), projectileHitboxSize, collide, null);
    }

    /**
     * @param targetDirection
     * @param entity
     * @return This Method set the direction for the skill assets folder
     */
    protected String animationFix(Point targetDirection, Entity entity) {
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));

        float x = epc.getPosition().x - targetDirection.x;
        float y = epc.getPosition().y - targetDirection.y;
        if (x > 0 && y < 0) {
            if (Math.abs(x) > Math.abs(y)) {
                return "Left/";
            } else {
                return "Up/";
            }

        } else if (x > 0 && y > 0) {
            if (Math.abs(x) > Math.abs(y)) {
                return "Left/";
            } else {
                return "Down/";
            }
        } else if (x < 0 && y < 0) {
            if (Math.abs(x) > Math.abs(y)) {
                return "Right/";
            } else {
                return "Up/";
            }

        } else if (x < 0 && y > 0) {
            if (Math.abs(x) > Math.abs(y)) {
                return "Right/";
            } else {
                return "Down/";
            }
        }
        return pathToTexturesOfProjectile;
    }

    /**
     * This Method check if the entity can get knock-back and set is
     * @param projectileComp
     * @param entityComp
     */
    protected void knockback(PositionComponent projectileComp, PositionComponent entityComp, float strength) {
        Point dir = Point.getUnitDirectionalVector( entityComp.getPosition(), projectileComp.getPosition());

        dir.x = dir.x * strength;
        dir.y = dir.y * strength;

        Point newPoint = new Point(dir.x + entityComp.getPosition().x, dir.y + entityComp.getPosition().y);

        if(Game.currentLevel.getTileAt(newPoint.toCoordinate()) != null){
            boolean tileCheck = Game.currentLevel.getTileAt(newPoint.toCoordinate()).isAccessible();

            if(tileCheck){
                entityComp.setPosition(newPoint);
            }
        }
    }

    /**
     * Check if the boomerang need to throw back
     * @param entity
     * @param p1
     * @param p2
     */
    private void checkThrowBack(Entity entity, Point p1, Point p2){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // Wartet eine Sekunde bevor der Bumerang zurückkommt
        ScheduledFuture<?> future = executor.schedule(new Runnable() {
            public void run() {
                if (!isCollide) {
                    // Code to execute after 1 second
                    throwBack(entity, p1, p2);
                }
            }
        }, 1, TimeUnit.SECONDS);

        isCollide = false;
        executor.shutdown();
    }

    /**
     * @return a float number 0 - 3
     */
    private float getDeviationNumber() {
        float temp = (float) (Math.random() * 2) + 1f;

        return temp;
    }
}
