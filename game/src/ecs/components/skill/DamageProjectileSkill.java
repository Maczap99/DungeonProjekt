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

public abstract class DamageProjectileSkill implements ISkillFunction, Serializable {

    private String pathToTexturesOfProjectile;
    private float projectileSpeed;

    private float projectileRange;
    private Damage projectileDamage;
    private Point projectileHitboxSize;

    private ITargetSelection selectionFunction;

    private float manaCost;

    private transient Sound sound;

    public DamageProjectileSkill(
        String pathToTexturesOfProjectile,
        float projectileSpeed,
        Damage projectileDamage,
        Point projectileHitboxSize,
        ITargetSelection selectionFunction,
        float projectileRange,
        float manaCost) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.manaCost = manaCost;
    }

    @Override
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();

        if (manaCost <= hero.getCurrentMana()) {

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

            // reduce mana
            hero.setCurrentMana(hero.getCurrentMana() - manaCost);
            System.out.println("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());

            try {
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/fireball1.mp3"));
                sound.play(0.5f);

            } catch (Exception e) {
                System.out.println("Sounddatei 'Fireball1.mp3' konnte nicht gefunden werden");
            }
        }else{
            System.out.println("Nicht genug Mana!");
            System.out.println("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());

        }
    }
}
