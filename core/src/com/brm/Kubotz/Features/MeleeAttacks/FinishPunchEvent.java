package com.brm.Kubotz.Features.MeleeAttacks;

import com.brm.GoatEngine.EventManager.EntityEvent;

/**
 * Triggered wjhen an entity finishes Punching
 */
public class FinishPunchEvent extends EntityEvent {

    public FinishPunchEvent(String entityId) {
        super(entityId);
    }
}
