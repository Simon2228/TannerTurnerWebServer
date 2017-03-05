import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        String hostName = "127.0.0.1";
        int portNumber = 4444;

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
            System.err.println("Error with host " + hostName);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("Error with host IO");
            System.exit(1);
        }
    }
}