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
    List<Files> findByOrganizationId(Long organizationId);

    @Query(value = """
    SELECT 
        sl3.name AS rack_name,
        sl2.name AS shelf_name,
        sl1.name AS archival_box_name
    FROM storage_locations sl1
    LEFT JOIN storage_locations sl2 ON sl1.parent_id = sl2.id  -- Shelf
    LEFT JOIN storage_locations sl3 ON sl2.parent_id = sl3.id  -- Rack
    WHERE sl1.id IN (
        SELECT DISTINCT f.archival_box_id 
        FROM files f 
        WHERE f.organization_id = :organizationId
    )
    """, nativeQuery = true)
    List<Object[]> findArchivalBoxesWithHierarchyByOrganization(@Param("organizationId") Long organizationId);

    @Query(value = """
    SELECT 
        sl3.name AS rack_name,
        sl2.name AS shelf_name,
        sl1.name AS archival_box_name
    FROM storage_locations sl1
    LEFT JOIN storage_locations sl2 ON sl1.parent_id = sl2.id  -- Shelf
    LEFT JOIN storage_locations sl3 ON sl2.parent_id = sl3.id  -- Rack
    WHERE sl1.type = 'Archival Box'
    """, nativeQuery = true)
    List<Object[]> findAllArchivalBoxesWithHierarchy();
    
}
