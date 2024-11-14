package com.cosek.edms.folders;

import com.cosek.edms.casestudy.CaseStudy;
import com.cosek.edms.departments.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoldersRepository extends JpaRepository<Folders, Long> {
    @Query(value = "SELECT folder_name FROM folders WHERE id = :folderId", nativeQuery = true)
    Folders findFolderById(@Param("folderId") Long caseStudyId);

    @Query(value = """
            SELECT DISTINCT f.* 
            FROM folders f 
            JOIN department_folders df ON f.id = df.folder_id 
            JOIN user_departments ud ON df.department_id = ud.department_id 
            WHERE ud.user_id = :userId
            """, nativeQuery = true)
    List<Folders> findAllByUserId(@Param("userId") Long userId);


}
