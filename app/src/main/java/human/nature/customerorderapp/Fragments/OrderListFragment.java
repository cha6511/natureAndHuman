package human.nature.customerorderapp.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Adapters.OrderListAdapter;
import human.nature.customerorderapp.AppSharedPreference;
import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.EventBus.Events;
import human.nature.customerorderapp.EventBus.GlobalBus;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.ListData.OrderData;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StaticDatas;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.REFRESH_ORDER_LIST;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static OrderListFragment orderListFragment;
    @BindView(R.id.list)
    RecyclerView list;
    OrderListAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    Unbinder unbinder;

    StaticDatas staticDatas = StaticDatas.getInstance();
    @BindView(R.id.prev)
    ImageView prev;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.next)
    ImageView next;

    AppSharedPreference spf;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    public static OrderListFragment getInstance() {
        if (orderListFragment == null) {
            orderListFragment = new OrderListFragment();
        }
        return orderListFragment;
    }

    public OrderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        AutoLayout.setView(view);
        unbinder = ButterKnife.bind(this, view);
        spf = new AppSharedPreference(getContext());
        refreshLayout.setOnRefreshListener(this);
        adapter = new OrderListAdapter(getContext(), staticDatas.orderData, this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(adapter);
        list.setLayoutManager(linearLayoutManager);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String currentDateandTime = sdf.format(new Date());
        date.setText(currentDateandTime);

        GetOrderList getOrderList = new GetOrderList(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getOrderList.execute(spf.getUserSeq(), staticDatas.s_no, date.getText().toString());

        return view;
    }


    @OnClick(R.id.prev)
    public void onPrevClicked() {
        int month = Integer.parseInt(date.getText().toString().split("-")[1]);
        int year = Integer.parseInt(date.getText().toString().split("-")[0]);
        if (--month < 1) {
            --year;
            month = 12;
        }
        date.setText(String.valueOf(year) + "-" + String.valueOf(month));
        GetOrderList getOrderList = new GetOrderList(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getOrderList.execute(spf.getUserSeq(), staticDatas.s_no, date.getText().toString());
    }

    @OnClick(R.id.next)
    public void onNextClicked() {
        int month = Integer.parseInt(date.getText().toString().split("-")[1]);
        int year = Integer.parseInt(date.getText().toString().split("-")[0]);
        if (++month > 12) {
            ++year;
            month = 1;
        }
        date.setText(String.valueOf(year) + "-" + String.valueOf(month));
        GetOrderList getOrderList = new GetOrderList(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getOrderList.execute(spf.getUserSeq(), staticDatas.s_no, date.getText().toString());
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        GetOrderList getOrderList = new GetOrderList(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getOrderList.execute(spf.getUserSeq(), staticDatas.s_no, date.getText().toString());
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void getMsg(Events.Msg msg){
        if(REFRESH_ORDER_LIST.equals(msg.getMsg())){
            onRefresh();
        }
    }


    class GetOrderList extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public GetOrderList(Context context, AsyncDone asyncDone) {
            this.context = context;
            this.asyncDone = asyncDone;
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
            Request.Builder builder = new Request.Builder();
            builder.url(staticDatas.baseUrl + "order/u_order_list?" +
                    "u_no=" + strings[0] +
                    "&s_no=" + strings[1] +
                    "&s_dt=" + strings[2]);
            try {
                Request request = builder.build();
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
                Log.d("getOrderList", s);
                staticDatas.orderData.clear();
                JSONArray resultArray = new JSONArray(s);
                JSONArray innerArray = resultArray.getJSONArray(0);
                for (int i = 0; i < innerArray.length(); i++) {
                    JSONObject data = innerArray.getJSONObject(i);
                    staticDatas.orderData.addFirst(new OrderData(
                            data.getString("no"),
                            data.getString("total_price"),
                            data.getString("store_no"),
                            data.getString("status"),
                            data.getString("order_time"),
                            data.getString("pay_time"),
                            data.getString("cancel_time"),
                            data.getString("cancel_yn"),
                            data.getString("end_time"),
                            data.getString("product_names"),
                            data.getString("product_prices"),
                            data.getString("options"),
                            data.getString("count"),
                            data.getString("bank_delivery")
                    ));
                }
                asyncDone.asyncDone(SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                asyncDone.asyncDone(FAILED);
            } finally {
//                dialog.dismiss();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        GlobalBus.getBus().register(this);
        Log.d("orderList onResume", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalBus.getBus().unregister(this);
        Log.d("orderList onPause", "pause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancel:
                final int pos = (int)view.getTag();
                if("N".equals(staticDatas.orderData.get(pos).getCancel_yn())) {
                    CancelOrderAsyncTask cancelOrderAsyncTask = new CancelOrderAsyncTask(getContext(), new AsyncDone() {
                        @Override
                        public void asyncDone(String result) {
                            if (SUCCESS.equals(result)) {
                                staticDatas.orderData.get(pos).setCancel_yn("REQ");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "정상적으로 요청되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    cancelOrderAsyncTask.execute(spf.getUserSeq(), staticDatas.orderData.get(pos).getNo());
                } else if("REQ".equals(staticDatas.orderData.get(pos).getCancel_yn())){
                    C_CancelOrderAsyncTask c_cancelOrderAsyncTask = new C_CancelOrderAsyncTask(getContext(), new AsyncDone() {
                        @Override
                        public void asyncDone(String result) {
                            if (SUCCESS.equals(result)) {
                                staticDatas.orderData.get(pos).setCancel_yn("N");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "정상적으로 요청되었습니다.", Toast.LENGTH_SHORT).show();
                            } else if("CANCELED".equals(result)){
                                Toast.makeText(getContext(), "이미 취소된 주문입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    c_cancelOrderAsyncTask.execute(spf.getUserSeq(), staticDatas.orderData.get(pos).getNo());
                }
                break;

            case R.id.status:
                final String no = String.valueOf(view.getTag());

                OrderFinish orderFinish = new OrderFinish(getContext(), new AsyncDone() {
                    @Override
                    public void asyncDone(String result) {
                        if(SUCCESS.equals(result)){
                            int oPos = -1;
                            for(int i = 0 ; i < staticDatas.orderData.size() ; i++){
                                if(no.equals(staticDatas.orderData.get(i).getNo())){
                                    oPos = i;
                                    break;
                                }
                            }
                            staticDatas.orderData.get(oPos).setStatus("3");
                            adapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(getContext(), "통신에 장애가있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                orderFinish.execute(String.valueOf(view.getTag()));
                break;
        }
    }

    private class OrderFinish extends AsyncTask<String, String, String>{
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public OrderFinish(Context context, AsyncDone asyncDone) {
            this.context = context;
            this.asyncDone = asyncDone;
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
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(staticDatas.baseUrl + "order/order_end?" +
                    "order_no=" + strings[0]);
            try {
                Request request = builder.build();
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
            try{
                Log.d("order finish", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject resObj = jsonArray.getJSONObject(0);
                if("OK".equals(resObj.getString("result"))){
                    asyncDone.asyncDone(SUCCESS);
                } else{
                    asyncDone.asyncDone(FAILED);
                }
            }catch (Exception e){
                e.printStackTrace();
                asyncDone.asyncDone(FAILED);
            }finally {
                dialog.dismiss();
            }
        }
    }


    private class C_CancelOrderAsyncTask extends AsyncTask<String, String ,String>{
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public C_CancelOrderAsyncTask(Context context, AsyncDone asyncDone) {
            this.context = context;
            this.asyncDone = asyncDone;
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
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(staticDatas.baseUrl + "order/u_o_cancel_cancel?" +
                    "u_no=" + strings[0] +
                    "&o_no=" + strings[1]);
            try {
                Request request = builder.build();
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
            try{
                Log.d("c_cancel", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject resObj = jsonArray.getJSONObject(0);
                if(SUCCESS.equals(resObj.getString("result"))){
                    asyncDone.asyncDone(SUCCESS);
                } else if("CANCELED".equals(resObj.getString("result"))){
                    asyncDone.asyncDone("CANCELED");
                } else{
                    asyncDone.asyncDone(FAILED);
                }
            }catch (Exception e){
                asyncDone.asyncDone(FAILED);
            }finally {
                dialog.dismiss();
            }
        }
    }




    private class CancelOrderAsyncTask extends AsyncTask<String, String ,String>{
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public CancelOrderAsyncTask(Context context, AsyncDone asyncDone) {
            this.context = context;
            this.asyncDone = asyncDone;
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
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(staticDatas.baseUrl + "order/u_o_cancel_req?" +
                    "u_no=" + strings[0] +
                    "&o_no=" + strings[1]);
            try {
                Request request = builder.build();
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
            try{
                Log.d("cancel", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject resObj = jsonArray.getJSONObject(0);
                if(SUCCESS.equals(resObj.getString("result"))){
                    asyncDone.asyncDone(SUCCESS);
                } else{
                    asyncDone.asyncDone(FAILED);
                }
            }catch (Exception e){
                asyncDone.asyncDone(FAILED);
            }finally {
                dialog.dismiss();
            }
        }
    }
}
