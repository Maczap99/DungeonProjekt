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
import tools.Constants;
import tools.Point;

import java.io.Serializable;
import java.util.logging.Logger;

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
    private transient Logger fireballSkillLogger;
    private transient Logger boomerangWeaponLogger;
    private transient Logger bowWeaponLogger;
    private transient Logger soundLogger;

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
        }
    }

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

            Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + animationHelper(targetPoint, entity));
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
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);

            // reduce mana
            hero.setCurrentMana(hero.getCurrentMana() - manaCost);
            fireballSkillLogger = Logger.getLogger("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());

            try {
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/fireball1.mp3"));
                sound.play(0.5f);

            } catch (Exception e) {
                soundLogger = Logger.getLogger("Sounddatei 'Fireball1.mp3' konnte nicht gefunden werden");
            }
        } else {
            fireballSkillLogger = Logger.getLogger("Nicht genug Mana!");
            fireballSkillLogger = Logger.getLogger("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());

        }
    }

    private void boomerang(Entity entity) {
        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
            SkillTools.calculateLastPositionInRange(
                epc.getPosition(), aimedOn, projectileRange);
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
            projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    private void test(Entity entity) {
        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
            SkillTools.calculateLastPositionInRange(
                epc.getPosition(), aimedOn, projectileRange);
        Point velocity =
            SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        VelocityComponent vc =
            new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, targetPoint, epc.getPosition());
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
    }

    private void bow(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();

        if (hero.getAmmo() > 0) {

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

            Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + animationHelper(targetPoint, entity));
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
                projectile, new Point(0.1f, 0.1f), projectileHitboxSize, collide, null);


            hero.setAmmo(hero.getAmmo() - 1);

            try {
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/bow.mp3"));
                sound.play(0.7f);

            } catch (Exception e) {
                soundLogger = Logger.getLogger("Sounddatei 'bow.mp3' konnte nicht gefunden werden");
            }
        } else {
            bowWeaponLogger = Logger.getLogger("Keine Pfeile mehr!");
        }
    }

    /**
     * @param targetDirection
     * @param entity
     * @return
     */
    protected String animationHelper(Point targetDirection, Entity entity) {
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));

        float xwert = epc.getPosition().x - targetDirection.x;
        float ywert = epc.getPosition().y - targetDirection.y;
        if (xwert > 0 && ywert < 0) { // rechts oberhalb
            if (Math.abs(xwert) > Math.abs(ywert)) { // weiter rechts als oberhalb
                return "Left/";
            } else {
                return "Up/";
            }

        } else if (xwert > 0 && ywert > 0) { // rechts unterhalb
            if (Math.abs(xwert) > Math.abs(ywert)) {
                return "Left/"; // weiter rechts als unterhalb
            } else {
                return "Down/";
            }
        } else if (xwert < 0 && ywert < 0) { // links oberhalb
            if (Math.abs(xwert) > Math.abs(ywert)) {
                return "Right/"; // weiter links als oberhalb
            } else {
                return "Up/";
            }

        } else if (xwert < 0 && ywert > 0) { // links unterhalb
            if (Math.abs(xwert) > Math.abs(ywert)) {
                return "Right/"; // weiter links als unterhalb
            } else {
                return "Down/";
            }
        }
        return pathToTexturesOfProjectile;
    }
}
