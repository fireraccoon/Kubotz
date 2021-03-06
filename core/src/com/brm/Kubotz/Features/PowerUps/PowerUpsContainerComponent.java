package com.brm.Kubotz.Features.PowerUps;

import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.Kubotz.Features.PowerUps.PowerUp;

import java.util.ArrayList;

/**
 * Used for an entity to have PowerUps
 */
public class PowerUpsContainerComponent extends EntityComponent {

    public static final String ID = "POWER_UPS_CONTAINER_COMPONENT";

    ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();


    public PowerUpsContainerComponent(XmlReader.Element element){
        super(element);
    }

    public PowerUpsContainerComponent(){
        
    }


    /**
     * Adds a powerup to the list and activates its effect
     */
    public void addPowerUp(PowerUp p){
        this.powerUps.add(p);
    }

    /**
     * Removes a powerup from the list and deactivate its effect
     */
    public void removePowerUp(PowerUp p){
        this.powerUps.remove(p);
    }


    public ArrayList<PowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * Desiralizes a component
     *
     * @param componentData the data as an XML element
     */
    @Override
    public void deserialize(XmlReader.Element componentData) {

    }
}
