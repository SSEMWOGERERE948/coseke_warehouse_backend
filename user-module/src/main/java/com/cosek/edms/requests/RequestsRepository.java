package com.cosek.edms.requests;

import com.cosek.edms.files.Files;
import com.cosek.edms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> {
    List<Requests> findByFilesAndUser(Files file, User user);
}
