package com.Usaid_Syed.taskforge.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
   @NotBlank(message = "Title is required")
   @Size(max = 100)
   String title,

   @Size(max = 500)
   String description,

   TaskStatus status
) {}
