package com.cosek.edms.filecategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileCategoryRepository extends JpaRepository<FileCategory, Long> {
    @Query(value = "SELECT user_id FROM file_category_user WHERE file_category_id = :fileCategoryId", nativeQuery = true)
    List<Long> findAssignedUserIdsByFileCategoryId(@Param("fileCategoryId") Long fileCategoryId);

    @Query(value = "SELECT description,name FROM file_category WHERE id = :fileCategoryId", nativeQuery = true)
    FileCategory findFileCategoryById(@Param("fileCategoryId") Long fileCategoryId);
}
