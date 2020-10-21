package test.project.storage.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.common.util.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.project.storage.model.File;
import test.project.storage.service.FileService;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createFile(@RequestBody File file) {
        if (file.getName() == null || file.getSize() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false,
                    "error", "Name and size are mandatory fields"));
        } else if (file.getSize() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false,
                    "error", "Size can not be negative"));
        }
        File newFile = fileService.create(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("ID", newFile.getId()));
    }

    @DeleteMapping(value = "/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteFile(@PathVariable("ID") String index) {
        if (fileService.findById(index).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false,
                            "error", "File not found"));
        }
        fileService.delete(index);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("success", true));
    }

    @PostMapping(value = "/{ID}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addTags(@PathVariable("ID") String index,
                                          @RequestBody File file) {
        if (fileService.findById(index).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false,
                            "error", "Entity with id = " + index + " is not found"));
        }
        File newFile = fileService.findById(index).get();
        if (newFile.getTags() == null) {
            newFile.setTags(file.getTags());
        } else {
            String[] newTags = Arrays.stream(ArrayUtils.concat(newFile.getTags(), file.getTags()))
                    .distinct()
                    .toArray(String[]::new);
            newFile.setTags(newTags);
        }
        fileService.update(newFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("success", true));
    }

    @DeleteMapping(value = "/{ID}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteTags(@PathVariable("ID") String index,
                                             @RequestBody File file) {
        if (fileService.findById(index).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false,
                            "error", "Entity with id = " + index + " is not found"));
        }

        File newFile = fileService.findById(index).get();
        Set<String> difference = new HashSet<>(Arrays.asList(newFile.getTags()));
        difference.removeAll(Arrays.asList(file.getTags()));
        if (difference.size() == newFile.getTags().length) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false,
                            "error", "Tag not found in file"));
        }
        newFile.setTags(difference.toArray(String[]::new));
        fileService.update(newFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("success", true));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllByTags(String[] tags, @PageableDefault Pageable pageable) {
        Page<File> all;
        if (tags == null) {
            all = fileService.getAll(pageable);
        } else {
            all = fileService.findFilesByTags(tags, pageable);
        }
        return ResponseEntity.status(HttpStatus.OK)
                 .body(Map.of("total", all.getTotalElements(),
                         "page", all.getContent()));
    }
}
