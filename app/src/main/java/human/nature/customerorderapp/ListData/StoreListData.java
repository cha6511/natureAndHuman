package human.nature.customerorderapp.ListData;

public class StoreListData {
    private String store_name;
    private String store_no;
    private String acpt_yn;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_no() {
        return store_no;
    }

    public void setStore_no(String store_no) {
        this.store_no = store_no;
    }

    public String getAcpt_yn() {
        return acpt_yn;
    }

    public void setAcpt_yn(String acpt_yn) {
        this.acpt_yn = acpt_yn;
    }

    public StoreListData(String store_name, String store_no, String acpt_yn) {

        this.store_name = store_name;
        this.store_no = store_no;
        this.acpt_yn = acpt_yn;
    }
}
