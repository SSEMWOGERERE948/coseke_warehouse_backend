package com.cosek.edms.departments;

import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;


    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new IllegalArgumentException("Department with name " + department.getDepartmentName() + " already exists");
        }
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) throws NotFoundException {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Department getDepartmentByName(String departmentName) throws NotFoundException {
        return departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new NotFoundException("Department not found with name: " + departmentName));
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentName) throws NotFoundException {
        Department department = getDepartmentById(id);

        if (!department.getDepartmentName().equals(departmentName.getDepartmentName()) &&
                departmentRepository.existsByDepartmentName(departmentName.getDepartmentName())) {
            throw new IllegalArgumentException("Department name already exists");
        }

        department.setDepartmentName(departmentName.getDepartmentName());

        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id) throws NotFoundException {
        Department department = getDepartmentById(id);
        if (!department.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete department with assigned users"+ department.getDepartmentName());
        }
        departmentRepository.deleteById(id);
    }

    public User assignUserDepartments(Long userId, List<String> departmentNames) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<Department> departments = user.getDepartments();

        for (String departmentName : departmentNames) {
            Department department = departmentRepository.findByDepartmentName(departmentName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department: " + departmentName));

            departments.add(department);
        }

        user.setDepartments(departments);
        return userRepository.save(user);
    }

}