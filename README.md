# File Storage

# Context
[Project's purpose](#purpose)

[Project's structure](#structure)

[For developer](#developer)

[Author](#author)

# <a name="purpose"></a>Project's purpose
This file storage is a small REST web-service that allows us to work with file content (name, size and tags).
This test project has the following functionality:
- add new files;
- delete files by ID;
- assign tags to file;
- remove tags from the file;
- list files filtered by tags.

# <a name="structure"></a>Project's structure
- Java 11
- Maven
- Travis
- Spring Boot 2.3.4.RELEASE
- Elasticsearch 2.3.4.RELEASE

# <a name="developer"></a>For developer
Open the project in your IDE as a maven project.

Download Elasticsearch and open /bin/elasticsearch.bat.

Run the project.

For testing the functionality of the project can be used Postman or other similar programs.
In the Header must be added Key with "Content-Type" and Value with "application/json".

To add a file should be used POST method, url = http://localhost:8080/file and body in json format that should have two mandatory fields (name and size).

To delete a file should be used DELETE method and url = http://localhost:8080/file/{ID}. Instead of {ID} should be added a file's id.

To assign tags to file should be used POST method, url = http://localhost:8080/file/{ID}/tags and body in json format that should have one mandatory fields (tags). 
List them in following format: ["tag1", "tag2", "tag3"]. Instead of {ID} should be added a file's id.

To remove tags from the file should be used DELETE method, url = http://localhost:8080/file/{ID}/tags and body in json format that should have one mandatory fields (tags). 
List them in following format: ["tag1", "tag2", "tag3"]. Instead of {ID} should be added a file's id.

To list files filtered by tags should be used GET method and url = http://localhost:8080/file?tags=tag1,tag2&page=2&size=3. 
Tags, page and size are optional. By default, will be shown a total amount of file and first page with 10 files.


# <a name="author"></a>Author
[Maryna Franchuk](https://github.com/Kaijou88)
