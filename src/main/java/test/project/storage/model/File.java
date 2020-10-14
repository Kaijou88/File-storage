package test.project.storage.model;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "storage", type = "files")
public class File implements Serializable {
    @Id
    private String id;
    private String name;
    private Long size;
    private String[] tags;

    public File() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public JsonObject toJson() {
        JsonArrayBuilder jsArray = Json.createArrayBuilder();
        if (tags != null) {
            for (String tag : tags) {
                jsArray.add(tag);
            }
        }
        return Json.createObjectBuilder()
                .add("ID", id)
                .add("name", name)
                .add("size", size)
                .add("tags", jsArray.build())
                .build();
    }
}
