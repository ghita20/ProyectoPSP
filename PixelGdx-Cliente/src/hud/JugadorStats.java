package hud;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador;

public class JugadorStats {
	
	public Stage stage;
	private Viewport viewPort;
	
	// Atributos del jugador
	private Jugador jugador;
	
	// Texturas Vida
	private ArrayList<Cell> vidas;
	private ArrayList<Corazon> corazones; // En paralelo
	
	// Numero de corazones
	private final static int NUMERO_CORAZONES = 3;
	
	public JugadorStats( SpriteBatch sb , Jugador jugador ) {
		// TODO Auto-generated constructor stub
		this.jugador = jugador;
		
		// ViwePort y Stage
		viewPort = new FitViewport(PixelGdx.WIDTH , PixelGdx.HEIGHT , new OrthographicCamera());
		stage = new Stage(viewPort,sb);
		
		// Fuente
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/raleway.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 22;
		BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		
//		Label labelVida = new Label( String.valueOf("DEMO") , new Label.LabelStyle(font12, Color.DARK_GRAY));
//		table.add(labelVida).expandX().padTop(10);

		Table table = new Table();
		// Alineamiento de la tabla
		table.left().top();
		
		// Fill Parent
		table.setFillParent(true);
		
		// Debug
		//table.setDebug(true);
		
		// ArrayList vidas
		vidas = new ArrayList<>();
		corazones = new ArrayList<>();
		
		// Corazones
		table.row().left().top();
		for ( int i = 0 ; i < NUMERO_CORAZONES ; i++ ) {
			// Instancia cada corazon
			Corazon auxCorazon = new Corazon();
			corazones.add(auxCorazon);
			
			Image auxI = auxCorazon.getCorazon();
			if ( i == 0)
				vidas.add( table.add( auxI ).padTop(20).padLeft(20) );
			else
				vidas.add( table.add( auxI ).padTop(20) );
		}
		
		// Añade la tabla al stage
		stage.addActor(table);
	}
	
	public void dispose ( ) {
		stage.dispose();
		
	}
	
	public void restarVida ( ) {
		// Corazon actual
		int corazonActual = jugador.getVida()/4;
		//System.out.println("Corazon actual : " +corazonActual);
		
		if ( corazonActual >= 0 ) {
			// Selecciona el corazon
			Corazon auxCorazon = corazones.get(corazonActual);
			// Le resta un punto de vida
			auxCorazon.restarPunto();
			// Cambia la imagen a mostrar
			vidas.get(corazonActual).setActor(auxCorazon.getCorazon());
		}
	}
	
	public void refrescarStats ( ) {
		int numVidas = jugador.getVida();
		int vidasCorazones = 0;
		
		// COmpruebo si esta todo correcto con los corazones
		for ( int i = corazones.size()-1; i>=0 ; i-- ) 
			vidasCorazones += corazones.get(i).getVida();
		
		System.out.println("Vidas j : " +numVidas +" Vidas corazones : " +vidasCorazones);
		
		if ( numVidas == vidasCorazones ) return;
		// Resta las vidas necesarias para sincronizar los corazones con las vidas
		if ( numVidas < vidasCorazones ) {
			int iCorazon = 2; // Indice corazones
			for ( int i = vidasCorazones ; i > numVidas ; i-- ) {
				while ( corazones.get(iCorazon).getVida() == 0)
					--iCorazon;
					
				// Resta el punto de vida
				corazones.get(iCorazon).restarPunto();
				// Cambia la imagen
				vidas.get( iCorazon ).setActor( corazones.get(iCorazon).getCorazon());
				
			}
		}
		
	}

}
