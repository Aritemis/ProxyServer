/**
 *	@author Ariana Fairbanks
 */

package proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Handler 
{
	public static final int BUFFER_SIZE = 1024;
	
	@SuppressWarnings("unused")
	public void process(Socket client) throws java.io.IOException 
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] secondBuffer = new byte[BUFFER_SIZE];
		Socket server = null;
		InputStream fromServer = null;
		DataOutputStream toServer = null;
		InputStream  fromClient = null;
		OutputStream toClient = null;
		
		try 
		{
			fromClient = new BufferedInputStream(client.getInputStream());
			toClient = new BufferedOutputStream(client.getOutputStream());
			int numBytes;
			
			
			while ( (numBytes = fromClient.read(buffer)) != -1) 
			{
				String input = new String(buffer);
				System.out.println(input);
				if(!input.substring(0, 3).equals("GET"))
				{
					client.close();
				}
				else
				{
					int end = input.indexOf("HTTP") -1;
					String resource = "/";
					String originHost = input.substring(5, end);
					int resourceStart = originHost.indexOf("/");
					if(resourceStart != -1)
					{
						resource = new String(originHost.substring(resourceStart));
						originHost = new String(originHost.substring(0, resourceStart));
					}
					server = new Socket(originHost, 80);
					fromServer = new BufferedInputStream(server.getInputStream());
					toServer = new DataOutputStream(server.getOutputStream());
					String result = "GET " + resource + " HTTP/1.1\r\nHost: " + originHost + "\r\nConnection: close\r\n\r\n";
					System.out.println(result);
					toServer.write(result.getBytes());
					int bytesRead;
					while ((bytesRead = fromServer.read(secondBuffer)) != -1)
					{
						toClient.write(secondBuffer);
						toClient.flush();
					}

				}
			}	
   		}
		catch (IOException | NumberFormatException e) 
		{
			System.err.println(e);
		}
		finally 
		{
			buffer = null;
			secondBuffer = null;
			if (fromClient != null) 
			{
				fromClient.close();
			}
			if (toClient != null)
			{
				toClient.close();
			}
			if (fromServer != null) 
			{
				fromServer.close();
			}
			if (toServer != null)
			{
				toServer.close();
			}
			client.close();
			server.close();
		}
	}
}
