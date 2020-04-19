package com.bmarkov.challenge.service;

import com.bmarkov.challenge.exception.CircularDependenciesException;
import com.bmarkov.challenge.exception.JobException;
import com.bmarkov.challenge.model.Job;
import com.bmarkov.challenge.model.Task;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * The service takes care of sorting the tasks
 * to create a proper execution order.
 */
@Named("jobService")
@Slf4j
public class JobService {
    /**
     * Create a proper execution order.
     * @param job from that job tasks are extracted; not null;
     * @return tasks in proper order
     * @throws CircularDependenciesException in case of circular dependencies.
     * @throws JobException in case of other problem like missing dependency.
     * @throws NullPointerException if job is null.
     *
     */
    public Task[] tasksInOrder(Job job) throws JobException, NullPointerException {
        if (job == null) {
            throw new NullPointerException("job cannot be null");
        }

        // Algorithm:
        // 'source' - contains no added sequential map(keeps order)
        // 'target' - contains already added tasks in proper order(keeps order)
        // 1. init 'target' = []
        // 2. while 'source' has items do:
        //   3. task = take next item from source
        //   4. if all dependencies of 'task' are present in target, then add 'task' to target
        //   5. else add 'task' to the end of 'source'
        //   6. if 'count' > n^2 then raise exception of Circular dependencies
        // 7. end of loop
        // 8. return 'target' as array of tasks

        Task[] tasks = job.getTasks();
        if (tasks == null) {
            // prevent NullPointerException
            tasks = new Task[0];
        }
        Map<String, Task> source = new LinkedHashMap<>(
                stream(tasks).collect(toMap(Task::getName, task -> task))
        );
        // Implementation of this map must keep order of insertion
        Map<String, Task> target = new LinkedHashMap<>(source.size());

        int i = 0;
        final int maxIterations = source.size() * source.size();
        log.debug("looping through the tasks");
        while (!source.isEmpty()) {
            i++;
            if (i > maxIterations) {
                throw new CircularDependenciesException("Circular dependency");
            }
            Iterator<Task> iterator = source.values().iterator();
            Task task = iterator.next();
            iterator.remove();

            String[] requires = task.getRequires();
            if (requires == null || requires.length == 0) {
                target.put(task.getName(), task);
            } else if (Arrays.stream(requires).allMatch(target::containsKey)) {
                target.put(task.getName(), task);
            } else {
                //return back to source, will be processed later
                source.put(task.getName(), task);
            }

        }
        Task[] array = target.values().toArray(new Task[0]);
        //clear the requires
        for (Task task : array) {
            task.setRequires(null);
        }
        log.debug("Returning {} tasks", target.size());
        return array;
    }


}
