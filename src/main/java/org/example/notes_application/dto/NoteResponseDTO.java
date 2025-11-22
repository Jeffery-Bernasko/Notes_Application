package org.example.notes_application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Data
public class NoteResponseDTO {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String title;
    private String content;
    private List<String> tags;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;
}
