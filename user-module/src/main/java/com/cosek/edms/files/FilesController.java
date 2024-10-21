package com.cosek.edms.files;

import com.cosek.edms.casestudy.CaseStudy;
import com.cosek.edms.casestudy.CaseStudyRepository;
import com.cosek.edms.casestudy.CaseStudyService;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.folders.FoldersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;

    @Autowired
    private CaseStudyService caseStudyService;

    @Autowired
    private final FoldersService foldersService;



    @PostMapping("/add")
    public ResponseEntity<Files> addFile(@RequestBody Files file) {
        Files savedFile = filesService.addFile(file);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Files> getFileById(@PathVariable Long id) {
        Optional<Files> file = filesService.getFileById(id);
        return file.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("all")
    public ResponseEntity<List<Files>> getAllFiles() {
        List<Files> files = filesService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("all/{id}")
    public ResponseEntity<List<Files>> getAllFilesById(@PathVariable Long id) {
        List<Files> files = filesService.getFilesByCreator(id);
        return ResponseEntity.ok(files);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Files> updateFile(@PathVariable Long id, @RequestBody Files updatedFile) {
        Files file = filesService.updateFile(id, updatedFile);
        return ResponseEntity.ok(file);
    }

    @PutMapping("/update-multiple")
    public ResponseEntity<List<Files>> updateMultipleFiles(@RequestBody List<Files> files) {
        List<Files> updatedFiles = filesService.updateMultipleFiles(files);
        return ResponseEntity.ok(updatedFiles);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        filesService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleFiles(@RequestBody List<Long> ids) {
        filesService.deleteMultipleFiles(ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fileId}/assign-case-study/{caseStudyId}")
    public ResponseEntity<Files> assignFileToCaseStudy(
            @PathVariable Long fileId,
            @PathVariable Long caseStudyId) {

        Files updatedFile = filesService.assignFileToCaseStudy(fileId, caseStudyId);
        return ResponseEntity.ok(updatedFile);
    }

    @PutMapping("/{fileId}/assign-folder/{folderId}")
    public ResponseEntity<Files> assignFileToFolder(
            @PathVariable Long fileId,
            @PathVariable Long folderId) {

        Files updatedFile = filesService.assignFileToFolder(fileId, folderId);
        return ResponseEntity.ok(updatedFile);
    }
}
