package com.brm.Kubotz.Features.DroneGauntlet;

import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.Utils.Timer;

import java.util.ArrayList;



/**
 * A Gauntlet spawning DRONES
 */
public class DroneGauntletComponent extends EntityComponent {
    public final static String ID = "DRONE_GAUNTLET_COMPONENT";

    private final int nbDronesLimint = 2;   //the maximum number of drones at once

    private Timer cooldown = new Timer(Timer.FIVE_SECONDS);

    private ArrayList<String> drones = new ArrayList<String>();


    public DroneGauntletComponent(){
        cooldown.start();
    }

    /**
     * Desiralizes a component
     *
     * @param componentData the data as an XML element
     */
    @Override
    public void deserialize(XmlReader.Element componentData) {

    }


    public Timer getCooldown() {
        return cooldown;
    }

    public void addDrone(String droneId) {
        drones.add(droneId);
    }

    public void removeDrone(String droneId){
        drones.remove(droneId);
    }


    public ArrayList<String> getDrones(){
        return drones;
    }

    public int getNbDronesLimint() {
        return nbDronesLimint;
    }
}