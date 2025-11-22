package org.example.notes_application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Data
public class NoteResponseDTO {
    private Long id;
    private String title;
    private String content;
    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;
}
