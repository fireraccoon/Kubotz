package com.brm.Kubotz.Features.GameRules;

import com.brm.GoatEngine.EventManager.EntityEvent;
import com.brm.GoatEngine.Utils.Logger;

/**
 * Triggered when a player is Defeated
 */
public class PlayerEliminatedEvent extends EntityEvent {

    /**
     *
     * @param entityId The player defeated
     */
    public PlayerEliminatedEvent(String entityId) {
        super(entityId);
        Logger.debug("Player Elliminated");
    }




}
