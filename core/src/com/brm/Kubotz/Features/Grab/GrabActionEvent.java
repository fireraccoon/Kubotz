package com.brm.Kubotz.Features.Grab;

import com.brm.GoatEngine.EventManager.EntityEvent;

/**
 * Triggered when an entity decides to grab
 */
public class GrabActionEvent extends EntityEvent {

    public GrabActionEvent(String entityId) {
        super(entityId);
    }
}
