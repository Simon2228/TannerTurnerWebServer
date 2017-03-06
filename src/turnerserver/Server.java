package turnerserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable
{
    private int serverPort = 4444;
    private int threadLimit = 10;
    private int userCount = 0;
    private boolean running = true;
    private ServerSocket serverSocket = null;
    private ExecutorService threadPool = null;

    public Server(int port, int threadLimit)
    {
        this.serverPort = port;
        this.threadLimit = threadLimit;
    }

    @Override
    public void run()
    {
        startServer();

        while (running())
        {
            if (!establishConnection())
            {
                break;
            }
        }

        stopServer();
    }

    private synchronized void startServer()
    {
        try
        {
            serverSocket = new ServerSocket(serverPort);
            threadPool = Executors.newFixedThreadPool(threadLimit);
        }
        catch (IOException e)
        {
            System.err.println("Cannot open port: " + e);
            System.exit(1);
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Invalid argument: " + e);
            System.exit(1);
        }
    }

    private boolean establishConnection()
    {
        boolean success = true;
        Socket clientSocket = null;
        RequestThread requestThread;

        try
        {
            clientSocket = serverSocket.accept();
        }
        catch (SocketException e)
        {
            System.err.println("Socket error: " + e);
            success = false;
        }
        catch (IOException e)
        {
            System.err.println("Error accepting client connection: " + e);
            success = false;
        }

        if (success)
        {
            try
            {
                requestThread = new RequestThread(clientSocket, ++userCount);
                threadPool.execute(requestThread);
            }
            catch (RejectedExecutionException e)
            {
                System.err.println("Error with thread pool: " + e);
                success = false;
            }
            catch (NullPointerException e)
            {
                System.err.println("Error establishing connection: " + e);
                success = false;
            }
        }

        return success;
    }

    private synchronized void stopServer()
    {
        try
        {
            threadPool.shutdown();

            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
            {
                threadPool.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            System.err.println("Thread pool did not shut down properly: " + e);
        }
        finally
        {
            try
            {
                serverSocket.close();
            }
            catch (IOException e)
            {
                System.err.println("Error closing server socket: " + e);
            }
        }

        running = false;
    }

    private synchronized boolean running()
    {
        return running;
    }

    public static void main(String[] args)
    {
        int port = 4444;
        int maxThreads = 10;

        if (args.length == 2)
        {
            try
            {
                port = Integer.parseInt(args[0]);
                maxThreads = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid argument(s)");
                System.exit(1);
            }
        }

        Server server = new Server(port, maxThreads);
        Thread thread = new Thread(server);
        thread.start();
    }
}