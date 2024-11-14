package com.cosek.edms.folders;

import com.cosek.edms.departments.Department;
import com.cosek.edms.departments.DepartmentRepository;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoldersService {

    private final FoldersRepository foldersRepository;

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;




    public Folders addFolder(Folders folder) {
        return foldersRepository.save(folder);
    }

    public Optional<Folders> getFolderById(Long id) {
        return foldersRepository.findById(id);
    }

    public Folders findFolderById(Long folderId) {
        return foldersRepository.findFolderById(folderId);
    }


    public List<Folders> getAllFolders() {
        return foldersRepository.findAll();
    }

    public Folders updateFolder(Long id, Folders updatedFolder) {
        return foldersRepository.findById(id)
                .map(folder -> {
                    folder.setFolderName(updatedFolder.getFolderName());
                    return foldersRepository.save(folder);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found with id " + id));
    }

    public List<Folders> updateMultipleFolders(List<Folders> folders) {
        return foldersRepository.saveAll(folders);
    }

    public void deleteFolder(Long id) {
        foldersRepository.deleteById(id);
    }

    public void deleteMultipleFolders(List<Long> ids) {
        foldersRepository.deleteAllById(ids);
    }

    @Transactional
    public void assignFoldersToDepartment(Long departmentId, List<Long> folderIds)
            throws ResourceNotFoundException {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        List<Folders> folders = foldersRepository.findAllById(folderIds);
        if (folders.isEmpty()) {
            throw new ResourceNotFoundException("Folders not found with the provided IDs.");
        }

        // Add new folders to existing ones
        Set<Folders> currentFolders = new HashSet<>(department.getFolders());
        currentFolders.addAll(folders);
        department.setFolders(new ArrayList<>(currentFolders));

        departmentRepository.save(department);
    }

    @Transactional
    public void unassignFoldersFromDepartment(Long departmentId, List<Long> folderIds)
            throws ResourceNotFoundException {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        List<Folders> foldersToRemove = foldersRepository.findAllById(folderIds);
        if (foldersToRemove.isEmpty()) {
            throw new ResourceNotFoundException("Folders not found with the provided IDs.");
        }

        // Remove specified folders from the department
        Set<Folders> currentFolders = new HashSet<>(department.getFolders());
        currentFolders.removeAll(foldersToRemove);
        department.setFolders(new ArrayList<>(currentFolders));

        departmentRepository.save(department);
    }

    public List<Folders> getAssignedFolders(Long departmentId) throws ResourceNotFoundException {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        return department.getFolders();
    }


    public List<Folders> getfoldersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Get folders for all departments the user belongs to
        return foldersRepository.findAllByUserId(userId);
    }
}
