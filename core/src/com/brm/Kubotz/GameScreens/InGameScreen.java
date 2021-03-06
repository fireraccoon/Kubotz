package com.brm.Kubotz.GameScreens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brashmonkey.spriter.Spriter;
import com.brashmonkey.spriter.gdxIntegration.LibGdxSpriterDrawer;
import com.brashmonkey.spriter.gdxIntegration.LibGdxSpriterLoader;
import com.brm.GoatEngine.ECS.EditorEntityProperty;
import com.brm.GoatEngine.ECS.common.PhysicsComponent;
import com.brm.GoatEngine.ECS.core.Entity;
import com.brm.GoatEngine.ECS.core.EntityManager;
import com.brm.GoatEngine.ECS.core.EntitySystemManager;
import com.brm.GoatEngine.ScriptingEngine.ScriptSystem;
import com.brm.GoatEngine.ScreenManager.GameScreen;
import com.brm.GoatEngine.ScreenManager.GameScreenManager;
import com.brm.GoatEngine.Utils.Logger;
import com.brm.Kubotz.Common.Systems.AISystem;
import com.brm.Kubotz.Common.Systems.AttackSystems.DamageSystem;
import com.brm.Kubotz.Common.Systems.LifespanSystem;
import com.brm.Kubotz.Common.Systems.PhysicsSystem;
import com.brm.GoatEngine.ECS.EntityXMLFactory;
import com.brm.Kubotz.Features.GameRules.PlayerScoreComponent;
import com.brm.Kubotz.Features.GameRules.GameRulesSystem;
import com.brm.Kubotz.Features.GameRules.LifeBasedFreeForAll;
import com.brm.Kubotz.Features.KubotzCharacter.Components.SkullHeadComponent;
import com.brm.Kubotz.Features.PowerUps.PowerUpsSystem;
import com.brm.Kubotz.Features.Respawn.SpawnPointComponent;
import com.brm.Kubotz.Constants;
import com.brm.Kubotz.Features.Grab.GrabSystem;
import com.brm.Kubotz.Features.Respawn.RespawnSystem;
import com.brm.Kubotz.Input.InputTranslationSystem;
import com.brm.Kubotz.Common.Systems.AttackSystems.AttackSystem;
import com.brm.Kubotz.Features.MeleeAttacks.MeleeSystem;
import com.brm.Kubotz.Common.Systems.MovementSystems.MovementSystem;
import com.brm.Kubotz.Common.Systems.RendringSystems.AnimationSystem;
import com.brm.Kubotz.Common.Systems.RendringSystems.RenderingSystem;
import com.brm.Kubotz.Common.Systems.SkillsSystem.SkillsSystem;


public class InGameScreen extends GameScreen {

    private EntityManager entityManager;
    private EntitySystemManager systemManager;

    //MAP
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;


    private boolean isPlayerOneSpawned = false;


    @Override
    public void init(GameScreenManager engine) {
        super.init(engine);
        Logger.info("In Game State initialisation");

        // Systems Init
        entityManager = ecsManager.getEntityManager();
        systemManager = ecsManager.getSystemManager();


        systemManager.addSystem(PhysicsSystem.class, new PhysicsSystem());

        //Camera
        EntityXMLFactory.createEntity("blueprint/Camera.xml", this.getEntityManager(),
                this.systemManager.getSystem(PhysicsSystem.class).getWorld());


        systemManager.addSystem(RenderingSystem.class, new RenderingSystem());
        systemManager.addSystem(InputTranslationSystem.class, new InputTranslationSystem());
        systemManager.addSystem(MovementSystem.class, new MovementSystem());

        systemManager.addSystem(GrabSystem.class, new GrabSystem());

        systemManager.addSystem(SkillsSystem.class, new SkillsSystem());

        systemManager.addSystem(PowerUpsSystem.class, new PowerUpsSystem());

        systemManager.addSystem(MeleeSystem.class, new MeleeSystem());

        systemManager.addSystem(LifespanSystem.class, new LifespanSystem());

        systemManager.addSystem(DamageSystem.class, new DamageSystem());

        systemManager.addSystem(AttackSystem.class, new AttackSystem());

        systemManager.addSystem(ScriptSystem.class, new ScriptSystem());

        systemManager.addSystem(AnimationSystem.class, new AnimationSystem());

        systemManager.addSystem(RespawnSystem.class, new RespawnSystem());

        systemManager.addSystem(AISystem.class, new AISystem());


        // LIFE BASED FREE FOR ALL
        systemManager.addSystem(GameRulesSystem.class, new GameRulesSystem());
        systemManager.getSystem(GameRulesSystem.class).setActiveRuleSystem(LifeBasedFreeForAll.class, new LifeBasedFreeForAll());



        //INIT SYSTEMS
        systemManager.initSystems();




        // Init Animation Manager
        Spriter.setDrawerDependencies(
                systemManager.getSystem(RenderingSystem.class).getSpriteBatch(),
                systemManager.getSystem(RenderingSystem.class).getShapeRenderer()
        );
        Spriter.init(LibGdxSpriterLoader.class, LibGdxSpriterDrawer.class);
        Spriter.load(Gdx.files.internal(Constants.KUBOTZ_ANIM_FILE).read(), Constants.KUBOTZ_ANIM_FILE);


        // MAP
        //LOAD MAP
        tiledMap = new TmxMapLoader().load(Constants.MAIN_MAP_FILE);
        float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/tileSize);
        systemManager.getSystem(RenderingSystem.class).setMapRenderer(mapRenderer);

