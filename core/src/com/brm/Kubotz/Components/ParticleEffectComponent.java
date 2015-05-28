package com.brm.Kubotz.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.brm.GoatEngine.ECS.Components.EntityComponent;
import com.brm.GoatEngine.GParticleEffect;

import java.util.ArrayList;


/**
 * Used to emit particles from that entity's position
 */
public class ParticleEffectComponent extends EntityComponent{

    public static String ID = "PARTICLE_EMITTER_COMPONENT";

    private ArrayList<GParticleEffect> effects = new ArrayList<GParticleEffect>();


    /**
     * Adds a new Particle effect to the list
     * @param particle
     * @param pos
     * @param startNow
     */
    public void addEffect(FileHandle particle, Vector2 pos, boolean startNow){
        GParticleEffect effect = new GParticleEffect();
        effect.load(particle, Gdx.files.internal(""));
        effect.setPosition(pos.x, pos.y);
        if(startNow){
            effect.start();
        }
        effects.add(effect);
    }

    /**
     * Adds a new particle effect not starting right awaya
     * @param particle
     * @param pos
     */
    public void addEffect(FileHandle particle, Vector2 pos){
       addEffect(particle, pos, false);
    }


    public ArrayList<GParticleEffect> getEffects() {
        return effects;
    }







}
