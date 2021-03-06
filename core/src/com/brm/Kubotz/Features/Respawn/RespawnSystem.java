package com.brm.Kubotz.Features.Respawn;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.ECS.core.CameraTargetComponent;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.ECS.common.HealthComponent;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.ECS.core.EntitySystem;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.GoatEngine.ECS.common.SpriterAnimationComponent;

import java.util.ArrayList;

/**
 * Used to make entities Respawn (More like players Respawn)
 */
public class RespawnSystem extends EntitySystem {
	
	public RespawnSystem(){}


    @Override
    public void init() {

    }

    @Override
    public void update(float dt){
        for(Entity entity: getEntityManager().getEntitiesWithComponent(RespawnComponent.ID)){
            RespawnComponent respawn = (RespawnComponent) entity.getComponent(RespawnComponent.ID);
            //STATE MACHINE

            switch (respawn.getState()) {
                case DEAD:
                    processDeadState(entity);
                    break;
                case WAITING:
                    processWaitingState(entity);
                    break;
                case SPAWNING:
                    processSpawning(entity);
                    break;
                case SPAWNED:
                    processSpawnedState(entity);
                    break;
            }
        }
        
    }

    /**
     * Processes an entity when it is in the Dead State
     * @param entity
     */
    private void processDeadState(Entity entity){
        RespawnComponent respawn = (RespawnComponent)entity.getComponent(RespawnComponent.ID);
        PhysicsComponent phys = (PhysicsComponent)entity.getComponent(PhysicsComponent.ID);

        respawn.getDelay().reset();

        // HIDE BODY //TODO disable Graphics for entity
        phys.getBody().setActive(false);

        if(entity.hasComponentEnabled(SpriterAnimationComponent.ID))
            entity.disableComponent(SpriterAnimationComponent.ID);

        if(entity.hasComponentEnabled(VirtualGamePad.ID))
            entity.disableComponent(VirtualGamePad.ID);

        if(entity.hasComponentEnabled(CameraTargetComponent.ID))
            entity.disableComponent(CameraTargetComponent.ID);

        respawn.setState(RespawnComponent.State.WAITING);
    }

    /**
     * Processes an entity when it is in the Spawned State
     * @param entity
     */
    private void processSpawnedState(Entity entity){
        RespawnComponent respawn = (RespawnComponent) entity.getComponent(RespawnComponent.ID);
        HealthComponent health = (HealthComponent) entity.getComponent(HealthComponent.ID);
        if(health.isDead()){
            respawn.setState(RespawnComponent.State.DEAD);
        }
    }

    /**
     * Processes an entity when it is in the spawning state
     * @param entity
     */
    private void processSpawning(Entity entity){
        RespawnComponent respawn = (RespawnComponent) entity.getComponent(RespawnComponent.ID);
        PhysicsComponent phys = (PhysicsComponent)entity.getComponent(PhysicsComponent.ID);
        HealthComponent health = (HealthComponent) entity.getComponent(HealthComponent.ID);


        Vector2 point = getRandomSpawnPoint();
        phys.setPosition(point.x, point.y);
        phys.getBody().setActive(true);


        if(entity.hasComponent(SpriterAnimationComponent.ID))
            entity.enableComponent(SpriterAnimationComponent.ID);

        if(entity.hasComponent(CameraTargetComponent.ID))
            entity.enableComponent(CameraTargetComponent.ID);

        if(entity.hasComponent(VirtualGamePad.ID))
            entity.enableComponent(VirtualGamePad.ID);


        health.setAmount(health.getMaxAmount());

        respawn.setState(RespawnComponent.State.SPAWNED);


    }

    /**
     * Processes an entity when it is in the waiting state (waiting to be respawned)
     * @param entity
     */
    private void processWaitingState(Entity entity){
        RespawnComponent respawn = (RespawnComponent) entity.getComponent(RespawnComponent.ID);
        if(respawn.getDelay().isDone()){
            respawn.setState(RespawnComponent.State.SPAWNING);
        }

    }

    /**
     * Returns a random Spawn point
     * @return
     */
    private Vector2 getRandomSpawnPoint(){

        //Get PowerUps Spawns
        ArrayList<EntityComponent> spawns = getEntityManager().getComponents(SpawnPointComponent.ID);
        for (int i = 0; i < spawns.size(); i++) {
            if (((SpawnPointComponent) spawns.get(i)).getType() != SpawnPointComponent.Type.Player) {
                spawns.remove(i);
            }
        }

        //Get a Random Spawn Point
        int index = MathUtils.random(spawns.size() - 1);
        Entity entity = getEntityManager().getEntitiesWithComponent(SpawnPointComponent.ID).get(index);
        SpawnPointComponent spawn = (SpawnPointComponent)entity.getComponent(SpawnPointComponent.ID);

        //Randomize position
        Vector2 pos = spawn.getPosition();
        pos.x = MathUtils.random(pos.x-0.1f, pos.x+0.1f);
        pos.y = MathUtils.random(pos.y-0.1f, pos.y+0.1f);


        return pos;
    }



}