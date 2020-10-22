package test.project.storage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import test.project.storage.model.File;

public interface FileRepository extends ElasticsearchRepository<File, String> {
    Page<File> findFilesByTagsIn(String[] tags, Pageable pageable);

    Page<File> findAll(Pageable pageable);
}
