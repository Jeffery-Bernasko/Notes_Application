package org.example.notes_application.repository;

import org.example.notes_application.model.Notes;
import org.example.notes_application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Notes, Long>, JpaSpecificationExecutor<Notes> {
    Page<Notes> findAllByOwnerAndDeletedAtIsNull(User owner, Pageable pageable);

    Page<Notes> findAllByOwnerAndDeletedAtIsNotNull(User owner, Pageable pageable);

    @Query("SELECT n FROM Notes n WHERE n.owner = :owner AND n.deletedAt IS NULL AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Notes> searchByTitleOrContent(@Param("search") String search, @Param("owner") User owner, Pageable pageable);

    @Query("SELECT DISTINCT n FROM Notes n JOIN n.tags t WHERE n.owner = :owner AND n.deletedAt IS NULL AND t IN :tags")
    Page<Notes> findByTagsIn(@Param("tags") List<String> tags, @Param("owner") User owner, Pageable pageable);


    Optional<Notes> findByIdAndOwnerAndDeletedAtIsNull(Long id, User owner);

    Optional<Notes> findByIdAndOwner(Long id, User owner);

    List<Notes> findAllByOwnerAndDeletedAtIsNull(User owner);

    boolean existsByIdAndOwner(Long id, User owner);

    @Query("SELECT n FROM Notes n WHERE n.owner = :owner AND n.deletedAt IS NULL AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Notes> searchByTitleOrContent(@Param("search") String search, @Param("owner") User user);

    @Query("SELECT n FROM Notes n " +
            "JOIN n.tags t " +
            "WHERE n.owner = :owner AND n.deletedAt IS NULL " +
            "AND t IN :tags")
    List<Notes> findByTagsIn(@Param("tags") List<String> tags, @Param("owner") User user);
}
