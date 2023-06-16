// Client Side Program
//Jacob Stuewe - jws170002

import java.io.*;
import java.net.*;
import java.util.*;


//----------------------------------------------------------------------- Client Class
public class Client
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
            //Create random variable
            Random rand = new Random(System.currentTimeMillis());
            //String the client will send to server
            String inputString = "";
            //terms the client is able to choose from
            String[] terms = {"Add", "Sub", "Mul", "Div", "Exit"};
		try
		{
			Socket clientSocket = new Socket("127.0.0.1", 6789);  //Establish connection with server on port 6789
                        //Data streams from and to the server
			DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        //counter to allow at least 3 math calculations
                        int counter = 0;
                        //While the client and server are connected
			while (true)
                        {
                            //Variable to creates a random number for the time intervals
                            int randTime = rand.nextInt(10000);
                            //Next two variables are the numbers inputted into the math equation
                            int randNum = rand.nextInt(100);
                            int randNum2 = rand.nextInt(100);
                            //Variable that will pick which term will be used in the equation
                            int randTerms = 0;
                            //if statement to not allow exit term until 3 equations are done
                            if(counter < 3){
                                randTerms = rand.nextInt(4);
                            }
                            //else add the exit term
                            else{
                                randTerms = rand.nextInt(5);
                            }
                            //If exit is selected set input to just Exit
                            if(terms[randTerms] == "Exit") inputString = "Exit";
                            //otherwise use this string format
                            else inputString = terms[randTerms] + " " + randNum + " " + randNum2;
                            //increase counter after an equation is sent
                            counter++;
                            //have the thread sleep for the random amount of time
                            Thread.sleep(randTime);
                            //write to the server
                            outToServer.writeUTF(inputString);
                            //if input is Exit close socket and close connection
                            if(inputString.equals("Exit"))
                            {
                                clientSocket.close();
				System.out.println("Connection closed");
				break;
                            }
                            //Get the info from server and print
                            String modifiedSentence = inFromServer.readUTF();
                            System.out.println(modifiedSentence);
			}
                    //Close the connections
                    inFromServer.close();
                    outToServer.close();
		}
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}