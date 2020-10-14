package test.project.storage.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import test.project.storage.model.File;

public interface FileService {
    Page<File> getAll(Pageable pageable);

    File create(File file);

    void delete(String id);

    Optional<File> findById(String id);

    void update(File file);

    Page<File> findFilesByTags(String[] tags, Pageable pageable);
}
