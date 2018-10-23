package human.nature.customerorderapp.ListData;

public class NotiData {
    private String title;
    private String add_time;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotiData(String title, String add_time, String content) {

        this.title = title;
        this.add_time = add_time;
        this.content = content;
    }
}
