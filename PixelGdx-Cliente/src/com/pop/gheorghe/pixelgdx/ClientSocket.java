package com.pop.gheorghe.pixelgdx;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ClientSocket implements KeyListener{

	private Socket socket;
    private Scanner scanner;
    Posicion p = new Posicion();
    
  
    
    public ClientSocket() throws Exception {
    	
    	p.x = 3.0f;
    	System.out.println(" P: " +p.x);
    	
        this.socket = new Socket("10.2.23.1", 65000);
        this.scanner = new Scanner(System.in);
        
        System.out.println("\r\nConnected to: " + socket.getInetAddress());

        // Espera a leer objetos
        new Thread( new Runnable() {

        	@Override
        	public void run() {
        		// TODO Auto-generated method stub

        		ObjectInputStream is;
        		try {
        			while ( true )
        				if ( socket!= null ) {
        					is = new ObjectInputStream(socket.getInputStream());
        					Object o = is.readObject();
        					if ( o!= null ) {
        						Posicion pos = (Posicion)o;
        						x = pos;
        					}
        				}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}

        	}
        }).start();
        
    }
    
    public void mandarInfo ( Posicion posicion ) {
		// Envia la posicion
		ObjectOutputStream outStream;
		try {
			outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(posicion);

			//			        System.out.println(" p: " +p.x);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
   
    Posicion x = null;
	public Posicion posicionJugador ( ) {
		return x;
	}
    
    private void start() throws IOException {
        
        System.out.println("asdasdasda");
//        new Thread( new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				 while (true) {
//					 	String input;
//			            input = scanner.nextLine();
////			            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
////			            out.println(input);
////			            out.flush();
//			           
//			            ObjectOutputStream outStream;
//						try {
//							outStream = new ObjectOutputStream(socket.getOutputStream());
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//			            
//			            System.out.println(" p: " +p.x);
//			            
////			            outStream.writeObject(p);
//			            
//			            
//			        }
//			}
//		}).start();
       
    }

  
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
//		switch ( e.getKeyCode() ) {
//		case KeyEvent.VK_D:
//			p.key = Input.Keys.D;
//			break;
//		case KeyEvent.VK_A:
//			p.key = Input.Keys.A;
//			break;
//		case KeyEvent.VK_W:
//			p.key = Input.Keys.W;
//			break;
//		case KeyEvent.VK_S:
//			p.key = Input.Keys.S;
//			break;
//		case KeyEvent.VK_SPACE:
//			p.key = Input.Keys.SPACE;
//			break;
//		default:
//			p.key = -1;
//		}
//		
//		ObjectOutputStream outStream;
//		try {
//			outStream = new ObjectOutputStream(socket.getOutputStream());
//				System.out.println(e.getKeyChar());
//	        outStream.writeObject(p);
//
////	        System.out.println(" p: " +p.x);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
        
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    
    
}
