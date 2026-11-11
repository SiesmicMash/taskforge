package com.Usaid_Syed.taskforge.controller;


import com.Usaid_Syed.taskforge.CSV.TaskCsvWriter;
import com.Usaid_Syed.taskforge.service.TaskService;
import com.Usaid_Syed.taskforge.task.TaskRequest;
import com.Usaid_Syed.taskforge.task.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create (@Valid @RequestBody TaskRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv() {
        String csv = TaskCsvWriter.toCsv(service.findAll());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tasks.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
