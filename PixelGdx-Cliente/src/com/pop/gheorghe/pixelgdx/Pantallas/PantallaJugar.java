package com.pop.gheorghe.pixelgdx.Pantallas;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.plaf.synth.SynthScrollBarUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Posicion;
import com.pop.gheorghe.pixelgdx.Sprites.InfoOndaVital;
import com.pop.gheorghe.pixelgdx.Sprites.InfoSerpiente;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador.State;
import com.pop.gheorghe.pixelgdx.Sprites.OndaVital;
import com.pop.gheorghe.pixelgdx.Sprites.Serpiente;
import com.pop.gheorghe.pixelgdx.Tools.WorldContactListener;

public class PantallaJugar implements Screen{
	// Referencia al juego principal
	private PixelGdx game;
	
	// Game Cam y viewPort
	private OrthographicCamera gameCam;
	private Viewport gamePort;
	
	// Tiled Map
	private TmxMapLoader maploader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	// Box2D
	private World world;
	private Box2DDebugRenderer b2debugRender;
	
	// Jugador
	private Jugador jugadorPrincipal;
	private Jugador jugadorDos;
	private TextureAtlas atlas;
	
	// Serpiete
	private ArrayList<Serpiente> serpientes;
	
	private ArrayList<OndaVital> ondas;

	private float GRAVEDAD = -5;
	
	// Constructor
	public PantallaJugar( PixelGdx game ) {
		// "cosas.atlas"
		atlas = new TextureAtlas("assets/cosas.atlas");
		// Game
		this.game = game;
		// Game cam
		gameCam = new OrthographicCamera();
		
		// Game Port
		gamePort = new FitViewport(PixelGdx.WIDTH / PixelGdx.PPM, PixelGdx.HEIGHT / PixelGdx.PPM, gameCam);
		
		// Carga el mapa
		maploader = new TmxMapLoader();
		TmxMapLoader.Parameters param = new TmxMapLoader.Parameters();
        param.textureMagFilter = Texture.TextureFilter.Nearest;
        param.textureMinFilter = Texture.TextureFilter.Nearest;
        param.generateMipMaps = true;
        map = maploader.load("assets/tiles/mapa1/mapaGigante.tmx",param);
        renderer = new OrthogonalTiledMapRenderer(map, 1f  / PixelGdx.PPM);
        
        
        // Posiciona la camara centrada en el principio del mapa
        gameCam.position.set(gamePort.getWorldWidth() / 2f, gamePort.getWorldHeight() / 2f, 0);
        gameCam.setToOrtho(false, PixelGdx.WIDTH / PixelGdx.PPM, PixelGdx.HEIGHT / PixelGdx.PPM);

        // Crea el Mundo Box2D, gravedad..etc
        world = new World(new Vector2(0, GRAVEDAD ), true);
        
        // Debug Render
        b2debugRender = new Box2DDebugRenderer();

        // Jugador
        if ( game.isServer ) {
	        jugadorPrincipal = new Jugador(this,"rey",69);
	        jugadorDos = new Jugador(this,"trebol",1);
        }else {
        	jugadorPrincipal = new Jugador(this,"trebol",1);
	        jugadorDos = new Jugador(this,"rey",69);
        }

        // Serpiente
        serpientes = new ArrayList<Serpiente>();

        // Ondas
        ondas = new ArrayList<OndaVital>();

        // Crea los cuerpos
        crearB2World();
        
        // Contact Listener
        world.setContactListener( new WorldContactListener(game.isServer) );
	}
	
	public TextureAtlas geAtlas() {
		return atlas;
	}
	
