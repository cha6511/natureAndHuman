package human.nature.customerorderapp.ListData;

import java.util.ArrayList;

public class ItemListData {
    private String desc;
    private String img_url;
    private String name;
    private String no;
    private String price;
    private String originPrice;
    private String sold_yn;
    private String total;
    private String amt;
    private ArrayList<Options> options = new ArrayList<>();
    private String option;
    private String option_no;
    private boolean needRefreshAdapter;

    public ItemListData(String desc, String img_url, String name, String no, String price, String originPrice, ArrayList<Options> options) {
        this.desc = desc;
        this.img_url = img_url;
        this.name = name;
        this.no = no;
        this.price = price;
        this.originPrice = originPrice;
        this.options = options;
        this.amt = "0";
        this.needRefreshAdapter = true;
    }

    public boolean isNeedRefreshAdapter() {
        return needRefreshAdapter;
    }

    public void setNeedRefreshAdapter(boolean needRefreshAdapter) {
        this.needRefreshAdapter = needRefreshAdapter;
    }

    public String getOption_no() {
        return option_no;
    }

    public void setOption_no(String option_no) {
        this.option_no = option_no;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(String originPrice) {
        this.originPrice = originPrice;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold_yn() {
        return sold_yn;
    }

    public void setSold_yn(String sold_yn) {
        this.sold_yn = sold_yn;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<Options> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Options> options) {
        this.options = options;
    }

    public static class Options{
        private String option_name;
        private String option_price;
        private String option_no;
        private String o_cnt;

        public Options(String option_name, String option_price, String option_no, String o_cnt) {
            this.option_name = option_name;
            this.option_price = option_price;
            this.option_no = option_no;
            this.o_cnt = o_cnt;
        }

        public String getO_cnt() {
            return o_cnt;
        }

        public void setO_cnt(String o_cnt) {
            this.o_cnt = o_cnt;
        }

        public String getOption_name() {

            return option_name;
        }

        public void setOption_name(String option_name) {
            this.option_name = option_name;
        }

        public String getOption_price() {
            return option_price;
        }

        public void setOption_price(String option_price) {
            this.option_price = option_price;
        }

        public String getOption_no() {
            return option_no;
        }

        public void setOption_no(String option_no) {
            this.option_no = option_no;
        }
    }
}
