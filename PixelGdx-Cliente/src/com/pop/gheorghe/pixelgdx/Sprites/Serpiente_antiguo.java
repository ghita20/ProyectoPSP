package com.pop.gheorghe.pixelgdx.Sprites;

import java.util.ArrayList;

import javax.swing.plaf.synth.SynthScrollBarUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Pantallas.PantallaJugar;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador.State;

public class Serpiente_antiguo extends Sprite {
	private World world;
	public Body b2body;
	
	// Animación de movimiento
    private Animation animacionMovimiento;
    private boolean direccionDerecha;
    
    // Estas variables las utilizo para dar un tiempo a que la serpiente pueda cambiar de dirección al chocar con algún límite
    private boolean cambiandoDireccion;
    private float tiempoCambioDireccion;
    
    // Si está dstruido o nop
    private boolean destruido;
    
    private float tiempo; // Tiempo para la animación
	private boolean destruir; // Centinela para destruir la serpiente después del world.step ( si se elimina antes genera una excepción )
	
	// Daño
	public static final int DAMAGE = 2;
	
	// Constructor
	public Serpiente_antiguo( PantallaJugar screen , float x , float y  ) {
		// Asigna como textura toda la región "serpiente" del atlas
		super(screen.geAtlas().findRegion("serpiente"));
		
		// World
		world = screen.getWorld();
		
		
		/// Se mueve hacia la derecha por defecto
		direccionDerecha = true;
		
		// Animación
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int i = 0; i < 4; i++) {
			frames.add( new TextureRegion(getTexture(), i * 32 , 35, 32 ,32));
		}
		animacionMovimiento = new Animation(0.25f, frames);
		
		// Define su cuerpo físico
		definir(x,y);
		
		// Tamaño en la pantalla
		setBounds(0, 0, 32 / PixelGdx.PPM, 32 / PixelGdx.PPM);
		// Primer keyFrame que se dibuja
		setRegion( (TextureRegion)animacionMovimiento.getKeyFrame(0f) );
		
		// Variables
		tiempo = 0f;
		destruido = false;
		destruir = false;
		
		// Cambio de direccion
		cambiandoDireccion = false;
		tiempoCambioDireccion = 0;
	}

	// Destruye la serpiente en el proximo update
	public void destruir ( ) {
		if ( !destruir ) 
			destruir = true;
	}
	
	public void update ( float dt ) {
		// Si no ha sido destruido aún comprueba si se quiere destruir
		if ( !destruido ) {
			// Tiempo de cambio de direccion
			if ( cambiandoDireccion ) {
				if ( tiempoCambioDireccion >= 0.05 ) cambiandoDireccion = false;
				tiempoCambioDireccion += dt;
				
			}
			if ( destruir ) {
				//System.out.println("Te destruyo pavo");
				world.destroyBody(b2body);
				destruido = true;
			} else {
				// Posición donde dibujará el frame = posició física - la mitad de lo que ocupa en pantalla ( para dibujar el frame en el medio )
				setPosition( b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
				// Asigna el frame 
				TextureRegion region = getFrame(dt);
				
				// Comprueba si tiene que invertir el frame
				if ( (b2body.getLinearVelocity().x < 0 || !direccionDerecha) && !region.isFlipX()) {
					region.flip(true, false);
					direccionDerecha = false;
				}else if ( (b2body.getLinearVelocity().x > 0 || direccionDerecha) && region.isFlipX() ) {
					region.flip(true, false);
					direccionDerecha = true;
				}
				
				// Asigna el frame a imprimir
				setRegion( region );
				
				// Mueve la serpiente
				mover();
			}
		}
	}
	
	// Movimiento de la serpiente en el mapa
	private void mover() {
		
		if ( (b2body.getLinearVelocity().x < 0.5f && direccionDerecha) || (b2body.getLinearVelocity().x > -0.5f && !direccionDerecha) )
			b2body.applyLinearImpulse(new Vector2( direccionDerecha?0.1f:-0.1f , 0 ), b2body.getWorldCenter(), true);	
	}
	
	public boolean cambiandoDeDireccion ( ) {
		return cambiandoDireccion;
	}
	
	public void setDireccionDerecha ( boolean estado ) {
		direccionDerecha = estado;
		b2body.setLinearVelocity(0, 0);
	}
	
	public void invertirDireccion ( ) {
		direccionDerecha = !direccionDerecha;
		b2body.setLinearVelocity(0, 0);
		cambiandoDireccion = true;
		tiempoCambioDireccion = 0;
		
	}
	
	public boolean getDireccionDerecha() {
		return direccionDerecha;
	}
	public TextureRegion getFrame ( float dt ) {
		// Tiempo += Delta
		tiempo += dt;
		return (TextureRegion) animacionMovimiento.getKeyFrame(tiempo,true);
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
		fdef.filter.groupIndex = -1; // Never collide
		
		
		CircleShape shapec = new CircleShape();
		shapec.setRadius( 20 / PixelGdx.PPM);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(8 / PixelGdx.PPM, 10  / PixelGdx.PPM);
		
		
		fdef.shape = shape;
		fdef.filter.categoryBits = PixelGdx.CATEGORIA_ENEMIGO;
		fdef.filter.maskBits = PixelGdx.MASK_ENEMIGO;
		b2body.createFixture(fdef).setUserData(this);

	}
	
	@Override
	public void draw(Batch batch) {
		// TODO Auto-generated method stub
		if ( !destruido )
			super.draw(batch);
	}
	
	
}
