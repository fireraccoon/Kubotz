package com.brm.Kubotz.Systems.SkillsSystem;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.ECS.Components.EntityComponent;
import com.brm.GoatEngine.ECS.Components.PhysicsComponent;
import com.brm.GoatEngine.ECS.Entity.Entity;
import com.brm.GoatEngine.ECS.Entity.EntityManager;
import com.brm.GoatEngine.ECS.Systems.EntitySystem;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.GoatEngine.Utils.Logger;
import com.brm.Kubotz.Components.Parts.Weapons.DroneGauntletComponent;
import com.brm.Kubotz.Components.SensorComponent;
import com.brm.Kubotz.Entities.DroneFactory;
import com.brm.Kubotz.Input.GameButton;

import java.awt.*;

/**
 * Turret Gauntlet System
 */
public class DroneGauntletSystem extends EntitySystem {

    public DroneGauntletSystem(EntityManager em) {
        super(em);
    }

    @Override
    public void init() {}


    @Override
    public void handleInput() {
        for(Entity entity: em.getEntitiesWithComponent(DroneGauntletComponent.ID)){
            if(entity.hasComponentEnabled(VirtualGamePad.ID)){
                handleInputForEntity(entity);
            }

        }
    }

    private void handleInputForEntity(Entity entity){
        VirtualGamePad gamePad = (VirtualGamePad) entity.getComponent(VirtualGamePad.ID);
        DroneGauntletComponent gauntlet = (DroneGauntletComponent) entity.getComponent(DroneGauntletComponent.ID);
        PhysicsComponent physicsComponent = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);


        if(gamePad.isButtonPressed(GameButton.ACTIVE_SKILL_BUTTON)){
            gamePad.releaseButton(GameButton.ACTIVE_SKILL_BUTTON);
            //can we spawn a turret?
            if(gauntlet.getCooldown().isDone()){
                String droneId = this.spawnDrone(physicsComponent, entity).getID();
                gauntlet.addDrone(droneId);

                entity.addComponent(new SensorComponent(3), SensorComponent.ID);


                gauntlet.getCooldown().reset();
            }
        }
    }


    @Override
    public void update(float dt) {

        for(Entity entity: em.getEntitiesWithComponent(DroneGauntletComponent.ID)){
            DroneGauntletComponent droneComp = (DroneGauntletComponent) entity.getComponent(DroneGauntletComponent.ID);

            // Untrack Dead Drones
            for(String drone: droneComp.getDrones()){
                if(em.getEntity(drone) == null){
                    droneComp.removeDrone(drone);
                }
            }

            if(droneComp.getDrones().isEmpty()){
                //Remove tracker Comp
                entity.removeComponent(SensorComponent.ID);

            }

        }
    }








    private Entity spawnDrone(PhysicsComponent physicsComponent, Entity master){
        Logger.log("SPAWN");
        Entity turret = new DroneFactory(em,  physicsComponent.getBody().getWorld(), master)
                .withPosition(new Vector2(physicsComponent.getPosition().x - 1, physicsComponent.getPosition().y + 1))
                .withDistance(new Vector2(MathUtils.random(1,2),MathUtils.random(1,2)))
                .withSize(4, 4)
                .build();
        return turret;
    }













}