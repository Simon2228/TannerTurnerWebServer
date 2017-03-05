package turnerserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable
{
    private int serverPort = 4444;
    private int threadLimit = 10;
    private int userCount = 0;
    private boolean isStopped = false;
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

        while(!isStopped())
        {
            boolean successfulConnection = processConnection();

            if (!successfulConnection)
            {
                break;
            }
        }

        stopServer();
    }

    private void startServer()
    {
        try
        {
            serverSocket = new ServerSocket(serverPort);
            threadPool = Executors.newFixedThreadPool(threadLimit);
        }
        catch (IOException e)
        {
            System.err.println("Cannot open port " + serverPort);
            System.exit(1);
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Invalid number of threads");
            System.exit(1);
        }
    }

    private boolean processConnection()
    {
        Socket clientSocket = null;
        RequestThread requestThread;

        try
        {
            clientSocket = serverSocket.accept();
        }
        catch (IOException e)
        {
            System.err.println("Error accepting client connection");

            if(isStopped())
            {
                return false;
            }
        }

        requestThread = new RequestThread(clientSocket, ++userCount);
        threadPool.execute(requestThread);
        return true;
    }

    public synchronized void stopServer()
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
            System.err.println("Thread pool did not shutdown properly");
        }

        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("Error closing server");
        }

        isStopped = true;
    }

    private synchronized boolean isStopped()
    {
        return isStopped;
    }

    public static void main(String[] args)
    {
        Server server = new Server(4444, 10);
        Thread thread = new Thread(server);
        thread.start();
    }
}