package human.nature.customerorderapp.ListData;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderData {
    private String no;
    private String total_price;
    private String store_no;
    private String status;
    private String order_time;
    private String pay_time;
    private String cancel_time;
    private String cancel_yn;
    private String end_time;
    private String product_names;
    private String product_prices;
    private String options;
    private String count;
    private String bank;

    public OrderData(String no, String total_price, String store_no, String status, String order_time, String pay_time, String cancel_time, String cancel_yn, String end_time, String product_names, String product_prices, String options, String count, String bank) {
        this.no = no;
        this.total_price = total_price;
        this.store_no = store_no;
        this.status = status;
        this.order_time = order_time;
        this.pay_time = pay_time;
        this.cancel_time = cancel_time;
        this.cancel_yn = cancel_yn;
//        try{
//            this.cancel_type = cancel_yn.getString("type");
//            JSONArray dataArray = cancel_yn.getJSONArray("data");
//            this.cancel_data = dataArray.getString(0);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        this.end_time = end_time;
        this.product_names = product_names;
        this.product_prices = product_prices;
        this.options = options;
        this.count = count;
        this.bank = bank;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCancel_yn(String cancel_yn) {
        this.cancel_yn = cancel_yn;
    }

    public String getStore_no() {
        return store_no;
    }

    public String getNo() {
        return no;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getStatus() {
        return status;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getPay_time() {
        return pay_time;
    }

    public String getCancel_time() {
        return cancel_time;
    }

    public String getCancel_yn() {
        return cancel_yn;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getProduct_names() {
        return product_names;
    }

    public String getProduct_prices() {
        return product_prices;
    }

    public String getOptions() {
        return options;
    }

    public String getCount() {
        return count;
    }

    public String getBank() {
        return bank;
    }
}
