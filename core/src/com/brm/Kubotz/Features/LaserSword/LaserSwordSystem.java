package com.brm.Kubotz.Features.LaserSword;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.ECS.core.EntitySystem;
import com.brm.GoatEngine.Input.VirtualGamePad;
import com.brm.Kubotz.Config;
import com.brm.Kubotz.Constants;
import com.brm.GoatEngine.Physics.Hitbox.Hitbox;
import com.brm.Kubotz.Input.GameButton;

/**
 * Used to handle the entities punching
 */
public class LaserSwordSystem extends EntitySystem{

    public LaserSwordSystem(){}

    @Override
    public void init(){}


    @Override
    public void handleInput() {
        for(Entity entity: getEntityManager().getEntitiesWithComponentEnabled(LaserSwordComponent.ID)) {
            if(entity.hasComponentEnabled(VirtualGamePad.ID)) {
                handleInputForEntity(entity);
            }
        }
    }


    private void handleInputForEntity(Entity entity){
        VirtualGamePad gamePad = (VirtualGamePad) entity.getComponent(VirtualGamePad.ID);
        LaserSwordComponent laserSword = (LaserSwordComponent)entity.getComponent(LaserSwordComponent.ID);
        PhysicsComponent phys = (PhysicsComponent)entity.getComponent(PhysicsComponent.ID);

        //Triggers the punch
        if(gamePad.isButtonPressed(GameButton.BUTTON_A)){

            if(laserSword.getCooldown().isDone() && laserSword.getDurationTimer().isDone() && !laserSword.isSwinging()){
                laserSword.getDurationTimer().reset();
                createAttackBox(phys);
                laserSword.setSwinging(true);

                fireEvent(new SwordSwingEvent(entity.getID()));


                //DISABLE GAMEPAD
                gamePad.setEnabled(false); //To prevent punching ir jumping at the same time
            }
        }
    }


    public void update(float dt) {
        // See if punch duration is over

        for(Entity entity: getEntityManager().getEntitiesWithComponentEnabled(LaserSwordComponent.ID)){
            LaserSwordComponent laserSword = (LaserSwordComponent)entity.getComponent(LaserSwordComponent.ID);
            //Check if the punch duration is over, if so hide the punch
            if(laserSword.isSwinging()){
                if(laserSword.getDurationTimer().isDone()){
                    laserSword.setSwinging(false);
                    laserSword.getCooldown().reset();
                    removeAttackBox((PhysicsComponent) entity.getComponent(PhysicsComponent.ID));

                    //Re-enable game pad
                    entity.enableComponent(VirtualGamePad.ID);


                    fireEvent(new FinishSwordSwingEvent(entity.getID()));
                }
            }

        }
    }




    /**
     * Creates an attack box for the melee attack
     * @param phys
     */
    private void createAttackBox(PhysicsComponent phys){
        CircleShape shape = new CircleShape();
        shape.setRadius(phys.getWidth() * 2.5f);

        Vector2 position = null;
        switch (phys.getDirection()) {
            case RIGHT:
                position = new Vector2(phys.getWidth() + phys.getWidth()/2, 0);
                break;
            case LEFT:
                position = new Vector2(-phys.getWidth()-phys.getWidth()/2, 0);
                break;
        }
        shape.setPosition(position);

        FixtureDef punchFixture = new FixtureDef();
        punchFixture.isSensor = true;
        punchFixture.shape = shape;

        Hitbox hitbox = new Hitbox(Hitbox.Type.Offensive, Constants.HITBOX_LABEL_MELEE);
        hitbox.damage = Config.LASER_SWORD_DAMAGE;
        int dir = (phys.getDirection() == PhysicsComponent.Direction.LEFT) ? -1 : 1;
        hitbox.knockback = 2 * dir;


        phys.getBody().createFixture(punchFixture).setUserData(hitbox);
        shape.dispose();

    }

    /**
     * Removes the attack box
     * @param phys
     */
    public void removeAttackBox(PhysicsComponent phys){
        for(int i=0; i<phys.getBody().getFixtureList().size ;i++){
            Fixture fixture = phys.getBody().getFixtureList().get(i);
            Hitbox hitbox = (Hitbox) fixture.getUserData();
            if(hitbox.label.equals(Constants.HITBOX_LABEL_MELEE)) {
                phys.getBody().destroyFixture(fixture);
            }
        }
    }








}

