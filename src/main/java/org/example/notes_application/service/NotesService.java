package org.example.notes_application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.notes_application.dto.NoteRequestDTO;
import org.example.notes_application.dto.NoteResponseDTO;
import org.example.notes_application.model.Notes;
import org.example.notes_application.model.User;
import org.example.notes_application.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotesService {
    private final NoteRepository noteRepository;

    @Transactional
    public Notes createNote(NoteRequestDTO dto, User user) {
        Notes notes = Notes.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .tags(dto.getTags())
                .owner(user)
                .build();
        return noteRepository.save(notes);
    }

    public Optional<Notes> updateNote(Long noteId, Notes updatedNote, User user) {
        return noteRepository.findByIdAndOwnerAndDeletedAtIsNull(noteId, user)
                .map(existing -> {
                    existing.setTitle(updatedNote.getTitle());
                    existing.setContent(updatedNote.getContent());
                    existing.setTags(updatedNote.getTags());
                    return noteRepository.save(existing);
                });
    }

    public Optional<Notes> updateNote(Long id, NoteRequestDTO dto, User user) {
        return noteRepository.findByIdAndOwner(id, user)
                .filter(note -> !note.isDeleted())
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setContent(dto.getContent());
                    existing.setTags(dto.getTags());
                    return noteRepository.save(existing);
                });
    }


    @Transactional
    public boolean softDeleteNote(Long noteId, User user) {
        return noteRepository.findByIdAndOwnerAndDeletedAtIsNull(noteId, user)
                .map(notes -> {
                    notes.softDelete();
                    noteRepository.save(notes);
                    return true;
                }).orElse(false);
    }

    @Transactional
    public boolean restoreNote(Long noteId, User user) {
        return noteRepository.findByIdAndOwner(noteId, user)
                .filter(Notes::isDeleted)
                .map(notes -> {
                    notes.restore();
                    noteRepository.save(notes);
                    return true;
                }).orElse(false);
    }

    public List<NoteResponseDTO> listNotes(User user) {
        return noteRepository.findAllByOwnerAndDeletedAtIsNull(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<Notes> getNoteById(Long noteId, User user) {
        return noteRepository.findByIdAndOwnerAndDeletedAtIsNull(noteId, user);
    }


    public List<NoteResponseDTO> searchNotes(String search, User user){
        return noteRepository.searchByTitleOrContent(search,user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<NoteResponseDTO> filterByTags(List<String> tags, User owner) {
        return noteRepository.findByTagsIn(tags, owner)
                .stream()
                .map(this::toDto)
                .toList();
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