package turnerserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class RequestThread implements Runnable
{
    private int id = 0;
    private Socket clientSocket = null;

    public RequestThread(Socket clientSocket, int id)
    {
        this.id = id;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
        try (
                InputStream input  = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                InputStreamReader inputReader = new InputStreamReader(input);
                BufferedReader in = new BufferedReader(inputReader);
                PrintWriter out = new PrintWriter(output, true)
        )
        {
            echo(in, out);
        }
        catch (SocketTimeoutException e)
        {
            System.err.println("Timeout occurred: " + e);
        }
        catch (SocketException e)
        {
            System.err.println("Socket error: " + e);
        }
        catch (IOException e)
        {
            System.err.println("Error processing client IO: " + e);
        }
        finally
        {
            try
            {
                clientSocket.close();
                System.out.println("Client " + id + " connection closed");
            }
            catch (IOException e)
            {
                System.err.println("Error closing client socket: " + e);
            }
        }
    }

    private void echo(BufferedReader in, PrintWriter out) throws IOException
    {
        String inputLine;

        System.out.println("Client " + id + " wishes to speak to us");
        out.println("Client " + id + ", type \"close\" and press enter to quit");

        while ((inputLine = in.readLine()) != null)
        {
            System.out.println("Client " + id + " said: \"" + inputLine + "\"");

            if (inputLine.equals("close"))
            {
                out.println("Goodbye, Client " + id);
                break;
            }

            out.println("Client " + id + ", I received your message: \"" + inputLine + "\"");
        }

        System.out.println("Client " + id + " has left");
    }
}