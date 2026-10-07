package com.Usaid_Syed.taskforge.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task " + id + " not found");
    }
}
