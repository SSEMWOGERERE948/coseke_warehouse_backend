package com.cosek.edms.folders;

import com.cosek.edms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoldersService {

    private final FoldersRepository foldersRepository;

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
}
