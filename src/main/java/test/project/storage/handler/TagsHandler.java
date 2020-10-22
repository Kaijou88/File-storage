package test.project.storage.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.elasticsearch.common.util.ArrayUtils;
import test.project.storage.model.File;

public class TagsHandler {
    public static File addingTagsToFile(File file, String tags) {
        if (tags.charAt(0) == '{') {
            tags = tags.split(":")[1];
        }
        String[] listTags = tags.replaceAll("[\\[\\]}\" ]", "")
                .split(",");

        if (file.getTags() == null) {
            file.setTags(listTags);
        } else {
            String[] newTags = Arrays.stream(ArrayUtils.concat(file.getTags(), listTags))
                    .distinct()
                    .toArray(String[]::new);
            file.setTags(newTags);
        }
        return file;
    }

    public static File deletingTagsFromFile(File file, String[] tags) {
        Set<String> difference = new HashSet<>(Arrays.asList(file.getTags()));
        difference.removeAll(Arrays.asList(tags));
        file.setTags(difference.toArray(String[]::new));
        return file;
    }
}
