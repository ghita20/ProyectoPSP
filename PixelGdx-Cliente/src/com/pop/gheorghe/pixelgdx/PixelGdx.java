package com.pop.gheorghe.pixelgdx;

import java.net.Socket;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pop.gheorghe.pixelgdx.Pantallas.PantallaJugar;

public class PixelGdx extends Game {
	
	// Dimensiones etc..
	public static final int WIDTH = 1240;
	public static final int HEIGHT = 620;
	public static final float PPM = 100f;
	
	// CategoryBits - para las colisiones
	public static final short CATEGORIA_JUGADOR = 0x0001;
	public static final short CATEGORIA_ENEMIGO = 0x0002;
	public static final short CATEGORIA_ESCENARIO = 0x0004;
	public static final short CATEGORIA_PODERES = 0x0008;
	public static final short CATEGORIA_LIMITE = 0x0016;
	public static final short CATEGORIA_ESPADA = 0x0032;
	
	// Masks - colisiones
	public static final short MASK_JUGADOR = CATEGORIA_ENEMIGO | CATEGORIA_ESCENARIO;
	public static final short MASK_ENEMIGO = CATEGORIA_JUGADOR | CATEGORIA_ESCENARIO | CATEGORIA_PODERES ;
	public static final short MASK_PODERES = CATEGORIA_ENEMIGO;
	public static final short MASK_ESCENEARIO = -1;
	public static final short MASK_LIMITES = CATEGORIA_ENEMIGO;
	
	
	// Batch - draw
	public SpriteBatch batch;
	public boolean isServer;
	public MyServerSocket sock ;
	public ClientSocket sockClient;
	
	public PixelGdx(int mode) {
		// TODO Auto-generated constructor stub
		super();
		isServer = mode == 0;
			
	}
	
	@Override
	public void create () {
		if ( isServer ) {
			// Socket Server
			try {
				sock = new MyServerSocket();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			// Socket Cliente
			try {
				sockClient = new ClientSocket();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		batch = new SpriteBatch();
		// Pantalla Jugar
		setScreen( new PantallaJugar( this ) );
	}
	
	

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
