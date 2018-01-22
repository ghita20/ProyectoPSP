package com.pop.gheorghe.pixelgdx.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.pop.gheorghe.pixelgdx.Pantallas.PantallaJugar;

public class Serpiente extends Enemigo {

	public Serpiente(PantallaJugar screen, float x, float y) {
		super(screen, x, y, "serpiente", 32, 32);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void cargarAnimacionMovimiento() {
		// TODO Auto-generated method stub
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int i = 0; i < 4; i++) {
			frames.add( new TextureRegion(getTexture(), i * 32 , 35, 32 ,32));
		}
		animacionMovimiento = new Animation(0.25f, frames);
	}

	@Override
	public TextureRegion getFrame(float dt) {
		// Tiempo += Delta
		tiempo += dt;
		return (TextureRegion) animacionMovimiento.getKeyFrame(tiempo,true);
	}

}
