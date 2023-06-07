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

    /**
     * @param entity
     * This Method handles the fireball entity
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

    /**
     * This Method handle the Boomerang entity and his behaviour
     * @param entity
     */
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

    /**
     * This Method handle the bow entity and the Deviation behaviors for the arrows
     * @param entity
     */
    private void bow(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        float xC = 0;
        float yC = 0;

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

            Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile + animationFix(targetPoint, entity));
            new AnimationComponent(projectile, animation);

            xC = 2f - getDeviationNumber();
            yC = 2f - getDeviationNumber();

            if(xC > 0){
                targetPoint.x = targetPoint.x - (2f- xC);
                System.out.println("-"+(2f- xC));
            }else{
                xC = xC * -1;
                targetPoint.x = targetPoint.x + ( 2f - xC);
                System.out.println("+"+  ( 2f - xC));
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
     * This Method set the direction for the skill assets folder
     */
    protected String animationFix(Point targetDirection, Entity entity) {
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));

        float x = epc.getPosition().x - targetDirection.x;
        float y = epc.getPosition().y - targetDirection.y;
        if (x > 0 && y < 0) { // rechts oberhalb
            if (Math.abs(y) > Math.abs(y)) { // weiter rechts als oberhalb
                return "Left/";
            } else {
                return "Up/";
            }

        } else if (x > 0 && y > 0) { // rechts unterhalb
            if (Math.abs(x) > Math.abs(y)) {
                return "Left/"; // weiter rechts als unterhalb
            } else {
                return "Down/";
            }
        } else if (x < 0 && y < 0) { // links oberhalb
            if (Math.abs(x) > Math.abs(y)) {
                return "Right/"; // weiter links als oberhalb
            } else {
                return "Up/";
            }

        } else if (x < 0 && y > 0) { // links unterhalb
            if (Math.abs(x) > Math.abs(y)) {
                return "Right/"; // weiter links als unterhalb
            } else {
                return "Down/";
            }
        }
        return pathToTexturesOfProjectile;
    }

    private float getDeviationNumber(){
        float temp = (float) (Math.random() * 2) +1f;

        return temp;
    }
}
