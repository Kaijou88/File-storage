package test.project.storage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import test.project.storage.model.File;
import test.project.storage.repository.FileRepository;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public Page<File> getAll(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }

    @Override
    public File create(File file) {
        return fileRepository.save(file);
    }

    @Override
    public void delete(String id) {
        fileRepository.deleteById(id);
    }

    @Override
    public Optional<File> findById(String id) {
        return fileRepository.findById(id);
    }

    @Override
    public void update(File file) {
        fileRepository.save(file);
    }

    @Override
    public Page<File> findFilesByTags(String[] tags, Pageable pageable) {
        Page<File> filesByTags = fileRepository.findFilesByTagsIn(tags, pageable);
        List<File> newFiles = new ArrayList<>();
        for (File file : filesByTags) {
            int count = tags.length;
            for (int j = 0; j < file.getTags().length; j++) {
                for (int i = 0; i < tags.length; i++) {
                    if (file.getTags()[j].equals(tags[i])) {
                        count--;
                    }
                }
            }
            if (count == 0) {
                newFiles.add(file);
            }
        }
        return new PageImpl<>(newFiles);
    }
}
