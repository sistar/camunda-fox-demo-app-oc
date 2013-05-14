package cfda;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AuditPost {
    private String id;
    private String post;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
