package manager;

import com.google.gson.Gson;
import manager.http.HttpTaskManager;
import manager.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    static HttpTaskServer server;
    HttpTaskManager httpTaskManager1;

    static KVServer kvServer;

    String url = "http://localhost:8080/";

    Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        taskManager = server.taskManager;
    }

    @AfterEach
    void AfterEach() {
        kvServer.stop();
        server.stop();
    }

    @Test
    void httpMethodGetPrioritizedTasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getPrioritizedTasks()), response.body());
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());
    }

    @Test
    void httpMethodGetHistory() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/history"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getHistory()), response.body());
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/history"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());
    }

    @Test
    void httpMethodGetTasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getTasks()), response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodGetSubtasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getSubtasks()), response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodGetEpics() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getEpics()), response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodGetEpicSubtasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/epic?id=4"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getEpicSubtasks(4)), response.body());
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());
    }

    @Test
    void httpMethodGetTask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task?id=1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getTask(1)), response.body());
        assertEquals(taskManager.getTask(1), gson.fromJson(response.body(), Task.class));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodGetSubtask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask?id=5"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getSubtask(5)), response.body());
        assertEquals(taskManager.getSubtask(5), gson.fromJson(response.body(), Subtask.class));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodGetEpic() throws IOException, InterruptedException {
        initializeAndAddTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic?id=4"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(gson.toJson(taskManager.getEpic(4)), response.body());
        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);
        assertEquals(taskManager.getEpic(4), epicFromJson);
        assertEquals(taskManager.getEpic(4).getSubtasksId(), epicFromJson.getSubtasksId());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodAddTask() throws IOException, InterruptedException {
        initializeTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        task1.setId(1);
        assertEquals(task1, taskManager.getTask(1));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodAddSubtask() throws IOException, InterruptedException {
        initializeTasks();
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("five", "task five", Status.DONE, epicId1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask5)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        subtask5.setId(2);
        assertEquals(subtask5, taskManager.getSubtask(2));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodAddEpic() throws IOException, InterruptedException {
        initializeTasks();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        epic1.setId(1);
        assertEquals(epic1, taskManager.getEpic(1));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodUpdateTask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        Task task = new Task("nine", "task nine", Status.NEW);
        task.setId(1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(task, taskManager.getTask(1));
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());
    }

    @Test
    void httpMethodUpdateSubtask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        Subtask subtask56 = new Subtask("five six", "task five six", Status.DONE, epic1.getId());
        subtask56.setId(5);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask56)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(subtask56, taskManager.getSubtask(5));
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask"))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask56)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());
    }

    @Test
    void httpMethodUpdateEpic() throws IOException, InterruptedException {
        initializeAndAddTasks();
        Epic epic12 = new Epic("three two", "task three two", Status.NEW);
        epic12.setId(4);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic12)))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(epic12, taskManager.getEpic(4));
        assertEquals(200, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(epic12)))
                .build();
        HttpResponse<Void> response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/ep"))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(epic12)))
                .build();
        HttpResponse<Void> response2 = client.send(request2, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response2.statusCode());
    }

    @Test
    void httpMethodDeleteTasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertFalse(taskManager.getTasks().isEmpty());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertTrue(taskManager.getTasks().isEmpty());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodDeleteSubtasks() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertFalse(taskManager.getSubtasks().isEmpty());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodDeleteEpics() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertFalse(taskManager.getEpics().isEmpty());
        assertFalse(taskManager.getSubtasks().isEmpty());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodDeleteTask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertNotNull(taskManager.getTask(1));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task?id=1"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertNull(taskManager.getTask(1));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodDeleteSubtask() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertNotNull(taskManager.getSubtask(5));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask?id=5"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertNull(taskManager.getSubtask(5));
        assertEquals(200, response.statusCode());
    }

    @Test
    void httpMethodDeleteEpic() throws IOException, InterruptedException {
        initializeAndAddTasks();
        assertNotNull(taskManager.getEpic(4));
        assertFalse(taskManager.getSubtasks().isEmpty());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic?id=4"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertNull(taskManager.getEpic(4));
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getPrioritizedTasks() {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        initializeAndAddTasks();
        assertFalse(taskManager.getPrioritizedTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getPrioritizedTasks(), httpTaskManager1.getPrioritizedTasks());
    }

    @Test
    void getHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getHistory().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getHistory(), httpTaskManager1.getHistory());
    }

    @Test
    void getTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTasks(), httpTaskManager1.getTasks());
    }

    @Test
    void getSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtasks(), httpTaskManager1.getSubtasks());
    }

    @Test
    void getEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpics(), httpTaskManager1.getEpics());
    }

    @Test
    void deleteTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTasks(), httpTaskManager1.getTasks());
    }

    @Test
    void deleteSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtasks(), httpTaskManager1.getSubtasks());
    }

    @Test
    void deleteEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getEpics().isEmpty());
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpics(), httpTaskManager1.getEpics());
    }

    @Test
    void getTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void getSubtask() {
        assertNull(taskManager.getSubtask(5));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(5));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void getEpicSubtasks() {
        assertNull(taskManager.getEpicSubtasks(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getEpicSubtasks(4).isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpicSubtasks(4), httpTaskManager1.getEpicSubtasks(4));
    }

    @Test
    void getEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void addTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        assertEquals(3, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void addSubtask() {
        assertNull(taskManager.getSubtask(5));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(5));
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void addEpicTask() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void updateTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        Task task = new Task("one", "task one", Status.NEW);
        task.setId(1);
        taskManager.updateTask(task);
        assertEquals(3, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task, task2, task3), httpTaskManager1.getTasks());
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void updateSubtask() {
        assertNull(taskManager.getSubtask(6));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(6));
        Subtask task = new Subtask("one", "task one", Status.NEW, 4);
        task.setId(6);
        taskManager.updateSubtask(task);
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(subtask5, task, subtask7), httpTaskManager1.getSubtasks());
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void updateEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        Epic task = new Epic("one", "task one", Status.NEW);
        task.setId(4);
        taskManager.updateEpic(task);
        assertEquals(2, taskManager.getEpics().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task, epic2), httpTaskManager1.getEpics());
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void deleteTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        taskManager.deleteTask(1);
        assertEquals(2, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task2, task3), httpTaskManager1.getTasks());
        assertNull(httpTaskManager1.getTask(1));
    }

    @Test
    void deleteSubtask() {
        assertNull(taskManager.getSubtask(6));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(6));
        taskManager.deleteSubtask(6);
        assertEquals(2, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(subtask5, subtask7), httpTaskManager1.getSubtasks());
        assertNull(httpTaskManager1.getSubtask(6));
    }

    @Test
    void deleteEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        taskManager.deleteEpic(4);
        assertEquals(1, taskManager.getEpics().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(epic2), httpTaskManager1.getEpics());
        assertNull(httpTaskManager1.getEpic(4));
    }
}