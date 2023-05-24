package ecs.systems;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.NoWalk;
import ecs.components.ai.idle.PatrouilleWalk;
import ecs.components.ai.idle.RadiusWalk;
import ecs.components.ai.idle.StaticRadiusWalk;
import ecs.components.ai.transition.RangeTransition;
import ecs.components.ai.transition.SelfDefendTransition;
import ecs.components.xp.XPComponent;
import ecs.entities.Monster;
import graphic.Animation;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class MonsterBuilder {


    /**
     * Structure for an Animation filepath Array
     * 1: idleLeft
     * 2: idleRight
     * 3: runLeft
     * 4: runRight
     */
    private String[] impPaths = new String[]{
        "monster/imp/idleLeft",
        "monster/imp/idleRight",
        "monster/imp/runLeft",
        "monster/imp/runRight"
    };
    private String[] chortPaths = new String[]{
        "monster/chort/idleLeft",
        "monster/chort/idleRight",
        "monster/chort/runLeft",
        "monster/chort/runRight"
    };
    private String[] goblinPaths = new String[]{
        "monster/goblin/idleLeft",
        "monster/goblin/idleRight",
        "monster/goblin/runLeft",
        "monster/goblin/runRight"
    };


    public MonsterBuilder(){

    }


    public Monster createRandomMonster(){
        Monster m = new Monster();
        int type = ThreadLocalRandom.current().nextInt(0,3);
        if(type == 0)
            m = setupImp(m);
        else if(type == 1)
            m = setupChort(m);
        else
            m = setupBlueChort(m);

        return m;
    }
    public Monster createMonsterOfType(int type){
        Monster m = new Monster();

        if(type == 0)
            m = setupImp(m);
        else if(type == 1)
            m = setupChort(m);
        else
            m = setupBlueChort(m);

        return m;
    }

    private Monster setupImp(Monster base){
        float xSpeed = 0.15f;
        float ySpeed = 0.15f;
        int maxHealth = 1;

        Animation moveLeft = AnimationBuilder.buildAnimation(impPaths[0]);
        Animation moveRight = AnimationBuilder.buildAnimation(impPaths[1]);
        new VelocityComponent(base, xSpeed, ySpeed, moveLeft, moveRight);

        Animation idleLeft = AnimationBuilder.buildAnimation(impPaths[2]);
        Animation idleRight = AnimationBuilder.buildAnimation(impPaths[3]);
        new AnimationComponent(base, idleLeft, idleRight);

        Optional<Component> optHC = base.getComponent(HealthComponent.class);
        if(optHC.isPresent()){
            HealthComponent hc = (HealthComponent) optHC.get();
            hc.setMaximalHealthpoints(maxHealth);
        }
        else {
            throw new MissingComponentException("Monster has no HealthComponent!");
        }

        Optional<Component> optXP = base.getComponent(XPComponent.class);
        if(optXP.isPresent()){
            XPComponent xc = (XPComponent) optXP.get();
            xc.setLootXP(10);
        }
        else{
            throw new MissingComponentException("This Monster has no XPComponent");
        }

        base = applyRandomAI(base);

        return base;
    }
    private Monster setupChort(Monster base){
        float xSpeed = 0.1f;
        float ySpeed = 0.1f;
        int maxHealth = 1;

        Animation moveLeft = AnimationBuilder.buildAnimation(chortPaths[0]);
        Animation moveRight = AnimationBuilder.buildAnimation(chortPaths[1]);
        new VelocityComponent(base, xSpeed, ySpeed, moveLeft, moveRight);

        Animation idleLeft = AnimationBuilder.buildAnimation(chortPaths[2]);
        Animation idleRight = AnimationBuilder.buildAnimation(chortPaths[3]);
        new AnimationComponent(base, idleLeft, idleRight);

        Optional<Component> optHC = base.getComponent(HealthComponent.class);
        if(optHC.isPresent()){
            HealthComponent hc = (HealthComponent) optHC.get();
            hc.setMaximalHealthpoints(maxHealth);
        }
        else {
            throw new MissingComponentException("Monster has no HealthComponent!");
        }

        Optional<Component> optXP = base.getComponent(XPComponent.class);
        if(optXP.isPresent()){
            XPComponent xc = (XPComponent) optXP.get();
            xc.setLootXP(10);
        }
        else{
            throw new MissingComponentException("This Monster has no XPComponent");
        }

        base = applyRandomAI(base);

        return base;
    }
    private Monster setupBlueChort(Monster base) {
        float xSpeed = 0.4f;
        float ySpeed = 0.4f;
        int maxHealth = 1;

        Animation moveLeft = AnimationBuilder.buildAnimation(goblinPaths[0]);
        Animation moveRight = AnimationBuilder.buildAnimation(goblinPaths[1]);
        new VelocityComponent(base, xSpeed, ySpeed, moveLeft, moveRight);

        Animation idleLeft = AnimationBuilder.buildAnimation(goblinPaths[2]);
        Animation idleRight = AnimationBuilder.buildAnimation(goblinPaths[3]);
        new AnimationComponent(base, idleLeft, idleRight);

        Optional<Component> optHC = base.getComponent(HealthComponent.class);
        if(optHC.isPresent()){
            HealthComponent hc = (HealthComponent) optHC.get();
            hc.setMaximalHealthpoints(maxHealth);
        }
        else {
            throw new MissingComponentException("Monster has no HealthComponent!");
        }

        Optional<Component> optXP = base.getComponent(XPComponent.class);
        if(optXP.isPresent()){
            XPComponent xc = (XPComponent) optXP.get();
            xc.setLootXP(10);
        }
        else{
            throw new MissingComponentException("This Monster has no XPComponent");
        }

        base = applyRandomAI(base);

        return base;
    }

    private Monster applyRandomAI(Monster base){
        Optional<Component> optAI = base.getComponent(AIComponent.class);
        AIComponent aiComponent;
        if(optAI.isPresent()){
            aiComponent = (AIComponent) optAI.get();
        }
        else {
            throw new MissingComponentException("This Monster has no AIComponent!");
        }

        int rand;

        rand = ThreadLocalRandom.current().nextInt(0,2);
        if(rand == 0)
            aiComponent.setFightAI(new CollideAI(2f));
        else
            aiComponent.setFightAI(new CollideAI(2f));

        rand = ThreadLocalRandom.current().nextInt(0,4);
        if(rand == 0)
            aiComponent.setIdleAI(new PatrouilleWalk(5f,3,1000, PatrouilleWalk.MODE.BACK_AND_FORTH));
        else if(rand == 1)
            aiComponent.setIdleAI(new RadiusWalk(5f, 2));
        else if(rand == 2)
            aiComponent.setIdleAI(new StaticRadiusWalk(5f, 2));
        else
            aiComponent.setIdleAI(new NoWalk());

        rand = ThreadLocalRandom.current().nextInt(0,2);
        if(rand == 0)
            aiComponent.setTransitionAI(new SelfDefendTransition());
        else
            aiComponent.setTransitionAI(new RangeTransition(7f));

        return base;
    }
}
