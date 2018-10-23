package human.nature.customerorderapp.ListData;

public class NotiThumbData {
    private String no;
    private String title;
    private String add_time;

    public NotiThumbData(String no, String title, String add_time) {
        this.no = no;
        this.title = title;
        this.add_time = add_time;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

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
}
