package com.cosek.edms.requests;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> {

    // ✅ Fetch requests made by a specific user
    List<Requests> findByUserId(Long userId);

    // ✅ Fetch requests by organization
    List<Requests> findByOrganizationId(Long organizationId);

    // ✅ Fetch requests by box number
    List<Requests> findByBoxNumber(int boxNumber);

    Optional<Requests> findByFileIdAndStatus(Long fileId, String status);

    @Transactional
    void deleteByFileId(Long fileId);

    boolean existsByFileIdAndUserIdAndStatus(Long fileId, Long userId, String status);
    // ✅ Fetch all requests for admin
    List<Requests> findAll();

}
