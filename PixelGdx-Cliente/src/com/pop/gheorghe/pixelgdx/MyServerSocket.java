package com.pop.gheorghe.pixelgdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyServerSocket {
	private ServerSocket server;
	Socket client;

	public MyServerSocket() throws Exception{
		// TODO Auto-generated constructor stub
		server = new ServerSocket(50000, 1, InetAddress.getByName("localhost"));
		client = null;
		
		listen();
		
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				ObjectInputStream is;
				try {
					while ( true )
						if ( client!= null ) {
							is = new ObjectInputStream(client.getInputStream());
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
	
	private void listen() throws Exception {
		
		// TODO: que no tenga que esperar a que se conecte..
//		new Thread( new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					client = MyServerSocket.this.server.accept();
//					String clientAddress = client.getInetAddress().getHostAddress();
//					System.out.println("\r\nNew connection from " + clientAddress);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				
//				
//			}
//		}).start();
//		try {
//			client = MyServerSocket.this.server.accept();
//			String clientAddress = client.getInetAddress().getHostAddress();
//			System.out.println("\r\nNew connection from " + clientAddress);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
    }
	
	
	
	Posicion x = null;
	public Posicion posicionJugador ( ) {
		return x;
	}
	
	public void setX ( Posicion x ) {
		this.x = x;
	}
	
	public void mandarInfo ( Posicion posicion ) {
		// Envia la posicion
		ObjectOutputStream outStream;
		try {
			//System.out.println(client);
			if( client != null ) {
				outStream = new ObjectOutputStream(client.getOutputStream());
				outStream.writeObject(posicion);
				//System.out.println("Mandando al cliente");
			}

			//			        System.out.println(" p: " +p.x);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


//	public static void main(String[] args) throws Exception {
//		MyServerSocket srv = new MyServerSocket();
//		
//	}


}
