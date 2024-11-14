package com.cosek.edms.departments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByDepartmentName(String departmentName);
    Optional<Department> findByDepartmentName(String departmentName);

    @Query(value = "SELECT d.* FROM departments d JOIN user_departments ud ON d.id = ud.department_id WHERE ud.user_id = :id", nativeQuery = true)
    List<Department> findAllByUserId(@Param("id") Long id);

}