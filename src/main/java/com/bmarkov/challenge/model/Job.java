package com.bmarkov.challenge.model;

import lombok.Data;

/**
 * A job is a collection of tasks.
 */
@Data
public class Job {
    private Task[] tasks;
}
