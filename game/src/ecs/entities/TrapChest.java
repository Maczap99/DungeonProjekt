package ecs.entities;

import ecs.components.*;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import graphic.Animation;
import tools.Point;

import java.util.List;


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



    /*
     * Erstellt eine TrapChest an position
     */
    public TrapChest(Point position){
        new PositionComponent(this, position);
        new InteractionComponent(this, defaultInteractionRadius, false, this::onInteraction);
        ac =
            new AnimationComponent(
                this,
                new Animation(DEFAULT_CLOSED_ANIMATION_FRAMES, 100, false),
                new Animation(DEFAULT_OPENING_ANIMATION_FRAMES, 15, false));

        //Test wegen dem getComponent bei onInteraction
        HealthComponent hc = new HealthComponent(this);
        hc.setMaximalHealthpoints(200);
        hc.setCurrentHealthpoints(200);
    }


    //Aus der Chest Klasse übernommen
    private static MissingComponentException createMissingComponentException(
        String Component, Entity e) {
        return new MissingComponentException(
            Component
                + " missing in "
                + TrapChest.class.getName()
                + " in Entity "
                + e.getClass().getName());
    }

    @Override
    public void onInteraction(Entity entity) {
        System.out.println(entity.id + " bekommt Schaden!");
        ac.setCurrentAnimation(ac.getIdleRight());

        /*
         * Sucht nach der HealthComponent der TrapChest. Witzig.
         * TODO: An die HealthComponent des Spielers kommen.
        HealthComponent hc =
            entity.getComponent(HealthComponent.class)
                .map(HealthComponent.class::cast)
                .orElseThrow(
                    () ->
                        createMissingComponentException(
                            HealthComponent.class.getName(), entity));

        hc.receiveHit(new Damage(damage, DamageType.PHYSICAL, this));

         */
    }
}
