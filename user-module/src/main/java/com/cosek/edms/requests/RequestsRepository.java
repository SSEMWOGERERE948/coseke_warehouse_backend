package com.cosek.edms.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> {

    // ✅ Fetch requests made by a specific user
    List<Requests> findByUserId(Long userId);

    // ✅ Fetch requests by organization
    List<Requests> findByOrganizationId(Long organizationId);

    // ✅ Fetch requests by box number
    List<Requests> findByBoxNumber(int boxNumber);

    // ✅ Fetch all requests for admin
    List<Requests> findAll();
}
