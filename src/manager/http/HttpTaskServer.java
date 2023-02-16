package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    public final HttpTaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this.taskManager = new HttpTaskManager("http://localhost:8078/", false);
        gson = new Gson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange h) {
        try {
            System.out.println(h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring("/tasks/".length());
            final String query = h.getRequestURI().getQuery();
            switch (path) {
                case "":
                    if ("GET".equals(h.getRequestMethod())) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(h, response);
                    } else {
                        System.out.println("/tasks ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                case "history":
                    if ("GET".equals(h.getRequestMethod())) {
                        String response = gson.toJson(taskManager.getHistory());
                        sendText(h, response);
                    } else {
                        System.out.println("/tasks ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                case "task":
                    handleTask(h, query);
                    break;
                case "subtask":
                    handleSubtask(h, query);
                    break;
                case "subtask/epic":
                    if ("GET".equals(h.getRequestMethod())) {
                        String idParam = query.substring(3);
                        final int id = Integer.parseInt(idParam);
                        final List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
                        final String response = gson.toJson(subtasks);
                        System.out.println("Получили подзадачи для эпика id=" + id);
                        sendText(h, response);
                    } else {
                        System.out.println("subtask/epic ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                case "epic":
                    handleEpic(h, query);
                    break;
                default:
                    System.out.println("Неизвестный запрос");
                    h.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleTask(HttpExchange h, String query) throws IOException {
        switch (h.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final List<Task> tasks = taskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Task task = taskManager.getTask(id);
                final String response = gson.toJson(task);
                System.out.println("Получили задачу id=" + id);
                sendText(h, response);
                return;
            }
            case "DELETE": {
                if (query == null) {
                    taskManager.deleteTasks();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.deleteTask(id);
                System.out.println("Удалили задачу id=" + id);
                h.sendResponseHeaders(200, 0);
                return;
            }
            case "POST": {
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                Task task = gson.fromJson(value, Task.class);
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    System.out.println("Задача успешно добавлена!");
                } else {
                    taskManager.updateTask(task);
                    System.out.println("Задача успешно обновлена!");
                }
            }
            h.sendResponseHeaders(200, 0);
            return;
            default: {
                System.out.println("Неверный запрос: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handleSubtask(HttpExchange h, String query) throws IOException {
        switch (h.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final List<Subtask> tasks = taskManager.getSubtasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Subtask subtask = taskManager.getSubtask(id);
                final String response = gson.toJson(subtask);
                System.out.println("Получили задачу id=" + id);
                sendText(h, response);
                return;
            }
            case "DELETE": {
                if (query == null) {
                    taskManager.deleteSubtasks();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.deleteSubtask(id);
                System.out.println("Удалили задачу id=" + id);
                h.sendResponseHeaders(200, 0);
                return;
            }
            case "POST": {
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                Subtask subtask = gson.fromJson(value, Subtask.class);
                if (subtask.getId() == 0) {
                    taskManager.addSubtask(subtask);
                    System.out.println("Задача успешно добавлена!");
                } else {
                    taskManager.updateSubtask(subtask);
                    System.out.println("Задача успешно обновлена!");
                }
            }
            h.sendResponseHeaders(200, 0);
            return;
            default: {
                System.out.println("Неверный запрос: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handleEpic(HttpExchange h, String query) throws IOException {
        switch (h.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final List<Epic> tasks = taskManager.getEpics();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Epic epic = taskManager.getEpic(id);
                final String response = gson.toJson(epic);
                System.out.println("Получили задачу id=" + id);
                sendText(h, response);
                return;
            }
            case "DELETE": {
                if (query == null) {
                    taskManager.deleteEpics();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.deleteEpic(id);
                System.out.println("Удалили задачу id=" + id);
                h.sendResponseHeaders(200, 0);
                return;
            }
            case "POST": {
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                Epic epic = gson.fromJson(value, Epic.class);
                if (epic.getId() == 0) {
                    taskManager.addEpicTask(epic);
                    System.out.println("Задача успешно добавлена!");
                } else {
                    taskManager.updateEpic(epic);
                    System.out.println("Задача успешно обновлена!");
                }
                h.sendResponseHeaders(200, 0);
                return;
            }
            default: {
                System.out.println("Неверный запрос: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}