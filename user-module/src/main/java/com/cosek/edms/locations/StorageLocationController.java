package com.cosek.edms.locations;

import com.cosek.edms.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storage-locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StorageLocationController {
    private final StorageLocationService storageLocationService;

    @PostMapping("/create")
    public ResponseEntity<StorageLocation> createStorageLocation(
            @RequestBody StorageLocation location,
            @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(storageLocationService.createStorageLocation(location, parentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocation> getStorageLocationById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(storageLocationService.getStorageLocationById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StorageLocation>> getAllStorageLocations() {
        return ResponseEntity.ok(storageLocationService.getAllStorageLocations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStorageLocation(@PathVariable Long id) throws NotFoundException {
        storageLocationService.deleteStorageLocation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-folders")
    public ResponseEntity<StorageLocation> assignFoldersToLocation(
            @RequestParam Long locationId,
            @RequestBody List<Long> folderIds) throws NotFoundException {
        return ResponseEntity.ok(storageLocationService.assignFoldersToLocation(locationId, folderIds));
    }
}
