package human.nature.customerorderapp.ListData;

public class CartData {
    private String img_url;
    private String item_name;
    private String item_option;
    private String item_amt;
    private String item_price;
    private String item_no;
    private String item_option_no;

    public CartData(String img_url, String item_name, String item_option, String item_amt, String item_price, String item_no, String item_option_no) {
        this.img_url = img_url;
        this.item_name = item_name;
        this.item_option = item_option;
        this.item_amt = item_amt;
        this.item_price = item_price;
        this.item_no = item_no;
        this.item_option_no = item_option_no;
    }

    public String getItem_option_no() {
        return item_option_no;
    }

    public void setItem_option_no(String item_option_no) {
        this.item_option_no = item_option_no;
    }

    public String getItem_no() {
        return item_no;
    }

    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }

    public String getItem_option() {
        return item_option;
    }

    public void setItem_option(String item_option) {
        this.item_option = item_option;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_amt() {
        return item_amt;
    }

    public void setItem_amt(String item_amt) {
        this.item_amt = item_amt;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }
}
