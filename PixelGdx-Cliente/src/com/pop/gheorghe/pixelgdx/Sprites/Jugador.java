package com.pop.gheorghe.pixelgdx.Sprites;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Pantallas.PantallaJugar;

public class Jugador extends Sprite {
	private PantallaJugar screen;
	private World world;
	public Body b2body;
	private TextureRegion standTexture;
	
	public enum State {
		SALTANDO, CAYENDO, CORRIENDO , MUERTO , STANDING, ATACANDO
	}
	public State currentState;
    public State previousState;
    private Animation jugadorCorriendo;
    private Animation jugadorSaltando;
    private Animation jugadorAtacando;
    private boolean atacando;
    
    public float stateTimer;
    private boolean direccionDerecha;
    private static final float VELOCIDAD = 4;
    
    // Cuerpo de la espada
    private Fixture espada;
    
    private int vida;
	
	public Jugador( PantallaJugar screen , String region , int regionY ) {
		// TODO Auto-generated constructor stub  - rey
		super(screen.geAtlas().findRegion(region));
		world = screen.getWorld();
		this.screen = screen;
		
		/// State
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		direccionDerecha = true;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int i = 0; i < 4; i++) { // new TextureRegion(getTexture(), i * 32 , 69, 32 ,32)
			frames.add( new TextureRegion(getTexture(), i * 32 , regionY, 32 ,32));
		}
		jugadorCorriendo = new Animation(0.1f, frames);
		frames.clear();
		
		
		for (int i = 4; i < 7; i++) {
			frames.add( new TextureRegion(getTexture(), i * 32 , regionY, 32 ,32));
		}
		jugadorSaltando = new Animation(0.2f, frames);
		frames.clear();
		
		// Atacar
		frames.add( new TextureRegion(getTexture(), 8 * 32 , regionY, 32 ,32));
		frames.add( new TextureRegion(getTexture(), 10 * 32 , regionY, 32 ,32));
		frames.add( new TextureRegion(getTexture(), 9 * 32 , regionY, 32 ,32));
		jugadorAtacando = new Animation(0.1f, frames);
		
		
		// Textura normal
		standTexture = new TextureRegion( getTexture(), 1, regionY , 32 ,32);
		
		definirJugador();
		
		setBounds(0, 0, 34 / PixelGdx.PPM, 34 / PixelGdx.PPM);
		setRegion(standTexture);
		
