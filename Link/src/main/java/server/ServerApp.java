package server;

import link.LinkCleaner;
import link.LinkRepository;
import link.LinkService;
import user.UserRepository;
import user.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    private final int port;
    private final UserService userService;
    private final LinkService linkService;
    private final LinkCleaner cleanerThread;

    public ServerApp(int port) {
        this.port = port;

        UserRepository userRepo = new UserRepository();
        LinkRepository linkRepo = new LinkRepository();

        userService = new UserService(userRepo);
        linkService = new LinkService(linkRepo);

        cleanerThread = new LinkCleaner(linkRepo, 30);
        cleanerThread.start();
    }

    public void start() {
        System.out.println("Сервер слушает порт " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Клиент подключился: " + client.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(client, userService, linkService);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanerThread.shutdown();
            System.out.println("Сервер завершает работу.");
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        ServerApp server = new ServerApp(port);
        server.start();
    }
}
