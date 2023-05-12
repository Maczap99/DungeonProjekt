package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.components.*;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.List;
import java.util.Optional;

/**
 * This ist a Class for the entity TrapChest
 * If the Player try to open the chest, he will get damage
 *
 */

public class TrapChest extends Entity implements IInteraction {

    //Wie viel Schaden die Truhe beim öffnen macht
    private static final int damage = 50;

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
    private transient AnimationComponent ac;
    private transient Sound sound;

    //Erstellt eine TrapChest an position
    public TrapChest(Point position){
        super();

        setup(position);
    }

    public void setup(Point position) {
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
        Hero held = (Hero) Game.getHero().get();

        if(h.isPresent()){
            hero = h.get();
            Optional<Component> he = hero.getComponent(HealthComponent.class);
            if(he.isPresent()){
                HealthComponent hc = (HealthComponent) he.get();
                hc.receiveHit(new Damage(damage, DamageType.PHYSICAL, this));

                // console output for current health
                int d = held.getCurrentHealth() - damage;
                if(d <= 0){
                    held.setCurrentHealth(0);
                }else{
                    held.setCurrentHealth(d);
                }
            }
            else{
                throw new MissingComponentException("Player has no HealthComponent!");
            }
            System.out.println("Held id "+ hero.id + " bekommt " + damage + " Schaden!");
            System.out.println("Leben: " + held.getCurrentHealth() + " von "+held.getHealth());

            try{
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/trap/trapChest1.mp3"));
                sound.play(0.5f);
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/effect/damageLong.mp3"));
                sound.play(0.5f);

            }catch (Exception e){
                System.out.println("Sounddatei konnte nicht gefunden werden");
            }
        }
    }
}