		vida = 100;
		
	}
	
	public int getVida() {
		return vida;
	}
	
	public void movimientoJugador (  ) {

		if (Gdx.input.isKeyJustPressed(Input.Keys.W)  ) { // && jugador.getState() != State.SALTANDO && jugador.getState() != State.CAYENDO
			saltar();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.S))
			b2body.applyLinearImpulse(new Vector2(0, -2.4f), b2body.getWorldCenter(), true);
		if (Gdx.input.isKeyPressed(Input.Keys.D) && b2body.getLinearVelocity().x <= 1.5f) {
			float velX = b2body.getLinearVelocity().x;
			if ( velX < 0 )
				b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
			b2body.applyForceToCenter(new Vector2(3.5f, 0), true);
			
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A) && b2body.getLinearVelocity().x >= -1.5f) {
			float velX = b2body.getLinearVelocity().x;
			if ( velX > 0 )
				b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
			b2body.applyForceToCenter(new Vector2(-3.5f, 0), true);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			//atacar();
			//System.out.println("Voy a crear en posicion: x: " +b2body.getPosition().x +" y: " +b2body.getPosition().y);
			
			screen.crearOndaVital(this);
		
		}
	}

	public void movimientoJugador ( int key ) {
		System.out.println("key: " +key);
		if ( key == Input.Keys.W  ) { // && jugador.getState() != State.SALTANDO && jugador.getState() != State.CAYENDO
			saltar();
		}
		if (key == Input.Keys.S)
			b2body.applyLinearImpulse(new Vector2(0, -2.4f), b2body.getWorldCenter(), true);
		if (key == Input.Keys.D && b2body.getLinearVelocity().x <= 1.5f) {
			float velX = b2body.getLinearVelocity().x;
			if ( velX < 0 )
				b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
			b2body.applyForceToCenter(new Vector2(7.5f, 0), true);
			
		}
		if (key == Input.Keys.A && b2body.getLinearVelocity().x >= -1.5f) {
			float velX = b2body.getLinearVelocity().x;
			if ( velX > 0 )
				b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
			b2body.applyForceToCenter(new Vector2(-7.5f, 0), true);
		}
		
		if (key == Input.Keys.SPACE) {
			//atacar();
			//System.out.println("Voy a crear en posicion: x: " +b2body.getPosition().x +" y: " +b2body.getPosition().y);
			
			screen.crearOndaVital(this);
		
		}
	}
	
	public void update ( float dt ) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 );
		// Asigna el frame segun el estado del personaje
		setRegion( getFrame(dt) );
	}
	// Remoto
	public void update ( float dt , TextureRegion textura ) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 );
		// Asigna el frame segun el estado del personaje
		setRegion( textura );
	}
	// Remoto
	public TextureRegion getFrame ( float dt , State state , float stateTimer , boolean direccionDerecha ) {
		currentState = state;
		//System.out.println(currentState.toString());
		
		TextureRegion region = null;
		switch( currentState ) {
		case CORRIENDO: 
			region = (TextureRegion) jugadorCorriendo.getKeyFrame(stateTimer, true);
			break;
		case STANDING: case CAYENDO:
			region = standTexture;
			break;
		case SALTANDO:
			region = (TextureRegion) jugadorSaltando.getKeyFrame(stateTimer);
			break;
		case ATACANDO:
			region = (TextureRegion) jugadorAtacando.getKeyFrame(stateTimer);
			break;
		}
		
		
		if ( !direccionDerecha && !region.isFlipX()) {
			region.flip(true, false);
			this.direccionDerecha = false;

		}else if ( direccionDerecha && region.isFlipX() ) {
			region.flip(true, false);
			this.direccionDerecha = true;
		}
			
		stateTimer = currentState == previousState ? stateTimer + dt : 0; 
		previousState = currentState;
		return region;
	}
	
	public TextureRegion getFrame ( float dt ) {
		currentState = getState();
		//System.out.println(currentState.toString());
		
		TextureRegion region = null;
		switch( currentState ) {
		case CORRIENDO: 
			region = (TextureRegion) jugadorCorriendo.getKeyFrame(stateTimer, true);
			break;
		case STANDING: case CAYENDO:
			region = standTexture;
			break;
		case SALTANDO:
			region = (TextureRegion) jugadorSaltando.getKeyFrame(stateTimer);
			break;
		case ATACANDO:
			region = (TextureRegion) jugadorAtacando.getKeyFrame(stateTimer);
			break;
		}
		
		
		if ( (b2body.getLinearVelocity().x < 0 || !direccionDerecha) && !region.isFlipX()) {
			region.flip(true, false);
			direccionDerecha = false;

		}else if ( (b2body.getLinearVelocity().x > 0 || direccionDerecha) && region.isFlipX() ) {
			region.flip(true, false);
			direccionDerecha = true;
		}
			
		stateTimer = currentState == previousState ? stateTimer + dt : 0; 
		previousState = currentState;
		return region;
	}
	
	public State getState() {
		if ( atacando  ) {
			System.out.println("Aquiiiiiiiiiiiiiiiiiiiiiii" + jugadorAtacando.isAnimationFinished(stateTimer));
			if ( !jugadorAtacando.isAnimationFinished(stateTimer) )
				return State.ATACANDO;
			else {
				atacando = false;
			}
		}
		if ( b2body.getLinearVelocity().y > 0 || ( b2body.getLinearVelocity().y >0 && previousState == State.SALTANDO) ) // Continua con la animacion de salto
			return State.SALTANDO;
		if ( b2body.getLinearVelocity().y < 0 )
			return State.CAYENDO;
		if ( b2body.getLinearVelocity().x != 0 )
			return State.CORRIENDO;
		return State.STANDING;
	}

	public void setState ( State state ) {
		previousState = currentState;
		currentState = state;
	}
	private void definirJugador () {
		BodyDef bdef = new BodyDef();
		bdef.position.set(100 / PixelGdx.PPM, 220 / PixelGdx.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		fdef.friction = 0.3f; // Hace parar un poco el impulso
		//CircleShape shape = new CircleShape();
		//shape.setRadius(15 / PixelGdx.PPM);
		
		CircleShape shapec = new CircleShape();
		shapec.setRadius( 20 / PixelGdx.PPM);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(8 / PixelGdx.PPM, 11  / PixelGdx.PPM);
		
		
		fdef.shape = shape;
		fdef.filter.categoryBits = PixelGdx.CATEGORIA_JUGADOR;
		fdef.filter.maskBits = PixelGdx.MASK_JUGADOR;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape shapeEspada = new EdgeShape();
		shapeEspada.set(new Vector2( -30 / PixelGdx.PPM, 10 / PixelGdx.PPM), new Vector2(30 / PixelGdx.PPM, 10 / PixelGdx.PPM));
        fdef.shape = shapeEspada;
        fdef.isSensor = true;
        
        espada = b2body.createFixture(fdef);
        espada.setUserData("espada");

	}
	public boolean enDireccionDerecha ( ) {
		return direccionDerecha;
	}
	
	@Override
	public void draw(Batch batch) {
		// TODO Auto-generated method stub
		super.draw(batch);
	}
	
	public void saltar() {
		// TODO Auto-generated method stub
		//if ( currentState != State.SALTANDO ) {

			//if ( b2body.getPosition().y < PixelGdx.HEIGHT / PixelGdx.PPM) {
	            //b2body.applyLinearImpulse(new Vector2(0, 2.9f), b2body.getWorldCenter(), true);
	            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
	            b2body.applyForceToCenter(0, 150f, true);
	            //currentState = State.SALTANDO;
			//}
            
        //}
	}
	
	public void herir ( Serpiente enemigo) {
		if ( vida > 0 )
			vida -= enemigo.DAMAGE;
		b2body.applyForceToCenter( enemigo.getDireccionDerecha() ? 100f : -100f , 55f, true);
		System.out.println("Hiriendo");
	}
	
	public void atacar() {
		// TODO Auto-generated method stub
		System.out.println("Enn");
		Array<Fixture> enRango = new Array<Fixture>();
		world.getFixtures(enRango);
		
		for( Fixture f : enRango ) {
			if ( f.getUserData() instanceof Serpiente ) {
				Serpiente serpiente = (Serpiente) f.getUserData();
				// TODO: 
				if ( serpiente.b2body.getPosition().x <= b2body.getPosition().x + ( direccionDerecha ? 0.5f : -0.5f )
						&& ( serpiente.b2body.getPosition().y <= b2body.getPosition().y +0.5f && serpiente.b2body.getPosition().y >= b2body.getPosition().y -0.5f)  ) {
					System.out.println("En ragoooo MATAKLAOOASDOSdpa Y: " +b2body.getPosition().y +" Y serpiente: " +serpiente.b2body.getPosition().y);
					serpiente.destruir();
				}
			}
		}
		atacando = true;
		stateTimer = 0;
		
		
	}

}
