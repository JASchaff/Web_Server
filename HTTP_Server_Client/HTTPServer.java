import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class HTTPServer {
	private static final double LOSS_RATE=0.25;
	private static final int AVERAGE_DELAY=150;//milliseconds
	

	public static void main(String[] args) throws Exception{
		Integer port =0;
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
		if(port>65535){
			System.err.println("ERR-arg 1");
			return;
		}
		if(port>11000 || port<10001){
			System.err.println("ERR-arg 1");
			return;
		}
		//creates listen socket
		ServerSocket listen=new ServerSocket(port);
		while(true){
		//once someone contacts, you get a new socket for that contact
			Socket s=listen.accept();
			s.setSoTimeout(10000000);
			//create output stream
				PrintWriter send= new PrintWriter(s.getOutputStream());
			//create input stream
				BufferedReader rcv= new BufferedReader(new InputStreamReader(s.getInputStream()));
			//pull the first line of the request
				String fline=rcv.readLine();
			//String Vector to hold the lines of the request
				Vector<String> Rheader = new Vector<String>();
			//add the first string to Rheader Vector
				Rheader.add(fline);
				//get the IP address of the client
				InetAddress ClientIP=s.getInetAddress();
				String Caddy=ClientIP.toString();
				Caddy=Caddy.substring(1);
				//get the port of the client
				Integer Cport=s.getPort();
				//if the request code is GET
/////////////////
				if(fline.startsWith("GET")){
				//print the IP address with the port and request code
					System.out.println(Caddy+":"+Cport+":GET");
				//put the rest of the GET request into Rheader
					for(String line=rcv.readLine(); !line.equals(""); line=rcv.readLine()){
						Rheader.add(line);
						System.out.println(line);
					}
				//find the index of the space after the PATH
					int space=fline.indexOf(" ", 4);
				//parse out the PATH
					String filename=fline.substring(5, space);
				//whether or not the requested file is found, will be determined later
					Boolean found=true;
				//Vector to store the outgoing file
					Vector<String> OutFile=new Vector<String>();
					
					Long Fsize=0L;
					Long Lastmod=0L;
					
				//try to get the length and last modified date of file
					File check= new File(filename);
					if(!check.exists()||!check.isFile()){
							found=false;
					}
					Fsize=check.length();
					Lastmod=check.lastModified();

					
					
				//Tries to find the file and store it in OutFile
					try{
						BufferedReader Freader=new BufferedReader(new FileReader(filename));
						for(String line=Freader.readLine();line!=null; line=Freader.readLine()){
							OutFile.add(line);
						}
						Freader.close();
					}catch(FileNotFoundException e){
						found=false;
					}
				//write header
				String header= GETheader(filename, found, Fsize, Lastmod );
				//write response
				StringBuilder sb=new StringBuilder(Fsize.intValue()+1000);
				sb.append(header);
				//if file was found append file
				if(found){
					for(int i=0; i<OutFile.size();i++){
						sb.append(OutFile.get(i));
					}
				}
				String response=sb.toString();
				//send response
				send.print(response);
				send.flush();

				}
/////////////////if the request code is PUT
				else if(fline.startsWith("PUT")){
					//print the IP address with the port and request code
					System.out.println(Caddy+":"+Cport+":PUT");
					//pull out the header
					System.out.println(fline);
					for(String line=rcv.readLine(); !line.equals(""); line=rcv.readLine()){
							Rheader.add(line);
							System.out.println(line);
					}
					
					int i=fline.indexOf(" ", 4);
					
					String filename=fline.substring(5, i);
					File file = new File(filename);
					if(!file.isFile()){
						send.print("HTTP/1.0 606 FAILED File NOT Created\r\nServer: HTTP Server\r\n\r\n");
						send.flush();
					}
					else{
						file.createNewFile();
						send.print("HTTP/1.0 200 OK File Created\r\nServer: HTTP Server\r\n\r\n");
						send.flush();
					
					
					BufferedWriter outfile= new BufferedWriter(new FileWriter(filename));
					//writes the rest of the received message to file
							for(String line = rcv.readLine(); line!=null; line=rcv.readLine()){
								outfile.write(line);
							}
					//close outfile
							outfile.close();
							s.shutdownInput();
					}
					
					
			
						
				
				
				}
				
				//close up shop
				rcv.close();
				send.close();
				s.close();
						
		}	
	}	
		
	private static String GETheader(String filename, Boolean found, Long Fsize, Long Lastmod){
		String header="";
		SimpleDateFormat df=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		Date d=new Date();
		String DATE=df.format(d);
		if(found){
			header="HTTP/1.0 200 OK\r\nDate: "+DATE+"\r\nServer: HTTP Server\r\nContent-Length: "+Fsize+"\r\nLast-Modified: "+(df.format(Lastmod))+"\r\n\r\n";
		}
		else{
			header="HTTP/1.0 404 Not Found\r\n\r\n";
		}
		return header;
	}
	
	
	
}


