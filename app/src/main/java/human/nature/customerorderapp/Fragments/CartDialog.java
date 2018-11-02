package human.nature.customerorderapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Adapters.CartListAdapter;
import human.nature.customerorderapp.AppSharedPreference;
import human.nature.customerorderapp.EventBus.Events;
import human.nature.customerorderapp.EventBus.GlobalBus;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.MainActivity;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StaticDatas;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static human.nature.customerorderapp.EventBus.Events.DIALOG_CLOSE;
import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

public class CartDialog extends Dialog implements View.OnClickListener {
    public CartDialog(@NonNull Context context) {
        super(context);
    }

    Button close;
    Button order;

    TextView total_price;

    RecyclerView list;
    CartListAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    StaticDatas staticDatas = StaticDatas.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        list = findViewById(R.id.list);
        adapter = new CartListAdapter(getContext(), staticDatas.cartData, this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(adapter);
        list.setLayoutManager(linearLayoutManager);

        close = findViewById(R.id.close);
        close.setOnClickListener(this);

        order = findViewById(R.id.order);
        order.setOnClickListener(this);

        total_price = findViewById(R.id.total_price);

        resultPrice();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.close:
                CartDialog.this.dismiss();
                break;

            case R.id.order:
                String price = "";
                String name = "";
                String cnt = "";
                String option = "";
                String no = "";
                String op_no = "";
                int total_price = 0;
                for (int i = 0; i < staticDatas.cartData.size(); i++) {
                    price += (Integer.parseInt(staticDatas.cartData.get(i).getItem_price()) * Integer.parseInt(staticDatas.cartData.get(i).getItem_amt())) + "/";
                    name += staticDatas.cartData.get(i).getItem_name() + "/";
                    cnt += staticDatas.cartData.get(i).getItem_amt() + "/";
                    option += staticDatas.cartData.get(i).getItem_option() + "/";
                    op_no += staticDatas.cartData.get(i).getItem_option_no() + "/";
                    no += staticDatas.cartData.get(i).getItem_no() + "/";
                    total_price += Integer.parseInt(staticDatas.cartData.get(i).getItem_price()) * Integer.parseInt(staticDatas.cartData.get(i).getItem_amt());
                }
                MakeOrderAsyncTask makeOrderAsyncTask = new MakeOrderAsyncTask(getContext(), new AsyncDone() {
                    @Override
                    public void asyncDone(String result) {
                        if (SUCCESS.equals(result)) {
                            staticDatas.cartData.clear();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "주문되었습니다.\n상단의 '주문목록'을 확인해주세요.", Toast.LENGTH_SHORT).show();
                            CartDialog.this.dismiss();
                        } else if("CNT NOT ENOUGH".equals(result)){
                            Toast.makeText(getContext(), "재고수량이 부족합니다.\n재고 수량을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(), "통신에 장애가있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                makeOrderAsyncTask.execute(String.valueOf(total_price), price, name, cnt, option, no, op_no);
                break;

            case R.id.delete:
                int pos = (int) view.getTag();
                staticDatas.cartData.remove(pos);
                adapter.notifyDataSetChanged();
                resultPrice();
                break;
        }
    }

    public void resultPrice() {
        int resultPrice = 0;
        for (int i = 0; i < staticDatas.cartData.size(); i++) {
            resultPrice += Integer.parseInt(staticDatas.cartData.get(i).getItem_price()) * Integer.parseInt(staticDatas.cartData.get(i).getItem_amt());
        }
        total_price.setText("합계 : " + String.format("%,d", resultPrice) + " 원");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Events.Msg msg = new Events.Msg(DIALOG_CLOSE);
        GlobalBus.getBus().post(msg);
    }

    class MakeOrderAsyncTask extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        AppSharedPreference spf;

        public MakeOrderAsyncTask(Context context, AsyncDone asyncDone) {
            this.context = context;
            this.asyncDone = asyncDone;
            this.spf = new AppSharedPreference(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog.Builder()
                    .setContext(context)
                    .setMessage("로딩중입니다...")
                    .setCancelable(false)
                    .setTheme(R.style.SpotsDialog)
                    .build();
//            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Log.d("order info", strings[0] + "\n" + strings[1] + "\n" + strings[2] + "\n" + strings[3] + "\n" + strings[5] + "\n" + strings[4] + "\n" + strings[6]);
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            formBodyBuilder.add("u_no", spf.getUserSeq());
            formBodyBuilder.add("s_no", staticDatas.s_no);
            formBodyBuilder.add("total_price", strings[0]);
            formBodyBuilder.add("item_price", strings[1]);
            formBodyBuilder.add("item_name", strings[2]);
            formBodyBuilder.add("item_cnt", strings[3]);
            formBodyBuilder.add("item_no", strings[5]);
            formBodyBuilder.add("item_option", strings[4]);
            formBodyBuilder.add("item_option_no", strings[6]);
            FormBody formBody = formBodyBuilder.build();
            try {
                Request request = new Request.Builder().url(staticDatas.baseUrl + "order/c_order").post(formBody).build();
                Response response = client.newCall(request).execute();
                String myResponse = response.body().string();
                return myResponse;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d("makeOrder result", s);
                JSONArray resultArray = new JSONArray(s);
                JSONArray innerArray = resultArray.getJSONArray(0);
                JSONObject result = innerArray.getJSONObject(0);
                if (SUCCESS.equals(result.getString("result"))) {
                    asyncDone.asyncDone(SUCCESS);
                } else if("CNT NOT ENOUGH".equals(result.getString("result"))) {
                    asyncDone.asyncDone("CNT NOT ENOUGH");
                } else{
                    asyncDone.asyncDone(FAILED);
                }
            } catch (Exception e) {
                asyncDone.asyncDone(FAILED);
            } finally {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }
    }
}
