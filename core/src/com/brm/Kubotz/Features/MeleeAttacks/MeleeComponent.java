package com.brm.Kubotz.Features.MeleeAttacks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.Utils.Timer;
import com.brm.Kubotz.Config;
import com.brm.Kubotz.Constants;

/**
 * Component used to let an entity punch
 */
public class MeleeComponent extends EntityComponent {

    public static final String ID = "PUNCH_COMPONENT";

    private int damage = Config.PUNCH_DAMAGE; //Number of damage per hit

    private Timer inputDelay = new Timer(Constants.PUNCH_INPUT_DELAY);
    private Timer durationTimer = new Timer(Constants.PUNCH_DURATION); //The Duration of the hit
    private Timer cooldown = new Timer(Config.PUNCH_COOLDOWN); //The delay between hits
    private PhysicsComponent phys;

    private Vector2 knockBack = new Vector2(0.1f, 0.1f);
    private boolean attacking;


    /**
     *
     * @param phys : The physics component of the entity
     */
    public MeleeComponent(PhysicsComponent phys){
        this.durationTimer.start();
        this.cooldown.start();
    }

    public MeleeComponent(XmlReader.Element componentData){
        super(componentData);
        this.durationTimer.start();
        this.cooldown.start();
    }


    @Override
    public void onDetach(Entity entity) {

    }

    /**
     * Desiralizes a component
     *
     * @param componentData the data as an XML element
     */
    @Override
    public void deserialize(XmlReader.Element componentData){}

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Timer getDurationTimer() {
        return durationTimer;
    }

    public void setDurationTimer(Timer durationTimer) {
        this.durationTimer = durationTimer;
    }

    public Timer getCooldown() {
        return cooldown;
    }

    public PhysicsComponent getPhys() {
        return phys;
    }

    public void setPhys(PhysicsComponent phys) {
        this.phys = phys;
    }

    public Vector2 getKnockBack() {
        return knockBack;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
}
