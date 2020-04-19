package com.bmarkov.challenge.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task has a name and a shell command.
 * Tasks may depend on other tasks and require that those are executed beforehand.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    private String name;
    private String command;
    private String [] requires;

}
