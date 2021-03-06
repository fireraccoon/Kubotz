package com.brm.Kubotz.Common.Components;

import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.Utils.Timer;

/**
 * Makes an entity die after a certain amount of time
 */
public class LifespanComponent extends EntityComponent {

    public static final String ID = "LIFESPAN_COMPONENT";

    private Timer counter;

    /**
     * Ctor
     * @param lifespan : in ms
     */
    public LifespanComponent(int lifespan){
        this.counter = new Timer(lifespan);
    }

    /**
     * Ctor
     * Lifespan defaults to infinite
     */
    public LifespanComponent(){
        this.counter = new Timer(Timer.INFINITE);
    }

    /**
     * Desiralizes a component
     *
     * @param componentData the data as an XML element
     */
    @Override
    public void deserialize(XmlReader.Element componentData) {

    }


    /**
     * Starts the life counter
     */
    public void startLife(){
        this.counter.start();
    }

    /**
     * Returns whether or not the lifespan is finished
     * @return
     */
    public boolean isFinished(){
        return  this.counter.isDone();
    }

    public Timer getCounter() {
        return counter;
    }

    public void setCounter(Timer counter) {
        this.counter = counter;
    }
}
