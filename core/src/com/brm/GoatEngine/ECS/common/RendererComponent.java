package com.brm.GoatEngine.ECS.common;

import com.badlogic.gdx.utils.XmlReader;
import com.brm.GoatEngine.ECS.core.EntityComponent;
import com.brm.GoatEngine.GraphicsEngine.Renderer;

import java.util.HashMap;

/**
 * Used when an entity needs more than a simple SpriteComponent and/or Animation Component
 * to be rendered. In that case we can provide this Component with a
 * Renderer.
 */
public class RendererComponent extends EntityComponent {
    public final static String ID = "RENDERER_COMPONENT";

    private final HashMap<String, Renderer> renderers = new HashMap<String, Renderer>();

    public RendererComponent(Renderer renderer) {
       // this.renderer = renderer;
    }


    public Renderer getRenderer() {
        return null;
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
