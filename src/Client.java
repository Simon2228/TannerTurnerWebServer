import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    public static void main(String[] args)
    {
        String hostName = "127.0.0.1";
        int portNumber = 4444;

        if (args.length == 2)
        {
            hostName = args[0];

            try
            {
                portNumber = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid argument");
                System.exit(1);
            }
        }

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                InputStream socketInput  = clientSocket.getInputStream();
                OutputStream socketOutput = clientSocket.getOutputStream();
                InputStreamReader socketInputReader = new InputStreamReader(socketInput);
                InputStreamReader systemInputReader = new InputStreamReader(System.in);
                BufferedReader in = new BufferedReader(socketInputReader);
                BufferedReader stdIn = new BufferedReader(systemInputReader);
                PrintWriter out = new PrintWriter(socketOutput, true)
        )
        {
            String userInput;

            System.out.println("There is a message waiting from the server...\n"
                    + in.readLine() + "\n");

            while ((userInput = stdIn.readLine()) != null)
            {
                out.println(userInput);

                if (userInput.equals("close"))
                {
                    System.out.println("\n" + in.readLine());
                    break;
                }

                System.out.println("You typed: " + userInput);
                System.out.println("\nThe server responded... \n" + in.readLine());
            }
        }
        catch (UnknownHostException e)
        {
            System.err.println("Can't find host: " + e);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("Error with host: " + e);
            System.exit(1);
        }
    }
}