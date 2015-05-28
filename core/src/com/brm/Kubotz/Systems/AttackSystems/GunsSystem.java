package com.brm.Kubotz.Systems.AttackSystems;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.ECS.Components.EntityComponent;
import com.brm.GoatEngine.ECS.Components.PhysicsComponent;
import com.brm.GoatEngine.ECS.Entity.Entity;
import com.brm.GoatEngine.ECS.Entity.EntityManager;
import com.brm.GoatEngine.ECS.Systems.EntitySystem;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.GoatEngine.Utils.Logger;
import com.brm.GoatEngine.Utils.Timer;
import com.brm.Kubotz.Components.ParticleEffectComponent;
import com.brm.Kubotz.Components.Parts.Weapons.GunComponent;
import com.brm.Kubotz.Components.LifespanComponent;
import com.brm.Kubotz.Constants;
import com.brm.Kubotz.Entities.BulletFactory;
import com.brm.Kubotz.Input.GameButton;
import com.brm.Kubotz.Systems.MovementSystems.MovementSystem;

/**
 * Used to update entities using a gun
 */
public class GunsSystem extends EntitySystem {


    public GunsSystem(EntityManager em) {
        super(em);
    }

    @Override
    public void init() {}


    @Override
    public void handleInput() {

        for(Entity entity: em.getEntitiesWithComponent(GunComponent.ID)){
            if(entity.hasComponentEnabled(VirtualGamePad.ID)){
                handleInputForEntity(entity);
            }

        }

    }



    private void handleInputForEntity(Entity entity){
            VirtualGamePad gamePad = (VirtualGamePad) entity.getComponent(VirtualGamePad.ID);
            GunComponent gunComponent = (GunComponent) entity.getComponent(GunComponent.getId());
            PhysicsComponent physComp = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);
            gunComponent.setShooting(false);
            if(gamePad.isButtonPressed(GameButton.PUNCH_BUTTON)){
                gamePad.releaseButton(GameButton.PUNCH_BUTTON);
                //can we shoot?
                if(gunComponent.getCooldown().isDone()){
                    gunComponent.getCooldown().reset();
                    gunComponent.setShooting(true);


                    //CREATE A BULLET
                    Entity bullet = this.createBullet(physComp, gunComponent);
                    ((LifespanComponent)bullet.getComponent(LifespanComponent.ID)).startLife();
                    //Move the bullet
                    int direction = (physComp.getDirection() == PhysicsComponent.Direction.RIGHT) ? 1 : -1;
                    MovementSystem.moveInX(bullet, gunComponent.getBulletSpeed().x  * direction);
                    MovementSystem.moveInY(bullet, gunComponent.getBulletSpeed().y);

                }
            }
    }



    @Override
    public void update(float dt) {


        //Delete bullet that collided
        for(Entity entity: em.getEntitiesWithTag(Constants.ENTITY_TAG_BULLET)){

            PhysicsComponent phys = (PhysicsComponent) entity.getComponent(PhysicsComponent.ID);
            if(!phys.getContacts().isEmpty()){
                phys.getBody().setActive(false);
            }
        }

    }



    /**
     * Creates a Gun Bullet
     * @param phys the PhysicsComponent of the puncher
     * @return the new Bullet
     */
    private Entity createBullet(PhysicsComponent phys, GunComponent gunComponent){

        // Put the punch at the right place according to the
        // direction the puncher is facing
        Vector2 position = null;
        switch (phys.getDirection()) {
            case RIGHT:
                position = new Vector2(phys.getWidth() + phys.getWidth()/2, 0);
                break;
            case LEFT:
                position = new Vector2(phys.getWidth() - phys.getWidth() - 2, 0); //TODO Clean this up! This is messy!
                break;
        }

        position.add(phys.getPosition());
        return new BulletFactory(this.em, phys.getBody().getWorld(), position)
                .withDamage(gunComponent.getDamage())
                .withSize(1, phys.getWidth() * 0.5f)
                .withKnockBack(gunComponent.getKnockBack())
                .withLifespan(Timer.FIVE_SECONDS)
                .withTag(Constants.ENTITY_TAG_BULLET)
                .withDirection(phys.getDirection())
                .withSpeed(gunComponent.getBulletSpeed())
                .build();

    }













}