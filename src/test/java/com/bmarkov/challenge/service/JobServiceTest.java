package com.bmarkov.challenge.service;

import com.bmarkov.challenge.exception.CircularDependenciesException;
import com.bmarkov.challenge.exception.JobException;
import com.bmarkov.challenge.model.Job;
import com.bmarkov.challenge.model.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobServiceTest {
    private JobService jobService = new JobService();

    @Test
    void testTasksInOrderWhenJobIsEmpty() throws JobException {
        Job job = new Job();
        Task[] tasks = jobService.tasksInOrder(job);
        assertEquals(0, tasks.length, "Expected no tasks");
    }

    @Test
    void testTasksInOrderWhenNoDependenciesInvolved() throws JobException {
        Job job = new Job();
        job.setTasks(new Task[]{
                new Task("name-1", "ls", null),
                new Task("name-2", "ls", null),
                new Task("name-3", "ls", null),
        });
        Task[] tasks = jobService.tasksInOrder(job);
        assertEquals(3, tasks.length, "Expected 3 tasks");
        assertEquals("name-1", tasks[0].getName() );
    }

    @Test
    void testTasksInOrderWithCircularDependencies()  {
        Job job = new Job();
        job.setTasks(new Task[]{
                new Task("name-1", "ls", new String[]{"name-2"}),
                new Task("name-2", "ls", new String[]{"name-3"}),
                new Task("name-3", "ls", new String[]{"name-1"}),
        });
        Assertions.assertThrows(CircularDependenciesException.class, () -> jobService.tasksInOrder(job));
    }

    @Test
    void testTasksInOrderWithComplexDependencies() throws JobException, JsonProcessingException {
        Job job = new ObjectMapper().readValue("{\n" +
                "\"tasks\":[\n" +
                "{\n" +
                "\"name\":\"task-1\",\n" +
                "\"command\":\"touch /tmp/file1\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"task-2\",\n" +
                "\"command\":\"cat /tmp/file1\",\n" +
                "\"requires\":[\n" +
                "\"task-3\"\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"task-3\",\n" +
                "\"command\":\"echo 'Hello World!' > /tmp/file1\",\n" +
                "\"requires\":[\n" +
                "\"task-1\"\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"task-4\",\n" +
                "\"command\":\"rm /tmp/file1\",\n" +
                "\"requires\":[\n" +
                "\"task-2\",\n" +
                "\"task-3\"\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "\n" +
                "}\n" +
                "", Job.class);
        Task[] tasks = jobService.tasksInOrder(job);
        assertEquals(4, tasks.length, "Expected 4 tasks");
        assertEquals("task-1", tasks[0].getName() );
        assertEquals("task-3", tasks[1].getName() );
        assertEquals("task-2", tasks[2].getName() );
        assertEquals("task-4", tasks[3].getName() );
    }

}
