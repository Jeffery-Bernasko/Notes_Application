package org.example.notes_application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class NoteRequestDTO {
    private String title;
    private String content;
    private List<String> tags;
}
