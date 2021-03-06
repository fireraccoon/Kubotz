package com.brm.Kubotz.Features.DashBoots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.EventManager.EntityEvent;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.ECS.core.EntitySystem;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.GoatEngine.Utils.Math.Vectors;
import com.brm.Kubotz.Common.Events.CollisionEvent;
import com.brm.Kubotz.Input.GameButton;
import com.brm.Kubotz.Common.Systems.MovementSystems.MovementSystem;

/**
 * Handles Dash Movements
 */
public class DashSystem extends EntitySystem{

    public DashSystem(){}

    @Override
    public void init(){

    }



    public void handleInput(){
        for(Entity entity : getEntityManager().getEntitiesWithComponent(DashComponent.ID)){
            if(entity.hasComponentEnabled(VirtualGamePad.ID)){
                this.handleInputForEntity(entity);
            }

        }
    }


    /**
     * Handles the Input for an entity
     * having Input i.e. with a VirtualGamePad
     */
    private void handleInputForEntity(Entity entity){
        DashComponent dashComp = (DashComponent) entity.getComponent(DashComponent.ID);
        VirtualGamePad gamePad = (VirtualGamePad) entity.getComponent(VirtualGamePad.ID);


        if(dashComp.getPhase() == DashComponent.Phase.NONE){

            boolean isDashValid = true;

            //Find dash direction
            if(gamePad.isButtonPressed(GameButton.DPAD_RIGHT)){
                dashComp.getDirection().x = DashComponent.RIGHT;
            }else if(gamePad.isButtonPressed(GameButton.DPAD_LEFT)){
                dashComp.getDirection().x = DashComponent.LEFT;
            }else if(gamePad.isButtonPressed(GameButton.DPAD_UP)){
                dashComp.getDirection().y = DashComponent.UP;
            }else if(gamePad.isButtonPressed(GameButton.DPAD_DOWN)){
                dashComp.getDirection().y = DashComponent.DOWN;
            }else{
                isDashValid = false;
            }

            if(isDashValid){
                PhysicsComponent phys = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);
                // Put the entity in preparation Phase
                dashComp.setPhase(DashComponent.Phase.PREPARATION);
                dashComp.getPreparationDuration().reset();
                dashComp.setStartPosition(phys.getPosition().cpy());
            }
        }
    }



    /**
     * Checks whether or not an entity is still allowed to dash
     * Basically tries to disable it under the right conditions
     * Checks all entities with DashComponent
     */
    @Override
    public void update(float dt){
        for(Entity entity : this.getEntityManager().getEntitiesWithComponent(DashComponent.ID)) {
            DashComponent dashComp = (DashComponent) entity.getComponent(DashComponent.ID);

            if (dashComp.isEnabled()) {
                //Are We Done with the preparing phase?
                if(dashComp.getPhase() == DashComponent.Phase.PREPARATION){
                   updatePreparationPhase(entity);
                } else if(dashComp.getPhase() == DashComponent.Phase.TRAVEL){
                    updateTravelPhase(entity);
                }else if(dashComp.getPhase() == DashComponent.Phase.DECELERATION){
                    updateDecelerationPhase(entity);
                }else if(dashComp.getPhase() == DashComponent.Phase.DONE){
                    dashComp.setPhase(DashComponent.Phase.NONE); //Reset
                    fireEvent(new DashPhaseChangeEvent(entity.getID(), DashComponent.Phase.NONE));
                }

            }
        }

    }


    /**
     * Updates an entity when it is in PREPARATION phase
     * @param entity the entity to update
     */
    private void updatePreparationPhase(Entity entity){
        DashComponent dashComp = (DashComponent) entity.getComponent(DashComponent.ID);

        if(dashComp.getPreparationDuration().isDone()){
            //We can now proceed to dashing in whatever direction
            dashComp.setPhase(DashComponent.Phase.TRAVEL);
            dashComp.getTravelDuration().reset();
            PhysicsComponent phys = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);
            phys.getBody().setGravityScale(0);

            fireEvent(new DashPhaseChangeEvent(entity.getID(), DashComponent.Phase.TRAVEL));
        }else{
            MovementSystem.stopXY(entity);
        }

    }

    /**
     * Updates an entity when it is in TRAVEL phase
     * @param entity the entity to update
     */
    private void updateTravelPhase(Entity entity){
        PhysicsComponent phys = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);
        DashComponent dashComp = (DashComponent) entity.getComponent(DashComponent.ID);

        // Do we need to disable?
        if (Vectors.euclideanDistance(phys.getPosition(), dashComp.getStartPosition()) >= dashComp.getDistance().x || dashComp.getTravelDuration().isDone()) {
            dashComp.setPhase(DashComponent.Phase.DECELERATION);
            fireEvent(new DashPhaseChangeEvent(entity.getID(), DashComponent.Phase.DECELERATION));
        }else{
            Vector2 velocity = new Vector2();
            velocity.x = dashComp.getDirection().x * (phys.getVelocity().x + dashComp.getSpeed().x) * Gdx.graphics.getDeltaTime();
            velocity.y = dashComp.getDirection().y * (phys.getVelocity().y + dashComp.getSpeed().y) * Gdx.graphics.getDeltaTime();
            MovementSystem.moveInX(entity, velocity.x);
            MovementSystem.moveInY(entity, velocity.y);

        }
    }


    /**
     * Updates an entity when it is in DECELERATION phase
     * @param entity the entity to update
     */
    private void updateDecelerationPhase(Entity entity){
        PhysicsComponent phys = (PhysicsComponent)entity.getComponent(PhysicsComponent.ID);
        DashComponent dashComp = (DashComponent) entity.getComponent(DashComponent.ID);

        Vector2 decelVel = new Vector2();


        //DECELERATION IN X //TODO Tweak for a smoother transition
        if(dashComp.getDirection().x != 0){
            decelVel.x = (phys.getVelocity().x > 0) ?
                    Math.max(phys.getVelocity().x - dashComp.getSpeed().x, 0):
                    Math.min(phys.getVelocity().x + dashComp.getSpeed().x, 0);
            MovementSystem.moveInX(entity, decelVel.x * Gdx.graphics.getDeltaTime());
        }


        if (dashComp.getDirection().y != 0) {
            decelVel.y = (phys.getVelocity().y > 0) ?
                    Math.max(phys.getVelocity().y - dashComp.getSpeed().y, 0):
                    Math.min(phys.getVelocity().y + dashComp.getSpeed().y, 0);
            MovementSystem.moveInY(entity, decelVel.y * Gdx.graphics.getDeltaTime());
        }

        // Is the entity done decelerating
        if(phys.getVelocity().x == 0){
            dashComp.getDirection().set(0, 0);
            phys.getBody().setGravityScale(1);
            dashComp.setPhase(DashComponent.Phase.DONE);
            fireEvent(new DashPhaseChangeEvent(entity.getID(), DashComponent.Phase.DONE));
        }

    }


    @Override
    public <T extends EntityEvent> void onEntityEvent(T event) {
        if(event.isOfType(CollisionEvent.class)){
            this.onCollision((CollisionEvent) event);
        }
    }




    public void onCollision(CollisionEvent e){
        Entity entityA = getEntityManager().getEntity(e.getEntityA());
        if(entityA.hasComponentEnabled(DashComponent.ID)){
            DashComponent dash = (DashComponent) entityA.getComponent(DashComponent.ID);
            if(dash.getPhase() == DashComponent.Phase.TRAVEL){
                dash.setPhase(DashComponent.Phase.DECELERATION);

            }
        }
    }

}
