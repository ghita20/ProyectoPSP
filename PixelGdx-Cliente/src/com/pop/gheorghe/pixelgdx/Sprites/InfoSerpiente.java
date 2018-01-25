package com.pop.gheorghe.pixelgdx.Sprites;

import java.io.Serializable;
import com.badlogic.gdx.math.Vector2;

public class InfoSerpiente implements Serializable{
	public Vector2 posicion;
	public boolean dirDerecha;
	public int id;
	public float angle;
	
	public float stateTime;
	
	public boolean destruir;
	
	public float vida;
}
