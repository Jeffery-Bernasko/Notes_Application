package org.example.notes_application.controller;

import lombok.RequiredArgsConstructor;
import org.example.notes_application.dto.NoteRequestDTO;
import org.example.notes_application.dto.NoteResponseDTO;
import org.example.notes_application.model.Notes;
import org.example.notes_application.model.User;
import org.example.notes_application.service.NotesService;
import org.example.notes_application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;
    private final UserService  userService;

    // Authenticated user injected from token
    private User getCurrentUser(UserDetails userDetails){
        System.out.println("Authenticated User: " + userDetails.getUsername());
        return userService.findByEmail(userDetails.getUsername()).orElseThrow(()->new RuntimeException("user not found"));
    }

    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@RequestBody NoteRequestDTO dto,@AuthenticationPrincipal UserDetails userDetails){
        User user = getCurrentUser(userDetails);
        Notes notes = notesService.createNote(dto,user);
        return ResponseEntity.ok(toDto(notes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        return notesService.updateNote(id, dto, user)
                .map(note -> ResponseEntity.ok(toDto(note)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        User user = getCurrentUser(userDetails);
        return notesService.restoreNote(id,user) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getNotes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(notesService.listNotes(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        return notesService.getNoteById(id, user)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    private NoteResponseDTO toDto(Notes note) {
        NoteResponseDTO dto = new NoteResponseDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setTags(note.getTags());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
