package human.nature.customerorderapp.Interface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import human.nature.customerorderapp.ListData.ItemListData;
import human.nature.customerorderapp.StaticDatas;

public class ItemListJsonParser {
    JSONArray jsonArray;
    StaticDatas staticDatas = StaticDatas.getInstance();
    ItemListData itemListData;
    public ItemListJsonParser(JSONArray jsonArray){
        this.jsonArray = jsonArray;
    }

    public void inputToItemListData() throws Exception{
//        staticDatas.itemListData.clear();
        ArrayList<ItemListData> tmp = new ArrayList<>();
        for(int i = 0 ; i < jsonArray.length() ; i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ArrayList<ItemListData.Options> options = new ArrayList<>();

            JSONArray optionsArray = jsonObject.getJSONArray("options");
            for(int j = 0 ; j < optionsArray.length() ; j++){
                JSONObject optionsObject = optionsArray.getJSONObject(j);
                options.add(new ItemListData.Options(
                        optionsObject.getString("name"),
                        optionsObject.getString("price"),
                        optionsObject.getString("option_no"),
                        optionsObject.getString("o_cnt")
                ));
            }

            itemListData = new ItemListData(
                    jsonObject.getString("desc"),
                    jsonObject.getString("img_url"),
                    jsonObject.getString("name"),
                    jsonObject.getString("no"),
                    jsonObject.getString("price"),
                    jsonObject.getString("price"), //originPrice
//                    jsonObject.getString("sold_yn"),
//                    jsonObject.getString("total_cnt"),
                    options
            );
            tmp.add(itemListData);
        }

        staticDatas.itemListData.addAll(tmp);
    }
}
