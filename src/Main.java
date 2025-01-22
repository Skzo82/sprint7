public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задач
        Task task1 = new Task(taskManager.generateId(), "Переезд", "Переезд на новую квартиру", TaskStatus.NEW);
        Task task2 = new Task(taskManager.generateId(), "Покупка мебели", "Купить новую мебель для квартиры", TaskStatus.NEW);

        // Создание эпиков и подзадач
        Epic epic1 = new Epic(taskManager.generateId(), "Семейный праздник", "Организация семейного праздника");
        Subtask subtask1 = new Subtask(taskManager.generateId(), "Пригласить гостей", "Отправить приглашения", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(taskManager.generateId(), "Купить еду", "Купить еду и напитки", TaskStatus.NEW, epic1.getId());

        // Сохраняем задачи и эпики через менеджер
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Распечатываем задачи и эпики
        System.out.println("Все задачи:");
        taskManager.getAllTasks().forEach((id, task) -> System.out.println(task.getTitle() + " - " + task.getStatus()));

        // Обновление статусов
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        // Распечатываем статусы после обновления
        System.out.println("\nОбновленные статусы:");
        System.out.println("Эпик: " + epic1.getTitle() + " - " + epic1.getStatus());
        System.out.println("Подзадача 1: " + subtask1.getTitle() + " - " + subtask1.getStatus());
        System.out.println("Подзадача 2: " + subtask2.getTitle() + " - " + subtask2.getStatus());

        // Удаление задачи и эпика
        taskManager.deleteTask(task1.getId());
        taskManager.deleteTask(epic1.getId());

        // Проверка оставшихся задач
        System.out.println("\nОставшиеся задачи после удаления:");
        taskManager.getAllTasks().forEach((id, task) -> System.out.println(task.getTitle() + " - " + task.getStatus()));
    }
}
