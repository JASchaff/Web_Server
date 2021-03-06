import java.net.*;
import java.util.*;

public class PingServer {
	private static final double LOSS_RATE=0.25;
	private static final int AVERAGE_DELAY=150;//milliseconds
	

	public static void main(String[] args) throws Exception{
		
		Integer port = 0;
		Long seed=0L;
		//checks number of args
		if(args.length<1){
			System.err.println("ERR-arg 1");
			return;
		}
		//parses arg 1 for port
		try{
			port=Integer.parseInt(args[0]);
			
		} catch(NumberFormatException e){ 
			System.err.println("ERR-arg 1");
			return;
		}
		
		//checks for range
		if(port>11000 || port<10001){
			System.err.println("Err-arg 1");
			return;
		}
		if(args.length>1){
			int s=0;
			try{
				s=Integer.parseInt(args[1]);
			} catch(NumberFormatException e){
				System.err.println("ERR-arg 2");
				return;
			}
			seed=Long.parseLong(args[1]);
		}
		//initiates seed to current time
		else{
			seed=System.currentTimeMillis();
		}
		//initiates random engine
		Random rengine=new Random(seed);
		//open socket
		DatagramSocket socket= new DatagramSocket(port);
		//while statement runs until JVM killed by Ctrl-c
		while(true){
			//byte array for incoming packets
			byte[] inbuffer=new byte[400];
			//packet for incoming packet
			DatagramPacket inmess=new DatagramPacket(inbuffer, inbuffer.length);
			//attempt to receive PING message
			socket.receive(inmess);
			//pull message from packet
			byte[] x = inmess.getData();
			//translate to string
			String message= new String(x);
			//pull IP
			InetAddress cADD= inmess.getAddress();
			//pull PORT
			Integer cport= inmess.getPort();
			//print address and port
			System.out.print(inmess.getAddress().getHostAddress()+"> "+message+" ");
			if(rengine.nextDouble()<LOSS_RATE){
				System.out.println("ACTION: not sent");
			}
			else{
				//packet for outgoing packet
				DatagramPacket outmess= new DatagramPacket(x,x.length, cADD, cport);
				//set delay
				int delay=(int)(rengine.nextDouble()*2*AVERAGE_DELAY);
				//wait for time delay
				Thread.sleep(delay);
				//resend packet PING
				socket.send(outmess);
				//print ACTION
				System.out.println("ACTION: delayed "+delay+" ms");
			}
		
		}
	}
}