        MapObjects mapObjects = tiledMap.getLayers().get("objects").getObjects();

        for(int i=0; i<mapObjects.getCount(); i++){

            RectangleMapObject obj = (RectangleMapObject) mapObjects.get(i);
            Rectangle rect = obj.getRectangle();
            String objType = (String) obj.getProperties().get("type");
            Vector2 position = new Vector2(rect.getX()/tileSize, rect.getY()/tileSize);
            float width = rect.getWidth()/tileSize, height = rect.getHeight()/tileSize;

            if(objType == null)
                continue;

            if(objType.equals("PLAYER_SPAWN")){
                int id = (isPlayerOneSpawned) ? PlayerScoreComponent.PLAYER_2 : PlayerScoreComponent.PLAYER_1;
                EntityXMLFactory.editorProperty = new EditorEntityProperty(position, width, height);
                Entity player = EntityXMLFactory.createEntity("blueprint/Kubotz.xml", this.getEntityManager(),
                        this.systemManager.getSystem(PhysicsSystem.class).getWorld()
                );
                PhysicsComponent phys = (PhysicsComponent) player.getComponent(PhysicsComponent.ID);
                phys.setPosition(position.x, position.y);

                /*Entity player = new KubotzFactory(entityManager, systemManager.getSystem(PhysicsSystem.class).getWorld(),
                        new Vector2(rect.getX()/tileSize, rect.getY()/tileSize))
                        .withHeight(2.0f)
                        .withCameraTargetComponent()
                        .build();*/
                player.addComponent(new PlayerScoreComponent(id), PlayerScoreComponent.ID);


                if(id == PlayerScoreComponent.PLAYER_1){
                    isPlayerOneSpawned = true;
                    player.addComponent(new SkullHeadComponent(), SkullHeadComponent.ID);
                    //player.addComponent(new DashBootsComponent(), DashBootsComponent.ID);
                }else{
                    //player.addComponent(new FlyingBootsComponent(), FlyingBootsComponent.ID);
                }


                Entity entity = new Entity();
                entityManager.registerEntity(entity);
                entity.addComponent(new SpawnPointComponent(new Vector2(rect.getX()/tileSize, rect.getY()/tileSize),
                        SpawnPointComponent.Type.Player), SpawnPointComponent.ID);

            }else if(objType.equals("ENTITY")){
                EntityXMLFactory.editorProperty = new EditorEntityProperty(position, width, height);
                String blueprint = obj.getProperties().get("blueprint").toString();
                Entity e = EntityXMLFactory.createEntity(blueprint, this.getEntityManager(),
                        this.systemManager.getSystem(PhysicsSystem.class).getWorld()
                );
                if(e.hasComponent(PhysicsComponent.ID)){
                    PhysicsComponent phys = (PhysicsComponent) e.getComponent(PhysicsComponent.ID);
                    //phys.setPosition(position.x, position.y);

                }


            }else{
                /*new BlockFactory(this.entityManager, systemManager.getSystem(PhysicsSystem.class).getWorld(),
                        new Vector2(rect.getX()/tileSize, rect.getY()/tileSize))
                        .withSize(rect.getWidth() / tileSize, rect.getHeight() / tileSize)
                        .withTag(Constants.ENTITY_TAG_PLATFORM)
                        .build();*/
            }
        }


        Logger.info("In Game State initialised");
    }



    @Override
    public void cleanUp(){}

    @Override
    public void resume(){}

    @Override
    public void handleInput(GameScreenManager engine){

        systemManager.getSystem(InputTranslationSystem.class).handleInput();

        //Since Scripts Can produce Input during their update phase
        systemManager.getSystem(ScriptSystem.class).handleInput();
        systemManager.getSystem(ScriptSystem.class).update(0);

        systemManager.getSystem(MovementSystem.class).handleInput();
        systemManager.getSystem(GrabSystem.class).handleInput();
        systemManager.getSystem(SkillsSystem.class).handleInput();
        systemManager.getSystem(AttackSystem.class).handleInput();


    }

    @Override
    public void update(GameScreenManager engine, float deltaTime) {

        systemManager.getSystem(AISystem.class).update(deltaTime);

        systemManager.getSystem(MovementSystem.class).update(deltaTime);

        systemManager.getSystem(SkillsSystem.class).update(deltaTime);

        systemManager.getSystem(AttackSystem.class).update(deltaTime);

        systemManager.getSystem(GrabSystem.class).update(deltaTime);
        systemManager.getSystem(DamageSystem.class).update(deltaTime);
        systemManager.getSystem(LifespanSystem.class).update(deltaTime);
        systemManager.getSystem(PowerUpsSystem.class).update(deltaTime);

        systemManager.getSystem(ScriptSystem.class).update(deltaTime);

        systemManager.getSystem(PhysicsSystem.class).update(deltaTime);
        systemManager.getSystem(AnimationSystem.class).update(deltaTime);


        systemManager.getSystem(RespawnSystem.class).update(deltaTime);


        systemManager.getSystem(GameRulesSystem.class).update(deltaTime);


    }

    @Override

    public void draw(GameScreenManager engine, float deltaTime) {

        // DRAW WORLD
        systemManager.getSystem(RenderingSystem.class).update(deltaTime);


    }
}
