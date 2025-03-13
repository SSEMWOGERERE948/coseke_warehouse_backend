package com.cosek.edms.folders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoldersRepository extends JpaRepository<Folders, Long> {

    // Fetch folder name based on ID
    @Query(value = "SELECT f.folder_name FROM folders f WHERE f.id = :folderId", nativeQuery = true)
    Optional<String> findFolderNameById(@Param("folderId") Long folderId);

    // Fetch all folders assigned to a user (No Department Dependencies)
    @Query(value = """
            SELECT DISTINCT f.* 
            FROM folders f
            JOIN user_folders uf ON f.id = uf.folder_id 
            WHERE uf.user_id = :userId
            """, nativeQuery = true)
    List<Folders> findAllByUserId(@Param("userId") Long userId);
}
