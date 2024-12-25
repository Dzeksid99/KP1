package client;

import java.awt.Desktop;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URI;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8888;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
        {
            System.out.println("Подключено к серверу " + host + ":" + port);

            String welcome = in.readLine();
            System.out.println("Сервер: " + welcome);

            Scanner scanner = new Scanner(System.in);
            String currentUser = null;

            MAIN_LOOP:
            while (true) {
                System.out.println("\n===== МЕНЮ =====");
                System.out.println("Текущий пользователь: " + (currentUser == null ? "нет" : currentUser));
                System.out.println("1. REGISTER (создать нового пользователя)");
                System.out.println("2. LOGIN (у меня уже есть UUID)");
                System.out.println("3. CREATE_LINK (создать реальную короткую ссылку)");
                System.out.println("4. GO (ввести короткую ссылку и открыть браузер)");
                System.out.println("5. EDIT_LIMIT");
                System.out.println("6. DELETE_LINK");
                System.out.println("7. QUIT");
                System.out.print("Ваш выбор: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        out.println("REGISTER");
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                        if (resp != null && resp.startsWith("USER ")) {
                            currentUser = resp.substring("USER ".length());
                        }
                    }
                    case "2" -> {
                        System.out.print("Введите ваш UUID: ");
                        String uuid = scanner.nextLine().trim();
                        out.println("LOGIN " + uuid);
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                        if ("OK".equals(resp)) {
                            currentUser = uuid;
                        }
                    }
                    case "3" -> {
                        if (currentUser == null) {
                            System.out.println("Сначала LOGIN или REGISTER!");
                            break;
                        }
                        System.out.print("Введите исходный (длинный) URL: ");
                        String url = scanner.nextLine().trim();
                        System.out.print("Введите время жизни (часов): ");
                        int hours = readInt(scanner);
                        System.out.print("Введите лимит переходов: ");
                        int limit = readInt(scanner);

                        out.println("CREATE_LINK " + currentUser + " " + url + " " + hours + " " + limit);
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                    }
                    case "4" -> {
                        System.out.print("Введите короткую ссылку (напр. https://clck.ru/abc123): ");
                        String shortUrl = scanner.nextLine().trim();

                        out.println("GO " + shortUrl);
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);

                        if (resp != null && resp.startsWith("REDIRECT ")) {
                            String realLink = resp.substring("REDIRECT ".length());
                            try {
                                Desktop.getDesktop().browse(new URI(realLink));
                                System.out.println("Открываем в браузере: " + realLink);
                            } catch (Exception e) {
                                System.out.println("Ошибка при открытии браузера: " + e.getMessage());
                            }
                        }
                    }
                    case "5" -> {
                        if (currentUser == null) {
                            System.out.println("Сначала LOGIN или REGISTER!");
                            break;
                        }
                        System.out.print("Введите shortUrl: ");
                        String shortUrl = scanner.nextLine().trim();
                        System.out.print("Введите новый лимит: ");
                        int newLimit = readInt(scanner);

                        out.println("EDIT_LIMIT " + shortUrl + " " + currentUser + " " + newLimit);
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                    }
                    case "6" -> {
                        if (currentUser == null) {
                            System.out.println("Сначала LOGIN или REGISTER!");
                            break;
                        }
                        System.out.print("Введите shortUrl для удаления: ");
                        String shortUrl = scanner.nextLine().trim();

                        out.println("DELETE_LINK " + shortUrl + " " + currentUser);
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                    }
                    case "7" -> {
                        out.println("QUIT");
                        String resp = in.readLine();
                        System.out.println("Сервер ответ: " + resp);
                        break MAIN_LOOP;
                    }
                    default -> System.out.println("Неверный выбор.");
                }
            }

            System.out.println("Клиент завершается.");

        } catch (ConnectException e) {
            System.out.println("Не удалось подключиться к " + host + ":" + port);
            System.out.println("Причина: " + e.getMessage());
            System.out.println("Убедитесь, что сервер запущен и слушает указанный порт.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int readInt(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
