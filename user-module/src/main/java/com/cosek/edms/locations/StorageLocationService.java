package com.cosek.edms.locations;

import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.folders.FoldersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageLocationService {
    private final StorageLocationRepository storageLocationRepository;
    private final FoldersRepository foldersRepository;

    @Transactional
    public StorageLocation createStorageLocation(StorageLocation location, Long parentId) {
        if (parentId != null) {
            StorageLocation parent = null;
            try {
                parent = storageLocationRepository.findById(parentId)
                        .orElseThrow(() -> new NotFoundException("Parent location not found"));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
            location.setParent(parent);
        }
        return storageLocationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public StorageLocation getStorageLocationById(Long id) throws NotFoundException {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<StorageLocation> getAllStorageLocations() {
        return storageLocationRepository.findAll();
    }

    @Transactional
    public void deleteStorageLocation(Long id) throws NotFoundException {
        StorageLocation location = getStorageLocationById(id);
        storageLocationRepository.deleteById(id);
    }

    @Transactional
    public StorageLocation assignFoldersToLocation(Long locationId, List<Long> folderIds) throws NotFoundException {
        StorageLocation location = getStorageLocationById(locationId);
        List<Folders> folders = foldersRepository.findAllById(folderIds);
        location.setFolders(folders);
        return storageLocationRepository.save(location);
    }
}
