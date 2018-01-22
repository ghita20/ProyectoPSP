package com.pop.gheorghe.pixelgdx;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.pop.gheorghe.pixelgdx.Sprites.Jugador.State;
import com.pop.gheorghe.pixelgdx.Sprites.InfoOndaVital;
import com.pop.gheorghe.pixelgdx.Sprites.OndaVital;
import com.pop.gheorghe.pixelgdx.Sprites.Serpiente;

public class Posicion implements Serializable{
	public float x;
	public int key;
	
	public Vector2 posicion;
	public float angle;
	
	public State state;
	public float stateTime;
	public boolean direccionDerecha;
	
	public ArrayList<InfoOndaVital> ondasVitales;
	
	
	

}
