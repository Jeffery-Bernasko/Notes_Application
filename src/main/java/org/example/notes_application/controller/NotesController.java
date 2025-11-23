package org.example.notes_application.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.notes_application.dto.NoteRequestDTO;
import org.example.notes_application.dto.NoteResponseDTO;
import org.example.notes_application.model.Notes;
import org.example.notes_application.model.User;
import org.example.notes_application.service.NotesService;
import org.example.notes_application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<NoteResponseDTO> createNote(@Valid @RequestBody NoteRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails){
        User user = getCurrentUser(userDetails);
        Notes notes = notesService.createNote(dto,user);
        return ResponseEntity.ok(toDto(notes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDTO dto,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);
        return notesService.softDeleteNote(id, user)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Page<NoteResponseDTO>> getNotes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(notesService.searchNotes(search, user, pageable));
        } else if (tags != null && !tags.isEmpty()) {
            return ResponseEntity.ok(notesService.filterByTags(tags, user, pageable));
        } else {
            return ResponseEntity.ok(notesService.listNotes(user, pageable));
        }
    }

    @GetMapping("/deleted")
    public ResponseEntity<Page<NoteResponseDTO>> getDeletedNotes(
            @PageableDefault(sort = "deletedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(notesService.listDeletedNotes(user, pageable));
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

    @GetMapping("/by-search")
    public ResponseEntity<Page<NoteResponseDTO>> getNotesBySearch(
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        Page<NoteResponseDTO> result = (search != null && !search.isBlank())
                ? notesService.searchNotes(search, user,pageable)
                : notesService.listNotes(user,pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<Page<NoteResponseDTO>> getNotesByTags(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(notesService.searchNotes(search, user,pageable));
        } else if (tags != null && !tags.isEmpty()) {
            return ResponseEntity.ok(notesService.filterByTags(tags, user,pageable));
        } else {
            return ResponseEntity.ok(notesService.listNotes(user,pageable));
        }
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