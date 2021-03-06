package com.brm.Kubotz.Features.LaserGuns;



import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.EventManager.EntityEvent;
import com.brm.GoatEngine.ECS.core.EntitySystem;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.common.TagsComponent;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.GoatEngine.Utils.Timer;
import com.brm.Kubotz.Common.Components.LifespanComponent;
import com.brm.Kubotz.Common.Events.CollisionEvent;
import com.brm.GoatEngine.Physics.Hitbox.Hitbox;
import com.brm.Kubotz.Common.Systems.MovementSystems.MovementSystem;
import com.brm.Kubotz.Constants;
import com.brm.Kubotz.Features.LaserSword.BulletFactory;
import com.brm.Kubotz.Input.GameButton;

/**
 * Used to update entities using a gun
 */
public class GunsSystem extends EntitySystem {


    public GunsSystem(){}

    @Override
    public void init() {}


    @Override
    public void handleInput() {
        for(Entity entity: getEntityManager().getEntitiesWithComponent(GunComponent.ID)){
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
            if(gamePad.isButtonPressed(GameButton.BUTTON_A)){
                gamePad.releaseButton(GameButton.BUTTON_A);
                //can we shoot?
                if(gunComponent.getCooldown().isDone()){
                    gunComponent.getCooldown().reset();
                    gunComponent.setShooting(true);

                    gamePad.setEnabled(false);

                    //CREATE A BULLET
                    Entity bullet = this.createBullet(physComp, gunComponent);
                    ((LifespanComponent)bullet.getComponent(LifespanComponent.ID)).startLife();
                    //Move the bullet
                    int direction = (physComp.getDirection() == PhysicsComponent.Direction.RIGHT) ? 1 : -1;
                    MovementSystem.moveInX(bullet, gunComponent.getBulletSpeed().x  * direction);
                    MovementSystem.moveInY(bullet, gunComponent.getBulletSpeed().y);

                    //Put playerInfo

                    this.fireEvent(new GunShotEvent(entity.getID()));
                    // TODO a SMAAALLL knockback when shooting


                }
            }
    }



    @Override
    public void update(float dt) {

        for(Entity entity: getEntityManager().getEntitiesWithComponentEnabled(GunComponent.ID)){
            GunComponent gun = (GunComponent)entity.getComponent(GunComponent.ID);
            //Check if the punch duration is over, if so hide the punch
            if(gun.isShooting()){
                if(gun.getDurationTimer().isDone()){
                    gun.setShooting(false);
                    gun.getCooldown().reset();


                    //Re-enable game pad
                    entity.enableComponent(VirtualGamePad.ID);


                    fireEvent(new FinishGunShotEvent(entity.getID()));
                }
            }

        }

    }


    @Override
    public <T extends EntityEvent> void onEntityEvent(T event) {
        if(event.getClass() == CollisionEvent.class){
            this.onCollision((CollisionEvent) event);
        }
    }


    /**
     * Triggered when a collision occurs
     * Used to see if a bullet has collided with something
     */
    private void onCollision(CollisionEvent collision){
        if(collision.getEntityA() != null) {
            Entity entityA = getEntityManager().getEntity(collision.getEntityA());
            if (entityA.hasComponent(TagsComponent.ID)) {
                TagsComponent tags = (TagsComponent) entityA.getComponent(TagsComponent.ID);
                if (tags.hasTag(Constants.ENTITY_TAG_BULLET)) {
                    Hitbox hitboxB = (Hitbox) collision.getFixtureB().getUserData();
                    if(hitboxB.type != Hitbox.Type.Intangible){
                        PhysicsComponent phys = (PhysicsComponent) entityA.getComponent(PhysicsComponent.ID);
                        phys.getBody().setActive(false);
                    }
                }
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
        return new BulletFactory(this.getEntityManager(), phys.getBody().getWorld(), position)
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