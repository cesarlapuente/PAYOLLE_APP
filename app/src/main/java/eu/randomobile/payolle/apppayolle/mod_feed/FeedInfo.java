package eu.randomobile.payolle.apppayolle.mod_feed;

/**
 * Created by 44 screens on 28/09/2016.
 */
public class FeedInfo {
    private String id;
    private String title;
    private String body;
    private String cat;

    public FeedInfo(String id,String title,String body,String cat){
        this.id = id;
        this.title = title;
        this.body = body;
        this.cat = cat;

    }
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
