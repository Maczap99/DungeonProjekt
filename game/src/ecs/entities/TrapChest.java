package ecs.entities;

import ecs.components.*;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.List;
import java.util.Optional;


public class TrapChest extends Trap {

    //Wie viel Schaden die Truhe beim öffnen macht
    private int damage = 5;

    //Die Standardreichweite, von der aus mit der Chest interagieren kann
    public static final float defaultInteractionRadius = 1f;


    //Benutzt die Standard Chest sprites (sind die Sprites für die TrapChest schon im Projekt oder bin ich blind?
    public static final List<String> DEFAULT_CLOSED_ANIMATION_FRAMES =
        List.of("objects/trapchest/trap_chest_close_anim_f0.png");
    public static final List<String> DEFAULT_OPENING_ANIMATION_FRAMES =
        List.of(
            "objects/trapchest/trap_chest_close_anim_f0.png",
            "objects/trapchest/trap_chest_open_anim_f0.png",
            "objects/trapchest/trap_chest_open_anim_f1.png",
            "objects/trapchest/trap_chest_open_anim_f2.png",
            "objects/trapchest/trap_chest_open_anim_f1.png");
    AnimationComponent ac;

    //Erstellt eine TrapChest an position
    public TrapChest(Point position){
        new PositionComponent(this, position);
        new InteractionComponent(this, defaultInteractionRadius, false, this::onInteraction);
        ac =
            new AnimationComponent(
                this,
                new Animation(DEFAULT_CLOSED_ANIMATION_FRAMES, 100, false),

                new Animation(DEFAULT_OPENING_ANIMATION_FRAMES, 20, false));

    }


    @Override
    public void onInteraction(Entity entity) {
        ac.setCurrentAnimation(ac.getIdleRight());
        Optional<Entity> h = Game.getHero();

        //Holt den Spieler um dann seine HealthComponent zu holen um ihm Schaden hinzuzufügen.
        Entity hero;
        if(h.isPresent()){
            hero = h.get();
            Optional<Component> he = hero.getComponent(HealthComponent.class);
            if(he.isPresent()){
                HealthComponent hc = (HealthComponent) he.get();
                hc.receiveHit(new Damage(damage, DamageType.PHYSICAL, this));
            }
            else{
                throw new MissingComponentException("Player has no HealthComponent!");
            }
            System.out.println(hero.id + " bekommt " + damage + " Schaden!");
        }
    }
}
