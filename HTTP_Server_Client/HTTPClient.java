import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;


public class HTTPClient {

	public static void main(String[] args) throws Exception{
		//if its a PUT argument send to PUTmssg
		if((args.length==3)&&(args[0].equalsIgnoreCase("put"))){
			PUTmssg(args[1], args[2]);
		}
		//if its a GET request send to GETrqst
		else if(args.length==1){
			GETrqst(args[0]);
		}
		//else put out an error and return
		else{
			System.err.println("ERR-arg 1");
			return;
		}	
		
		
	}
	
	private static void GETrqst(String a)throws Exception{
		//creates a string to check the beginning of the argument for http://
		//String check = a.substring(0, Math.min(a.length()-1, 6));
		String hostname = " ";
		String Port="80";
		String Path=" ";
		InetAddress Host=null;
		int PORT=0;
		int i=0;
		//checks string to ensure proper format
		if(!(a.startsWith("http://"))|| a.length()<7){
			System.err.println("ERR-invalid URL");
			return;
		}
		//looks for a port in the URL
		i=a.indexOf(":", 6);
		//if a port was found
		if(i>=0){
			hostname = a.substring(7, i);
			//looks for the path follwing the port
			int ix=a.indexOf("/", i);
			//if the path was found
			if(ix>=0){
				Port=a.substring(i+1,ix);
				Path=a.substring(ix);
			}
			//if a path wasn't specified
			else{
				Port=a.substring(i+1);
				Path="/";
			}
		}
		//if a port wasn't specified
		else{
			//looks for a path
			i=a.indexOf("/", 7);
			//if a path was found
			if(i>=0){
				hostname = a.substring(7, i);
				Port="80";
				Path= a.substring(i);
			}
			//if a path wasn't specified
			else{
				hostname = a.substring(7);
				Port="80";
				Path = "/";
			}
		}
		//convert Port to integer value
		try{
			PORT= Integer.parseInt(Port);
		}catch(NumberFormatException e){
			System.err.println("ERR-bad port");
			return;
		}
		//build HTTP header string
		String header = "GET "+ Path +" HTTP/1.0\r\nHost: "+ hostname +"\r\nUser-Agent: ODU-CS455/555\r\n\r\n";
		//retrieve host address
		try{
			Host=InetAddress.getByName(hostname);
		}catch(UnknownHostException e){
			System.err.println("ERR-Unknown Host "+hostname);
			return;
			
		}
		//create socket
		try{
			Socket s= new Socket(Host, PORT);
			s.setSoTimeout(10000000);
		//create output stream
			PrintWriter send= new PrintWriter(s.getOutputStream());
		//crate input stream
			BufferedReader rcv= new BufferedReader(new InputStreamReader(s.getInputStream()));
		//send header on output stream
			send.print(header);
		//print header to screen
			System.out.print(header);
		//flush output stream
			send.flush();
		//String for Response Code and Server Type
			String rcode="x";
			String server="x";
		//Read response until double \r\n into header Vector
			Vector<String> Rheader = new Vector<String>();
			for(String line=rcv.readLine(); !line.equals(""); line=rcv.readLine()){
				Rheader.add(line);
			}
		//parse Response Code from first line of header
			rcode=Rheader.get(0).substring(9,13);
		//find line containing the Server Type
			for( i=1; i<Rheader.size();i++){
				String temp=Rheader.get(i);
				if(temp.startsWith("Server")){
					server=temp.substring(8);
				}
				else if(temp.startsWith("Via")){
					server= temp.substring(5);
				}
			}
		//print Rcode and Server type
			System.out.println(rcode+"\n"+server);
		//if Rcode is in 200 range
			if(rcode.startsWith("2")){
				Boolean found=false;
				String lastmod=" ";
				String contentL=" ";
				String filetype=".txt";
				String filename=" ";
		//looks for last modified date
				for(i=1; i<Rheader.size(); i++){
					String temp=Rheader.get(i);
					if(temp.startsWith("Last-Modified")){
						lastmod=temp.substring(15);
						found=true;
					}
				}
		//if date found, prints to screen
				if(found){
					System.out.println(lastmod);
					found=false;
				}
		//looks for length of file
				for(i=1; i<Rheader.size(); i++){
					String temp=Rheader.get(i);
					if(temp.startsWith("Content-Length")){
						contentL=temp.substring(16);
						found=true;
					}
				}
		//if length found, prints to screen
				if(found){
					System.out.println(contentL);
					found=false;
				}
		//looks for file type either html or txt
				for(i=1; i<Rheader.size(); i++){
					String temp=Rheader.get(i);
					if(temp.startsWith("Content")){
						if(temp.contains("html")){
						filetype=".html";
						}
					}
			
				}
		//creates file with filename index + filetype html or txt
				filename="index"+filetype;
		//output stream for file
				BufferedWriter outfile= new BufferedWriter(new FileWriter(filename));
		//writes the rest of the received message to file
				for(String line = rcv.readLine(); line!=null; line=rcv.readLine()){
					outfile.write(line);
				}
		//close outfile
				outfile.close();
		//prints a blank line for formatting 
				System.out.println("");
		//prints the received header
				for(i=0; i<Rheader.size(); i++){
					System.out.println(Rheader.get(i));
				}
			}
		//if Response code is in 300 range
			if(rcode.startsWith("3")){
				String locale=" ";
		//looks for the new location
				for(i=1; i<Rheader.size(); i++){
					String temp=Rheader.get(i);
					if(temp.startsWith("Location")){
						locale=temp.substring(10);
					}
				}
		//prints the new location
				System.out.println(locale);
			}
		//closes buffered reader
			rcv.close();
		//closes the socket
			s.close();
		//closes the output writer
			send.close();
		}catch(SocketTimeoutException e){
			System.err.println("ERR-Request Timed Out");
			return;
		}
		
	}
	