	private void crearB2World () {
		BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Suelo
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(
            		(rect.getX() + rect.getWidth() / 2f) / PixelGdx.PPM, 
            		(rect.getY() + rect.getHeight() / 2f) / PixelGdx.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(
            		rect.getWidth() / 2f / PixelGdx.PPM, 
            		rect.getHeight() / 2f / PixelGdx.PPM);
            
            fdef.shape = shape;
            fdef.filter.categoryBits = PixelGdx.CATEGORIA_ESCENARIO;
            fdef.filter.maskBits = PixelGdx.MASK_ESCENEARIO;
            body.createFixture(fdef);
            
        }
        // Suelo
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(PolylineMapObject.class)){
        	float[] vertices = ((PolylineMapObject) object).getPolyline().getTransformedVertices();
        	Vector2[] worldVertices = new Vector2[vertices.length /2];
        	
        	for (int i = 0; i < worldVertices.length; i++) {
        		worldVertices[i] = new Vector2(vertices[i*2] / PixelGdx.PPM, vertices[ i* 2 +1] / PixelGdx.PPM);
				
			}
        	ChainShape cs = new ChainShape();
        	cs.createChain(worldVertices);
        	Shape shapePoly = cs;
        	
        	Body bdy;
        	BodyDef bdyf = new BodyDef();
        	
        	
        	bdyf.type = BodyType.StaticBody;
        	bdy = world.createBody(bdyf);
        	bdy.createFixture(shapePoly, 1.0f);
        	
        	
        	shapePoly.dispose();
        	
        }
        
        
        // Limites - para que los enemigos choquen y se muevan en ciertos limites
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(
            		(rect.getX() + rect.getWidth() / 2f) / PixelGdx.PPM, 
            		(rect.getY() + rect.getHeight() / 2f) / PixelGdx.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(
            		rect.getWidth() / 2f / PixelGdx.PPM, 
            		rect.getHeight() / 2f / PixelGdx.PPM);
            
            fdef.shape = shape;
            fdef.filter.categoryBits = PixelGdx.CATEGORIA_LIMITE;
            fdef.filter.maskBits = PixelGdx.MASK_LIMITES;
            body.createFixture(fdef);
        }
        int i = 0;
//         Serpientes
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
        	Rectangle rect = ((RectangleMapObject) object).getRectangle();
        	serpientes.add( new Serpiente(this, rect.getX() / PixelGdx.PPM, rect.getY() / PixelGdx.PPM, i++));
        }
	}
	
	public void handleInput( float dt) {
		// Movimiento del jugador
		jugadorPrincipal.movimientoJugador();
		if ( game.isServer ) {
			// TODO: Mandar info al cliente de la posicion del jugadorPrincipal
			Posicion posJugador = new Posicion();
			posJugador.posicion = jugadorPrincipal.b2body.getTransform().getPosition();
			posJugador.angle = jugadorPrincipal.b2body.getAngle();
			posJugador.state = jugadorPrincipal.getState();
			posJugador.stateTime = jugadorPrincipal.stateTimer;
			posJugador.direccionDerecha = jugadorPrincipal.enDireccionDerecha();
			
			// TODO: Mandar las ondas vitales
			posJugador.ondasVitales = new ArrayList<>();
			for ( OndaVital v : ondas ) {
				//System.out.println("Quiero mandar onda : " +v.tiempo);
				if ( v.tiempo == 0 ) {
					InfoOndaVital auxO = new InfoOndaVital();
					auxO.posicion = v.b2body.getTransform().getPosition();
					auxO.dirDerecha = v.direccionDerecha;
					auxO.stateTime = v.tiempo;
					posJugador.ondasVitales.add( auxO );
				}
			}
			
			// TODO: mandar info de los enemigos ( serpientes )
			posJugador.serpientes = new ArrayList<>();
			for ( Serpiente s : serpientes ) {
				//System.out.println("Quiero mandar serpiente : " +s.tiempo);
				InfoSerpiente auxO = new InfoSerpiente();
				auxO.id = s.id;
				auxO.posicion = s.b2body.getTransform().getPosition();
				auxO.dirDerecha = s.direccionDerecha;
				auxO.stateTime = s.tiempo;
				auxO.angle = s.b2body.getAngle();
				auxO.destruir = s.getDestruido();
				auxO.vida = s.getVida();
				posJugador.serpientes.add( auxO );
			}
			
			
			game.sock.mandarInfo(posJugador);
		}else {
			//TODO: Mandar info al servior de la posicion del jugadorPrincipal
			Posicion posJugador = new Posicion();
			posJugador.posicion = jugadorPrincipal.b2body.getTransform().getPosition();
			posJugador.angle = jugadorPrincipal.b2body.getAngle();
			posJugador.state = jugadorPrincipal.getState();
			posJugador.stateTime = jugadorPrincipal.stateTimer;
			posJugador.direccionDerecha = jugadorPrincipal.enDireccionDerecha();

			// TODO: Mandar las ondas vitales
			posJugador.ondasVitales = new ArrayList<>();
			for ( OndaVital v : ondas ) {
//				System.out.println("Quiero mandar onda : " +v.tiempo);
				if ( v.tiempo == 0 ) {
					InfoOndaVital auxO = new InfoOndaVital();
					auxO.posicion = v.b2body.getTransform().getPosition();
					auxO.dirDerecha = v.direccionDerecha;
					auxO.stateTime = v.tiempo;
					posJugador.ondasVitales.add( auxO );
				}
			}

			game.sockClient.mandarInfo(posJugador);
		}
		
	}
	
	// Crea una onda vital en el mapa
	public void crearOndaVital ( Jugador jugador ) {
		OndaVital nuevaOnda = new OndaVital( this, 
				jugador.b2body.getPosition().x, 
				jugador.b2body.getPosition().y+0.1f, 
				jugador.enDireccionDerecha(), 
				jugador);
		
		ondas.add( nuevaOnda );
	}
	
	
	
	public void update( float delta ) {
		// HandleInput
		handleInput(delta);
		// takes 1 step in the physics simulation(60 times per second)
		world.step(1 / 60f, 6, 2);

		// Actualiza jugador
		jugadorPrincipal.update(delta);
		// TODO: SERVER Conseguir informaci�n del jugador 2 en el server
		if ( game.isServer ) {
			if ( game.sock.posicionJugador() != null) {
				jugadorDos.b2body.setTransform(game.sock.posicionJugador().posicion, game.sock.posicionJugador().angle );
				TextureRegion textura = jugadorDos.getFrame(delta, game.sock.posicionJugador().state, game.sock.posicionJugador().stateTime , game.sock.posicionJugador().direccionDerecha);
				jugadorDos.update(delta, textura);
				
				// TODO: Leer el estado de las ondas vitales
				ArrayList<InfoOndaVital> infoOndas = game.sock.posicionJugador().ondasVitales;
				if ( infoOndas != null )
					for ( InfoOndaVital v : infoOndas ) {
						System.out.println("Creando buena ondaaaa en el servidor");
						OndaVital auxOnda = new OndaVital(this, v.posicion.x, v.posicion.y, v.dirDerecha, jugadorDos);
						ondas.add(auxOnda);
					}
				
				
			}else
				jugadorDos.update(delta);
		}else { // TODO: CLIENTE conseguir info
			jugadorDos.update(delta);
			if ( game.sockClient.posicionJugador() != null) {
				jugadorDos.b2body.setTransform(game.sockClient.posicionJugador().posicion, game.sockClient.posicionJugador().angle );
				TextureRegion textura = jugadorDos.getFrame(delta, game.sockClient.posicionJugador().state, game.sockClient.posicionJugador().stateTime , game.sockClient.posicionJugador().direccionDerecha);
				jugadorDos.update(delta, textura);
				
				// TODO: Leer el estado de las ondas vitales
				ArrayList<InfoOndaVital> infoOndas = game.sockClient.posicionJugador().ondasVitales;
				if ( infoOndas != null )
					for ( InfoOndaVital v : infoOndas ) {
						System.out.println("Creando buena onda en el cliente");
						OndaVital auxOnda = new OndaVital(this, v.posicion.x, v.posicion.y, v.dirDerecha, jugadorDos);
						ondas.add(auxOnda);
					}
				
				// TODO: lee el estado de las serpientes
				ArrayList<InfoSerpiente> infoSerpientes = game.sockClient.posicionJugador().serpientes;
				if ( infoSerpientes != null )
					for ( InfoSerpiente s : infoSerpientes ) {
//						System.out.println("Actualizando serpiente");
						
						for ( Serpiente auxS : serpientes ) {
							if ( auxS.id == s.id ) {
								auxS.b2body.setTransform(s.posicion, s.angle);
								auxS.direccionDerecha = s.dirDerecha;
								auxS.setVida(s.vida);
								if ( s.destruir ) auxS.destruir();
								auxS.update(s.stateTime);
							}
						}
						
					}
			}
			
		}
			
//				System.out.println("Voy a :" + game.sock.posX().x );
//				jugadorDos.b2body.applyForceToCenter(1f, 0, true);
//				jugadorDos.b2body.setTransform(game.sock.posX().x, 0, jugador.b2body.getAngle());
			
		// Actualiza serpiente
		for( Serpiente s : serpientes )
			s.update(delta);
		
		ArrayList<OndaVital> aEliminar = new ArrayList<OndaVital>();
		for (OndaVital ondaVital : ondas) {
			if ( ondaVital.finAnimacion() ) {
				aEliminar.add(ondaVital);
				world.destroyBody(ondaVital.b2body);
			}
			else
				ondaVital.update(delta);
		}
		ondas.removeAll(aEliminar);
//		
		//update our gamecam with correct coordinates after changes
		//System.out.println("Pos jugador: " +jugador.b2body.getPosition().x +" menor que: " +(gamePort.getScreenWidth()/PixelGdx.PPM)/2);
//		if ( jugador.b2body.getPosition().x < (gamePort.getScreenWidth()/PixelGdx.PPM)/2 )
//			gameCam.position.x = Math.round( (gamePort.getScreenWidth()/PixelGdx.PPM)/2 * 100f)/100f; // Esto soluciona el problema de las l�neas al renderizar
//		else if ( jugador.b2body.getPosition().x + (gamePort.getScreenWidth()/PixelGdx.PPM)/2  <  map.getProperties().get("width",Integer.class)*16/PixelGdx.PPM)
//			gameCam.position.x = Math.round( jugador.b2body.getPosition().x * 100f)/100f ; // Esto soluciona el problema de las l�neas al renderizar
		//gameCam.position.x = jugador.b2body.getPosition().x;
		//gameCam.position.y = jugador.b2body.getPosition().y;
		
		
		// GAME CAM 
		float mitadViewPortWidth = gameCam.viewportWidth / 2;
		float mitadViewPortHeight = gameCam.viewportHeight / 2;
		
		
		if (jugadorPrincipal.b2body.getPosition().x < mitadViewPortWidth)
			gameCam.position.x = mitadViewPortWidth;
		else if ( jugadorPrincipal.b2body.getPosition().x > 41.5f - mitadViewPortWidth )
			gameCam.position.x = gamePort.getWorldWidth() - mitadViewPortWidth;
		else
			gameCam.position.x = jugadorPrincipal.b2body.getPosition().x;

		if (jugadorPrincipal.b2body.getPosition().y < mitadViewPortHeight) {
			gameCam.position.y = mitadViewPortHeight;
		}else {
			gameCam.position.y = jugadorPrincipal.b2body.getPosition().y;
		}
		
		gameCam.update();
		//
//		
//		gameCam.viewportWidth = mitadViewPortWidth ;
//		gameCam.viewportHeight = mitadViewPortHeight ;
//		gameCam.update();
		
		// Le dice al renderer que pintar ( pinta lo que actualmente se esta viendo en pantalla )
		renderer.setView(gameCam);
		
	
	}
	

	@Override
	public void render(float delta) {
		// Actualiza las posiciones..etc
		update(delta);
		
		// Limpia la pantalla en negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Renderiza el mapa
        renderer.render();
        
        // DebugRender
        //b2debugRender.render(world, gameCam.combined);
        
        // TODO: Pinta el player y los enemigos.. 
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        jugadorPrincipal.draw(game.batch);
        jugadorDos.draw(game.batch);
        for( Serpiente s : serpientes )
			s.draw(game.batch);
        for (OndaVital ondaVital : ondas) {
			ondaVital.draw(game.batch);
		}
        game.batch.end();
        
        for( Serpiente s : serpientes )
			s.drawBarraVida(game.batch);
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		//updated our game viewport
        gamePort.update(width,height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		  //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2debugRender.dispose();
	}
	
	public World getWorld() {
		return world;
	}

}
