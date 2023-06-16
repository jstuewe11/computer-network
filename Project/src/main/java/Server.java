// Server Side Program

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

//----------------------------------------------------------------------- Server Class
public class Server
{ 
	public static void main(String[] args) throws IOException
	{ 
		ServerSocket welcomeSocket = new ServerSocket(6789);  //Server port = 6789
		int index = -1;
   
    System.out.println("**********************************");
    System.out.println("***   WELCOME TO MATH SERVER   ***");
    System.out.println("*** Protocol: Operator Int Int ***");
    System.out.println("**********************************");   
    
		while (true)
		{
			Socket connectionSocket = null;
			try
			{
				connectionSocket = welcomeSocket.accept();  //Socket accepts incoming client requests
        index++;
				System.out.println("\nClient " + index + " joined...\n" + connectionSocket);
				
				DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        
				Thread newClient = new ClientThread(index, connectionSocket, inFromClient, outToClient);
				newClient.start();
			}
			catch (Exception e)
      {
				connectionSocket.close();
				e.printStackTrace();
			}
		}
	}
}
//----------------------------------------------------------------------- ClientThread Class
class ClientThread extends Thread
{

  //DateFormat joinDate = new SimpleDateFormat("MM/dd/yyyy");
  //DateFormat joinTime = new SimpleDateFormat("hh:mm:ss");
  //DateFormat exitTime = new SimpleDateFormat("hh:mm:ss");
  //String[][] clientLog = new String[3][100];
  //Date date = new Date(System.currentTimeMillis());
  long[][] clientLog = new long[2][100];
  long currentDateTime;
  long initialDateTime;
 
	final DataInputStream inFromClient;
	final DataOutputStream outToClient;
	final Socket connectionSocket;
  final int index; 
	
	public ClientThread(int index, Socket connectionSocket, DataInputStream inFromClient, DataOutputStream outToClient)
	{
    this.index = index;
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	@Override
	public void run()
	{
            initialDateTime = System.currentTimeMillis();
            Date initialDate = new Date(initialDateTime);
    clientLog[0][index] = System.currentTimeMillis();
    
		String clientSentence;
		String clientResponse;
    String[] tokens;
    String ack = "\nConnection Established Successfully...";
    int result;    

    //clientLog[0][index] = joinDate.format(date);
    //clientLog[1][index] = joinTime.format(date); 
        
		while (true)
		{
			try
      {        
				outToClient.writeUTF(ack + "\nType an operator [Add/Sub/Mul/Div] followed by 2 numbers. \nType \"Exit\" to terminate...");
        ack = "";
				clientSentence = inFromClient.readUTF();
        
        System.out.println("\nClient " + this.index + ":\t" + clientSentence);
				if(clientSentence.equals("Exit"))
				{
          //clientLog[2][index] = exitTime.format(date);
          clientLog[1][index] = System.currentTimeMillis();
          currentDateTime = System.currentTimeMillis();
          Date currentDate = new Date(currentDateTime);
					this.connectionSocket.close();
          System.out.println("Client " + index + " log history...");
          //System.out.println("Date joined: \t" + clientLog[0][index]);
          //System.out.println("Time joined: \t" + clientLog[1][index]);
          //System.out.println("Time exited: \t" + clientLog[2][index]);
          
          System.out.println("Joined: \t" + initialDate);
          System.out.println("  Left: \t" + currentDate);
          System.out.println(" Total: \t" + ((clientLog[1][index] - clientLog[0][index])/1000.0) + " seconds" );
          
					System.out.println("Connection closed");
					break;
				}
				
        tokens = clientSentence.split("\\s");          
				switch (tokens[0])
        {
				
					case "Add" :
            result = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " + " + tokens[2] + " = " + result;
            System.out.println("Result:\t\t" + clientResponse);
						outToClient.writeUTF("Server Result: " + clientResponse);
						break;
					
					case "Sub" :
            result = Integer.parseInt(tokens[1]) - Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " - " + tokens[2] + " = " + result;
            System.out.println("Result:\t\t" + clientResponse);
						outToClient.writeUTF("Server Result: " + clientResponse);
						break;
            
					case "Mul" :
            result = Integer.parseInt(tokens[1]) * Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " * " + tokens[2] + " = " + result;
            System.out.println("Result:\t\t" + clientResponse);
						outToClient.writeUTF("Server Result: " + clientResponse);
						break;
            	
					case "Div" :
            if (Integer.parseInt(tokens[2]) == 0)
            {
                clientResponse = "Division by zero is not allowed";
                System.out.println("Result:\t\t" + clientResponse);
					    	outToClient.writeUTF("Server Result: " + clientResponse);
                break;
            }
            result = Integer.parseInt(tokens[1]) / Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " / " + tokens[2] + " = " + result;
            System.out.println("Result:\t\t" + clientResponse);
						outToClient.writeUTF("Server Result: " + clientResponse);
						break;
						
					default:
						clientResponse = "Invalid input";
            System.out.println("Result:\t\t" + clientResponse);
						outToClient.writeUTF("Server Result: " + clientResponse);
						break;
				} //End switch
			}
      catch (IOException e)
      {
				e.printStackTrace();
			}
		}//End while
		
		try
		{
			this.inFromClient.close();
			this.outToClient.close();
			
		}
    catch(IOException e)
    {
			e.printStackTrace();
		}
	}
}