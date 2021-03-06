package com.brm.Kubotz.Features.FlyBoots;

import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.Utils.Timer;
import com.brm.Kubotz.Config;

/**
 * Boots giving a Kubotz the ability to Fly
 */
public class FlyingBootsComponent extends EntityComponent {
    public static String ID = "FLY_BOOTS_COMPONENT";


    private Timer effectDuration = new Timer(Config.FLYBOOTS_DURATION);   // The duration of the flight
    private Timer cooldown = new Timer(Config.FLYBOOTS_COOLDOWN);     // Min amount of time needed between uses of skill


    public FlyingBootsComponent(){
        effectDuration.start();
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

    public Timer getEffectDuration() {
        return effectDuration;
    }

    public Timer getCooldown() {
        return cooldown;
    }
}
