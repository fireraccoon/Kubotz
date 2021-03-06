package com.brm.Kubotz.Features.Rooms;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;

/**
 * Represents a Room
 */
public class RoomComponent extends EntityComponent{
    public static final String ID = "ROOM_COMPONENT";

    private TiledMap tiledMap;

    private boolean loaded; //Whether or not the map was loaded in the


    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
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
