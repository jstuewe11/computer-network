/*
Final Project - Server Side Program
CS 4390.003 - Computer Networks - F22
Leila Nasimi - LXN200006
Jacob Stuewe - JWS170002
*/

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
		int index = -1;                                       //Client number given to eaxh client
   
    System.out.println("**********************************");
    System.out.println("***   WELCOME TO MATH SERVER   ***");
    System.out.println("*** Protocol: Operator Int Int ***");
    System.out.println("**********************************");   
    
		while (true)
		{
			Socket connectionSocket = null;
			try
			{
				connectionSocket = welcomeSocket.accept();      //Socket accepts incoming client requests
        index++;                                        //Specify a client number
				System.out.println("\nClient " + index + " joined...\n" + connectionSocket);
				
				DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());     //Data streams from and to the client
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 

        outToClient.writeUTF("Connection Established Successfully...");                           //Send acknowledgment to client
            
				Thread newClient = new ClientThread(index, connectionSocket, inFromClient, outToClient);  //Create a new thread for each client, so they multiple clients can run at the same time
				newClient.start();                                                                        //Start the threat
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
  long[][] clientLog = new long[2][100];              //Array to log client info
  long joinDateTime;                                  //Date and time client connects the server
  long endDateTime;                                   //Date and time client disconnects from server
 
	final DataInputStream inFromClient;                 //Data input/output stream objects to communicate(read/write) with client
	final DataOutputStream outToClient;
	final Socket connectionSocket;                      //Create a socket object for server
  final int index; 
	
	public ClientThread(int index, Socket connectionSocket, DataInputStream inFromClient, DataOutputStream outToClient)  //ClientThread Class's constructor
	{
    this.index = index;
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	@Override
	public void run()     //Start running the thread
	{
     joinDateTime = System.currentTimeMillis();          //Log the client's join time
     Date joinDate = new Date(joinDateTime);
     clientLog[0][index] = System.currentTimeMillis();
    
		String clientSentence;                               //Request received from client                            
		String clientResponse;                               //Result of client's request
    String[] tokens;                                    //Array to store parts of client request (operator, int, int)
    int result;                                          //Result of math calculation
        
		while (true)                                         //While the client and server are connected
		{
			try
      {        
				clientSentence = inFromClient.readUTF();        //Read request from client and orint on screen
        System.out.println("\nClient " + this.index + ":\t" + clientSentence);
        
				if(clientSentence.equals("Exit"))              //If client request is "Exit", close the client connection and print log
				{
          clientLog[1][index] = System.currentTimeMillis();
          endDateTime = System.currentTimeMillis();
          Date endDate = new Date(endDateTime);
					this.connectionSocket.close();
          System.out.println("Client " + index + " log history...");
          System.out.println("Joined: \t" + joinDate);
          System.out.println("  Left: \t" + endDate);
          System.out.println(" Total: \t" + ((clientLog[1][index] - clientLog[0][index])/1000.0) + " seconds" );
					System.out.println("Connection closed");
					break;
				}
				
        tokens = clientSentence.split("\\s");          //Break down the client request into 3 parts ((operator, int, int)  
				switch (tokens[0])                             //Based on the operator, do different calculations
        {
				
					case "Add" :                                 //If operator is Add: first number + second number, and send the rsult back
            result = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " + " + tokens[2] + " = " + result;
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
						break;
					
					case "Sub" :                                 //If operator is Sub: first number - second number, and send the rsult back
            result = Integer.parseInt(tokens[1]) - Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " - " + tokens[2] + " = " + result;
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
						break;
            
					case "Mul" :                                 //If operator is Mul: first number * second number, and send the rsult back
            result = Integer.parseInt(tokens[1]) * Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " * " + tokens[2] + " = " + result;
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
						break;
            	
					case "Div" :                                 //If operator is Div: first number / second number, and send the rsult back
            if (Integer.parseInt(tokens[2]) == 0)      //If second number is 0, return an error
            {
                clientResponse = "Division by zero is not allowed";
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
                break;
            }
            result = Integer.parseInt(tokens[1]) / Integer.parseInt(tokens[2]);
						clientResponse = tokens[1] + " / " + tokens[2] + " = " + result;
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
						break;
						
					default:
						clientResponse = "Invalid input";
            outToClient.writeUTF("Server Result: " + clientResponse);
            System.out.println("Result:\t\t" + clientResponse);
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