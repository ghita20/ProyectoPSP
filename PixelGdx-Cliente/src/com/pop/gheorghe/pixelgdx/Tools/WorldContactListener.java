package com.pop.gheorghe.pixelgdx.Tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador;
import com.pop.gheorghe.pixelgdx.Sprites.OndaVital;
import com.pop.gheorghe.pixelgdx.Sprites.Serpiente;

public class WorldContactListener implements ContactListener {

	private float velocidadOndaVital; // Para que al chocar con enemigos mantenga su velocidad de impulso
	private boolean isServer;
	
	public WorldContactListener( boolean isServer ) {
		// TODO Auto-generated constructor stub
		this.isServer = isServer;
	}
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		if ( isServer ) {
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();
			if ( fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
				//System.out.println( " A: " +fixtureA.getUserData().toString() +" B: " + fixtureB.getUserData().toString());

				if ( fixtureA.getFilterData().categoryBits == PixelGdx.CATEGORIA_ESPADA && fixtureB.getUserData() instanceof Serpiente ) {
					//System.out.println("Espada y serpiente fa");
				}
			}
			// Contactos
			int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
			switch ( cDef ) {
			// Choque entre entre poder y enemigo
			case PixelGdx.CATEGORIA_PODERES | PixelGdx.CATEGORIA_ENEMIGO :
				if ( isServer ) {
					Serpiente serpiente = null;
					if ( fixtureA.getUserData() instanceof Serpiente ) 
						serpiente = (Serpiente) fixtureA.getUserData();
					if ( fixtureB.getUserData() instanceof Serpiente ) 
						serpiente = (Serpiente) fixtureB.getUserData();

					// Daña al enemigo etc..
					
						serpiente.atacar(Jugador.DAMAGE);
					
					System.out.println("vida : " +serpiente.getVida());
				}
				break;

				// Choque entre jugador y enemigo
			case PixelGdx.CATEGORIA_JUGADOR | PixelGdx.CATEGORIA_ENEMIGO :
				Jugador jugador = null;
				Serpiente auxSerpiente = null;

				if ( fixtureA.getUserData() instanceof Jugador ) {
					jugador = (Jugador) fixtureA.getUserData();
					auxSerpiente = (Serpiente) fixtureB.getUserData();
				}
				if ( fixtureB.getUserData() instanceof Jugador ) {
					jugador = (Jugador) fixtureB.getUserData();
					auxSerpiente = (Serpiente) fixtureA.getUserData();
				}

				if ( jugador != null )
					jugador.herir(auxSerpiente);
				break;

			}
		}

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		if ( isServer ) {
			Object fixA = contact.getFixtureA().getUserData() , 
					fixB = contact.getFixtureB().getUserData();
			Serpiente auxSerpiente = null;
			OndaVital auxOnda = null ;

			//System.out.println("A = " +fixA +" B = " +fixB);
			if ( fixA instanceof OndaVital && fixB instanceof Serpiente  ) {
				auxSerpiente = (Serpiente)fixB;
				auxOnda = (OndaVital)fixA;
			}else if ( fixA instanceof Serpiente && fixB instanceof OndaVital ) {
				auxSerpiente = (Serpiente)fixA;
				auxOnda = (OndaVital)fixB;
			}
			if ( auxOnda == null || auxOnda.haTocadoEnemigo ) return;


			boolean direccionDerecha = auxOnda.direccionDerecha;

			auxSerpiente.b2body.applyForceToCenter( (float)(Math.random()* (direccionDerecha?100f:-100f) ) , 50f, true);
			auxOnda.haTocadoEnemigo = true;

			velocidadOndaVital = auxOnda.b2body.getLinearVelocity().x; // Almacena la velocidad antes del impacto
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Serpiente auxS = null;
		OndaVital auxJ = null ;

		int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

		// 
		switch ( cDef ) {
		// Choque entre limite y enemigo
		case PixelGdx.CATEGORIA_LIMITE | PixelGdx.CATEGORIA_ENEMIGO :
			// Capturo la Serpiente
			if ( fixtureA.getUserData() instanceof Serpiente ) 
				auxS = (Serpiente) fixtureA.getUserData();
			if ( fixtureB.getUserData() instanceof Serpiente ) 
				auxS = (Serpiente) fixtureB.getUserData();
			// Cambia de dirección la serpiente
			if ( !auxS.cambiandoDeDireccion() )
				auxS.invertirDireccion();

			break; // Fin Limite y Enemigo

		case PixelGdx.CATEGORIA_PODERES | PixelGdx.CATEGORIA_ENEMIGO :
			if ( fixtureA.getUserData() instanceof OndaVital ) 
				auxJ = (OndaVital) fixtureA.getUserData();
			if ( fixtureB.getUserData() instanceof OndaVital ) 
				auxJ = (OndaVital) fixtureB.getUserData();
			// Mantiene la velocidad de la ondaVital
			auxJ.b2body.setLinearVelocity(new Vector2( velocidadOndaVital,0));
			break;

		}



	}

}
