package server;

import link.LinkService;
import user.UserService;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final UserService userService;
    private final LinkService linkService;

    public ClientHandler(Socket socket, UserService userService, LinkService linkService) {
        this.socket = socket;
        this.userService = userService;
        this.linkService = linkService;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
        {
            out.println("WELCOME to Real Clck.ru Shortener Server. Enter commands.");

            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                String response = handleCommand(line);
                out.println(response);

                if ("GOODBYE".equals(response)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка IO с клиентом: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignore) {}
        }
    }

    private String handleCommand(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "REGISTER":
                var user = userService.createNewUser();
                return "USER " + user.getUserId();

            case "LOGIN":
                if (parts.length < 2) {
                    return "ERROR Usage: LOGIN <uuid>";
                }
                try {
                    UUID uuid = UUID.fromString(parts[1]);
                    return userService.userExists(uuid) ? "OK" : "NOT_FOUND";
                } catch (IllegalArgumentException e) {
                    return "ERROR invalid uuid";
                }

            case "CREATE_LINK":
                if (parts.length < 5) {
                    return "ERROR Usage: CREATE_LINK <uuid> <url> <hours> <limit>";
                }
                try {
                    UUID uuid = UUID.fromString(parts[1]);
                    String url = parts[2];
                    int hours = Integer.parseInt(parts[3]);
                    int limit = Integer.parseInt(parts[4]);

                    String realShortUrl = linkService.createShortLink(url, uuid, hours, limit);
                    return "LINK " + realShortUrl;
                } catch (Exception e) {
                    return "ERROR " + e.getMessage();
                }

            case "GO":
                if (parts.length < 2) {
                    return "ERROR Usage: GO <shortUrl>";
                }
                String shortUrl = parts[1];
                String result = linkService.go(shortUrl);
                if (result == null) {
                    return "ERROR link not found or expired/limit";
                } else {
                    return "REDIRECT " + result;
                }

            case "EDIT_LIMIT":
                if (parts.length < 4) {
                    return "ERROR Usage: EDIT_LIMIT <shortUrl> <uuid> <newLimit>";
                }
                try {
                    String sUrl = parts[1];
                    UUID uuid = UUID.fromString(parts[2]);
                    int newLimit = Integer.parseInt(parts[3]);
                    boolean ok = linkService.editLimit(sUrl, uuid, newLimit);
                    return ok ? "OK" : "ERROR cannot edit limit (not owner or not found)";
                } catch (Exception e) {
                    return "ERROR " + e.getMessage();
                }

            case "DELETE_LINK":
                if (parts.length < 3) {
                    return "ERROR Usage: DELETE_LINK <shortUrl> <uuid>";
                }
                try {
                    String sUrl = parts[1];
                    UUID uuid = UUID.fromString(parts[2]);
                    boolean ok = linkService.deleteLink(sUrl, uuid);
                    return ok ? "OK" : "ERROR cannot delete (not owner or not found)";
                } catch (Exception e) {
                    return "ERROR " + e.getMessage();
                }

            case "QUIT":
                return "GOODBYE";

            default:
                return "ERROR unknown command: " + cmd;
        }
    }
}
