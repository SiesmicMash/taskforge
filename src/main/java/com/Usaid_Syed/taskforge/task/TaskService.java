package com.Usaid_Syed.taskforge.task;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());

        if(request.status() != null) {
            task.setStatus(request.status());
        }
        return toResponse(repository.save(task));
    }

    public List<TaskResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public TaskResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getOrThrow(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }

        return toResponse(repository.save(task));
    }

    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Task getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(id));
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getStatus(), t.getCreatedAt());
    }

}
