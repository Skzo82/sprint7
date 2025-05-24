package test;

import managers.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends test.TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
