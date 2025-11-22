package org.example.notes_application.repository;

import org.example.notes_application.model.Notes;
import org.example.notes_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Notes, Long>, JpaSpecificationExecutor<Notes> {
    Optional<Notes> findByIdAndOwnerAndDeletedAtIsNull(Long id, User owner);

    Optional<Notes> findByIdAndOwner(Long id, User owner);

    List<Notes> findAllByOwnerAndDeletedAtIsNull(User owner);

    boolean existsByIdAndOwner(Long id, User owner);
}
