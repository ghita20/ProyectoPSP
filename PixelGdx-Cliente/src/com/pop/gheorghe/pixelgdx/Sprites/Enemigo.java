package com.pop.gheorghe.pixelgdx.Sprites;

import java.awt.geom.RectangularShape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
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

public abstract class Enemigo extends Sprite{
	// World y body
	private World world;
	public Body b2body;
	
	// Animaciï¿½n de movimiento
    protected Animation animacionMovimiento;
    public boolean direccionDerecha;
    
    // Estas variables las utilizo para dar un tiempo a que al enemigo le de tiempo de cambiar de direcciï¿½n al chocar con algï¿½n lï¿½mite
    private boolean cambiandoDireccion;
    private float tiempoCambioDireccion;
    
    // Si estï¿½ dstruido o nop
    private boolean destruido;
    
    public float tiempo; // Tiempo para la animaciï¿½n
	private boolean destruir; // Centinela para destruir el enemigo despuï¿½s del world.step ( si se elimina antes genera una excepciï¿½n )
	
	// Daï¿½o
	public static final int DAMAGE = 2;
	
	// Identificador
	public int id;
	
	// Max Velocidad
	private float maxVelocidad;
	
	// vida
	private float vida;
	private Fixture barraVida;
	
	// Constructor
	public Enemigo( PantallaJugar screen , float x , float y , String region , float width , float height , int id , float maxVelocidad , float vida) {
		// Asigna como textura toda la regiï¿½n "serpiente" del atlas
		super(screen.geAtlas().findRegion(region));
		
		// ID
		this.id = id;
		
		// vida
		this.vida = vida;
		
		// Max Velocidad
		this.maxVelocidad = maxVelocidad;
		
		// World
		world = screen.getWorld();
		
		/// Se mueve hacia la derecha por defecto
		direccionDerecha = true;
		
		// Carga la animaciï¿½n de movimiento
		
		animacionMovimiento = null;
		cargarAnimacionMovimiento();
		
		// Define su cuerpo fï¿½sico
		definir(x,y);
		
		// Tamaï¿½o en la pantalla
		setBounds(0, 0, width / PixelGdx.PPM, height / PixelGdx.PPM);
		
		// Primer keyFrame que se dibuja
		if ( animacionMovimiento == null ) throw new IllegalStateException("No hay animaciï¿½n de movimiento.");
		setRegion( (TextureRegion)animacionMovimiento.getKeyFrame(0f) );
		
		// Variables
		tiempo = 0f;
		destruido = false;
		destruir = false;
		
		// Cambio de direccion
		cambiandoDireccion = false;
		tiempoCambioDireccion = 0;
	}
	
	protected abstract void cargarAnimacionMovimiento();
	public abstract TextureRegion getFrame ( float dt );

	// Destruye el enemigo en el proximo update
	public void destruir ( ) {
		if ( !destruir ) 
			destruir = true;
	}
	
	public void update ( float dt ) {
		// Si no ha sido destruido aï¿½n comprueba si se quiere destruir
		if ( !destruido ) {
			
			if ( destruir ) { // Esto es necesario porque solo se puede eliminar un Body justo después del world.step
				world.destroyBody(b2body);
				destruido = true;
			} else {
				// Posiciï¿½n donde dibujarï¿½ el frame = posiciï¿½ fï¿½sica - la mitad de lo que ocupa en pantalla ( para dibujar el frame en el medio )
				setPosition( b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
				// Asigna el frame 
				TextureRegion region = getFrame(dt);
				
				// Comprueba si tiene que invertir el frame
//				if ( (b2body.getLinearVelocity().x < 0 || !direccionDerecha) && !region.isFlipX()) {
//					region.flip(true, false);
//					direccionDerecha = false;
//				}else if ( (b2body.getLinearVelocity().x > 0 || direccionDerecha) && region.isFlipX() ) {
//					region.flip(true, false);
//					direccionDerecha = true;
//				}
				
				if ( (direccionDerecha && region.isFlipX()) || (!direccionDerecha && !region.isFlipX()) )
					region.flip(true, false);
				
				// Comprueba la velocidad
				if ( b2body.getLinearVelocity().x > maxVelocidad ) {
					b2body.setLinearVelocity(new Vector2(0,b2body.getLinearVelocity().y));
				}else if ( b2body.getLinearVelocity().x < 0 ) {
					if ( b2body.getLinearVelocity().x*-1 > maxVelocidad )
						b2body.setLinearVelocity(new Vector2(0,b2body.getLinearVelocity().y));
				}
				
				// Asigna el frame a imprimir
				setRegion( region );
				
				// Mueve el enemigo
				mover();
			}
			
			// Tiempo de cambio de direccion
			if ( cambiandoDireccion ) {
				if ( tiempoCambioDireccion >= 0.05 ) 
					cambiandoDireccion = false;
				tiempoCambioDireccion += dt;
			}
		}
	}
	
	// Movimiento basico del enemigo
	protected void mover() {
		if ( (b2body.getLinearVelocity().x < 0.5f && direccionDerecha) || (b2body.getLinearVelocity().x > -0.5f && !direccionDerecha) )
			b2body.applyLinearImpulse(new Vector2( direccionDerecha?0.1f:-0.1f , 0 ), b2body.getWorldCenter(), true);	
	}
	
	public boolean cambiandoDeDireccion ( ) {
		return cambiandoDireccion;
	}
	
	public void setDireccionDerecha ( boolean estado ) {
		direccionDerecha = estado;
		b2body.setLinearVelocity(0, 0);
		invertirDireccion();
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
	
	public float getVida ( ) {
		return vida;
	}
	
	private void definir ( float x , float y) {
		BodyDef bdef = new BodyDef();
		bdef.position.set(x , y );
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		// Crea el body
		b2body = world.createBody(bdef);
		
		// Filtro para las colisiones..etc
		FixtureDef fdef = new FixtureDef();
		fdef.friction = 0.3f; // Hace parar un poco el impulso
		fdef.filter.groupIndex = -1; // Never collide
		
		// Forma
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
		if ( !destruido ) {
			super.draw(batch);
			batch.end();
			
			// TODO: Pintar mejor la barra de vida
			ShapeRenderer sh = new ShapeRenderer();
			sh.setProjectionMatrix(batch.getProjectionMatrix());
			sh.begin(ShapeType.Filled);
			sh.setColor(Color.RED);
			sh.rect( b2body.getPosition().x - 0.05f  , b2body.getPosition().y +  10 / PixelGdx.PPM, (vida/10 / PixelGdx.PPM), 3 / PixelGdx.PPM);
			sh.end();
			
			batch.begin();
		
		}
	}
	public void drawBarraVida(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
		
	}
	
	public void atacar ( float damage ) {
		vida -= damage;
		if ( vida == 0 )
			destruir();
	}
	
	public boolean getDestruido ( ) {
		return destruido;
	}
	
	public void setVida ( float vida ) {
		this.vida = vida;
	}
	

}
