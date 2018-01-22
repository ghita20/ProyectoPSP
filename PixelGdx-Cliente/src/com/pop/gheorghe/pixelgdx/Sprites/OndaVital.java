package com.pop.gheorghe.pixelgdx.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Pantallas.PantallaJugar;

public class OndaVital extends Sprite{
	private World world;
	public Body b2body;
	
    private Animation animacionMovimiento;
    public boolean direccionDerecha;
    private Jugador jugador;
    
    private float y;
    public float tiempo;
	
	public OndaVital( PantallaJugar screen , float x , float y, boolean dirDrcha, Jugador jugador ) {
		// TODO Auto-generated constructor stub
		super(screen.geAtlas().findRegion("onda"));
		world = screen.getWorld();
		
		/// State
		direccionDerecha = dirDrcha;
		this.y = y;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int i = 0; i < 4; i++) {
			frames.add( new TextureRegion(getTexture(), i * 32 , 103, 32 ,32));
		}
		animacionMovimiento = new Animation(0.15f, frames);
		
		
		
		setBounds(0, 0, 32 / PixelGdx.PPM, 32 / PixelGdx.PPM);
		setRegion( (TextureRegion)animacionMovimiento.getKeyFrame(0f) );
		definir(x,y);
		tiempo = 0f;
		this.jugador = jugador;
		if ( dirDrcha )
			b2body.applyForceToCenter(new Vector2(30f, 0),true);
		else {
			b2body.applyForceToCenter(new Vector2(-30f, 0),true);
		}
		
		//System.out.println(" Posicionb actual : x = " +b2body.getPosition().x +" y = " +b2body.getPosition().y);
	}

	public Jugador getJugador ( ) {
		return jugador;
	}

	public void update ( float dt ) {
		//System.out.println("Tiempo : " +tiempo +" X = " + b2body.getPosition().x);
		b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
		setPosition( b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2);
		// Asigna el frame segun el estado del personaje
		TextureRegion region = getFrame(dt,0);
		if ( !direccionDerecha && !region.isFlipX())
			region.flip(true, false);
		else if ( direccionDerecha && region.isFlipX() )
			region.flip(true, false);
		setRegion( region );

	}
	// Remote
	public void update ( float dt , float time) {
		//System.out.println("Tiempo : " +tiempo +" X = " + b2body.getPosition().x);
		b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
		setPosition( b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2);
		// Asigna el frame segun el estado del personaje
		TextureRegion region = getFrame(dt,time);
		if ( !direccionDerecha && !region.isFlipX())
			region.flip(true, false);
		else if ( direccionDerecha && region.isFlipX() )
			region.flip(true, false);
		setRegion( region );

	}

	public TextureRegion getFrame ( float dt , float time ) {
		if ( time == 0)
			tiempo += dt;
		else
			tiempo = time;
		return (TextureRegion) animacionMovimiento.getKeyFrame(tiempo,false);
	}

	private void definir ( float x , float y) {
		BodyDef bdef = new BodyDef();
		bdef.position.set(x , y );
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		

		FixtureDef fdef = new FixtureDef();
		fdef.friction = 0.3f; // Hace parar un poco el impulso
		//CircleShape shape = new CircleShape();
		//shape.setRadius(15 / PixelGdx.PPM);
		fdef.friction = 1;
		fdef.density = 10;
		
		
		CircleShape shapec = new CircleShape();
		shapec.setRadius( 20 / PixelGdx.PPM);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(8 / PixelGdx.PPM, 10  / PixelGdx.PPM);
		
		
		fdef.shape = shape;
		fdef.filter.categoryBits = PixelGdx.CATEGORIA_PODERES;
		fdef.filter.maskBits = PixelGdx.MASK_PODERES;
		b2body.createFixture(fdef).setUserData(this);

	}
	
	public boolean finAnimacion ( ) {
		return animacionMovimiento.isAnimationFinished(tiempo);
	}
	
	@Override
	public void draw(Batch batch) {
		// TODO Auto-generated method stub
		super.draw(batch);
	}

}
