package human.nature.customerorderapp;

import java.util.ArrayList;

import human.nature.customerorderapp.ListData.CartData;
import human.nature.customerorderapp.ListData.ItemListData;
import human.nature.customerorderapp.ListData.OrderData;

public class StaticDatas {
    public static StaticDatas staticDatas;

    public static ArrayList<ItemListData> itemListData;
    public static ArrayList<OrderData> orderData;
    public static ArrayList<CartData> cartData;
    public static final String baseUrl = "https://app.thehandsgift.com:3000/";
    public static String s_no;
    public static String s_name;


    public static StaticDatas getInstance(){
        if(staticDatas == null){
            staticDatas = new StaticDatas();
            itemListData = new ArrayList<>();
            orderData = new ArrayList<>();
            cartData = new ArrayList<>();
        }
        return staticDatas;
    }

}