	private static void PUTmssg(String a, String b)throws Exception{
		//checks for server name
		if(!(a.contains("sirius"))&&!(a.contains("atria"))){
			System.err.println("ERR-arg 2");
			return;
		}
		Long Fsize=0L;
		Long Lastmod=0L;
		//looks for port
		String hostname = " ";
		String Port=" ";
		String ServerPath=" ";
		InetAddress Host=null;
		int PORT=0;
		int i=0;
		//checks string to ensure proper format
		if(!(a.startsWith("http://"))|| a.length()<7){
			System.err.println("ERR-invalid URL");
			return;
		}
		//looks for a port in the URL
		i=a.indexOf(":", 6);
		//if a port was found
		if(i>=0){
			hostname = a.substring(7, i);
			//looks for the path follwing the port
			int ix=a.indexOf("/", i);
			//if the path was found
			if(ix>=0){
				Port=a.substring(i+1,ix);
				ServerPath=a.substring(ix);
			}
			//if a path wasn't specified
			else{
				Port=a.substring(i+1);
				ServerPath="./";
			}
		}
		//if a port wasn't specified
		else{
			System.err.println("Err- arg 2 port was not given");
			return;
		}
		
		//convert Port to integer value
		try{
			PORT= Integer.parseInt(Port);
		}catch(NumberFormatException e){
			System.err.println("ERR-bad port");
			return;
		}

		File check= new File(b);
		if(!check.exists()||!check.isFile()){
				System.err.println("ERR-Arg 3-File Not Found");
				return;
		}
		//pulls file length and last modified info
		Fsize=check.length();
		Lastmod=check.lastModified();
		//checks for / in path, if none, adds one at the beginning
		
		try{
		InetAddress HOST= InetAddress.getByName(hostname);
		Socket s=new Socket(HOST, PORT);
		s.setSoTimeout(10000000);
		PrintWriter send=new PrintWriter(s.getOutputStream());
		BufferedReader rcv=new BufferedReader(new InputStreamReader(s.getInputStream()));
		BufferedReader OutFile=new BufferedReader(new FileReader(b));
		StringBuilder sb=new StringBuilder(Fsize.intValue()+1000);
		String header="PUT "+ServerPath+" HTTP1.0\r\nHost: "+hostname+"\r\nUser-Agent: ODU-CS455/555\r\n\r\n";
		sb.append(header);
		for(String line=OutFile.readLine(); line!=null; line=OutFile.readLine()){
			sb.append(line);
		}
		String request=sb.toString();
		System.out.println(request);
		send.print(request);
		send.flush();
		String rcode="x";
		String server="x";
	//Read response until double \r\n into header Vector
		Vector<String> Rheader = new Vector<String>();
		for(String line=rcv.readLine(); !line.equals(""); line=rcv.readLine()){
			Rheader.add(line);
		}
		String fline=Rheader.get(0);
	//parse Response Code from first line of header
		rcode=fline.substring(9,13);
	//find line containing the Server Type
		for( i=1; i<Rheader.size();i++){
			String temp=Rheader.get(i);
			if(temp.startsWith("Server")){
				server=temp.substring(8);
			}
			else if(temp.startsWith("Via")){
				server= temp.substring(5);
			}
		}
	//print Rcode and Server type
		System.out.println(rcode+"\n"+server);
		
		for(i=0 ; i<Rheader.size(); i++){
			System.out.println(Rheader.get(i));
		}
		s.close();
		rcv.close();
		send.close();
		OutFile.close();
		
		}catch(SocketTimeoutException e){
			System.err.println("Err-Request timed out");
		}
		
	}
		
}
