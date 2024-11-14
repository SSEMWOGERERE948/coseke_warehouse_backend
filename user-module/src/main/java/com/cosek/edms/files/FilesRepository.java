package com.cosek.edms.files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
    List<Files> findByCreatedBy(Long createdBy);

    @Query("SELECT f FROM Files f " +
            "JOIN f.folder fld " +
            "JOIN fld.departments dept " +
            "WHERE dept.id IN :departmentIds")
    List<Files> findFilesByDepartmentIdsAndFolders(@Param("departmentIds") List<Long> departmentIds);
}
