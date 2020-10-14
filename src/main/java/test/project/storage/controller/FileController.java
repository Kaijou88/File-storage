package test.project.storage.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<String> createFile(@RequestBody File file) {
        String json;
        if (file.getName() == null || file.getSize() == null) {
            json = Json.createObjectBuilder()
                    .add("success", false)
                    .add("error", "Name and size are mandatory fields")
                    .build().toString();
            return new ResponseEntity<String>(json, HttpStatus.BAD_REQUEST);
        } else if (file.getSize() <= 0) {
            json = Json.createObjectBuilder()
                    .add("success", false)
                    .add("error", "Size should be higher then zero")
                    .build().toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        File newFile = fileService.create(file);
        json = Json.createObjectBuilder()
                .add("ID", newFile.getId())
                .build().toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{ID}")
    public ResponseEntity<String> deleteFile(@PathVariable("ID") String index) {
        String json;
        if (fileService.findById(index).isEmpty()) {
            json = Json.createObjectBuilder()
                    .add("success", false)
                    .add("error", "file not found")
                    .build().toString();
            return new ResponseEntity<>(json,HttpStatus.NOT_FOUND);
        }
        fileService.delete(index);
        json = Json.createObjectBuilder()
                .add("success", true)
                .build().toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PostMapping(value = "/{ID}/tags")
    public ResponseEntity<String> addTags(@PathVariable("ID") String index,
                                          @RequestBody File file) {
        File newFile = fileService.findById(index).get();
        newFile.setTags(file.getTags());
        fileService.update(newFile);
        String json = Json.createObjectBuilder()
                .add("success", true)
                .build().toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{ID}/tags")
    public ResponseEntity<String> deleteTags(@PathVariable("ID") String index,
                                             @RequestBody File file) {
        File newFile = fileService.findById(index).get();
        Set<String> difference = new HashSet<>(Arrays.asList(newFile.getTags()));
        difference.removeAll(Arrays.asList(file.getTags()));
        String json;
        if (difference.size() == newFile.getTags().length) {
            json = Json.createObjectBuilder()
                    .add("success", false)
                    .add("error", "tag not found on file")
                    .build().toString();
            return new ResponseEntity<>(json,HttpStatus.BAD_REQUEST);
        }
        newFile.setTags(difference.toArray(String[]::new));
        fileService.update(newFile);
        json = Json.createObjectBuilder()
                .add("success", true)
                .build().toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<String> getAllByTags(String[] tags, @PageableDefault Pageable pageable) {
        Page<File> all;
        if (tags == null) {
            all = fileService.getAll(pageable);
        } else {
            all = fileService.findFilesByTags(tags, pageable);
        }
        long totalElements = all.getTotalElements();
        JsonArrayBuilder arrayTags = Json.createArrayBuilder();
        for (File file : all.getContent()) {
            arrayTags.add(file.toJson());
        }

        String json = Json.createObjectBuilder()
                .add("total", totalElements)
                .add("page", arrayTags.build())
                .build().toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
