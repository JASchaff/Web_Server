import java.io.*;
import java.net.*;
import java.text.DecimalFormat;


public class PingClient {

	public static void main(String[] args) throws Exception{
		//checks for extra or missing arguments
				if(args.length< 1){
					System.err.println("ERR-arg 1");
					return;
				}
		String farg=args[0]; //first argument string server name
		int sarg=0; //second argument port number
		//check first arg viable
		if(!(farg.equalsIgnoreCase("atria"))&& !(farg.equalsIgnoreCase("sirius"))){
			System.err.println("ERR-arg 1");
			return;
		}
		//checks for extra or missing arguments
				if(args.length!= 2){
					System.err.println("ERR-arg 2");
					return;
				}
		//parse second arg to integer
		try{
			sarg=Integer.parseInt(args[1]);
		} catch (NumberFormatException e){//if it doesn't parse, not viable
			System.err.println("Err-arg 2");
			return;
		}
		//checks for range
		if(sarg>11000 || sarg<10001){
			System.err.println("Err-arg 2");
			return;
		}
		
		InetAddress Pserver;
		//gets host IP by name
		try{
		Pserver= InetAddress.getByName(farg);
		} catch(UnknownHostException e){
			System.err.println("ERR-Unknown Host Exception");
			return;
		}
		
		
		
		//connects Socket by port number
		
		 DatagramSocket	socket = new DatagramSocket(sarg);
		//statistic trackers
		Integer psent=0;
		Integer preceived=0;
		Integer rttmin=0;
		Integer rttmax=0;
		Double rttavg=0.0;
		//loop for sending packets
		for(int i=0; i<10; i++){
			Long stime= System.currentTimeMillis();
			//build string to send
			String message="PING "+ i+" "+ stime;
			//convert to byte array
			byte[] tosend=message.getBytes();
			//build packet
			DatagramPacket spac=new DatagramPacket(tosend, tosend.length, Pserver, sarg); 
			//send packet
			try{
			socket.send(spac);
			System.out.print(message+" ");
			psent++;
			}catch(IOException e){
				System.err.println("Err-IO exception, packet "+ i +" not sent\n");
				continue;
			}
			//byte array for message
			byte[] torec=new byte[400];
			//build receiving datagramPacket
			DatagramPacket rpac= new DatagramPacket(torec, torec.length);
			try{
				//set timeout
				socket.setSoTimeout(2000);
				//try to receive packet
				socket.receive(rpac);
				Long rtime=System.currentTimeMillis();
				Long RTT= rtime-stime;
				System.out.println("RTT: "+RTT+" ms");
				preceived++;
				if(RTT<rttmin||rttmin==0){
					rttmin=RTT.intValue();
				}
				if(RTT>rttmax){
					rttmax=RTT.intValue();
				}
				rttavg+=RTT;
			}catch(IOException e){
				System.out.println("RTT: *");
				continue;
			}
			
		}
		DecimalFormat df= new DecimalFormat("###0.00");
		if(preceived>0){
		rttavg=(rttavg)/preceived;
		}
		float pLoss= (100.0f-((100.0f*preceived)/psent));
		System.out.println("---- PING Statistics ----");
		System.out.println(psent+" packets transmitted, "+preceived+" packets received, "+Math.round(pLoss)+"% packet loss");
		System.out.println("round-trip (ms) min/avg/max= "+rttmin+"/"+df.format(rttavg)+"/"+rttmax); 
		
		if(!socket.isClosed()){
			socket.close();
		}
	}
}
