package com.cosek.edms.folders;

import com.cosek.edms.casestudy.CaseStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoldersRepository extends JpaRepository<Folders, Long> {
    @Query(value = "SELECT folder_name FROM folders WHERE id = :folderId", nativeQuery = true)
    Folders findFolderById(@Param("folderId") Long caseStudyId);
}
