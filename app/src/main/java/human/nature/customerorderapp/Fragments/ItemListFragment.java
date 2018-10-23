package human.nature.customerorderapp.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Adapters.ItemListAdapter;
import human.nature.customerorderapp.AppSharedPreference;
import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.EventBus.Events;
import human.nature.customerorderapp.EventBus.GlobalBus;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.Interface.ItemListJsonParser;
import human.nature.customerorderapp.ListData.CartData;
import human.nature.customerorderapp.ListData.ItemListData;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StaticDatas;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static human.nature.customerorderapp.EventBus.Events.DIALOG_CLOSE;
import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {

    RecyclerView list;
    SwipeRefreshLayout swipeRefreshLayout;
    ItemListAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    StaticDatas staticDatas = StaticDatas.getInstance();
    int page = 0;

    FloatingTextButton fab;

    public static ItemListFragment itemListFragment;

    public static ItemListFragment getInstance() {
        if (itemListFragment == null) {
            itemListFragment = new ItemListFragment();
        }
        return itemListFragment;
    }

    public ItemListFragment() {
        // Required empty public constructor
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_item_list, container, false);

        AutoLayout.setView(v);

        list = v.findViewById(R.id.list);

        fab = v.findViewById(R.id.action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartDialog cartDialog = new CartDialog(getContext());
                cartDialog.show();
                Window window = cartDialog.getWindow();
                window.setLayout(MATCH_PARENT, MATCH_PARENT);
            }
        });

        swipeRefreshLayout = v.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new ItemListAdapter(getContext(), staticDatas.itemListData, this, this, this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(adapter);
        list.setLayoutManager(linearLayoutManager);
        Log.d("start paging", page+"");
        staticDatas.itemListData.clear();
        GetItemListAsyncTask getItemListAsyncTask = new GetItemListAsyncTask(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "상품 목록을 불러오는데 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getItemListAsyncTask.execute(String.valueOf(page), staticDatas.s_no);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            list.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                   if(!list.canScrollVertically(1)){
                       Log.d("scrolling page", page+"");
                        GetItemListAsyncTask paging = new GetItemListAsyncTask(getContext(), new AsyncDone() {
                            @Override
                            public void asyncDone(String result) {
                                if (SUCCESS.equals(result)) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getContext(), "상품 목록을 불러오는데 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        paging.execute(String.valueOf(page+=10), staticDatas.s_no);
                    }
                }
            });
        }
        return v;
    }

    @Override
    public void onClick(final View view) {
        final int pos = (int) view.getTag();
        int amt = Integer.parseInt(staticDatas.itemListData.get(pos).getAmt());
        final ItemListData data = staticDatas.itemListData.get(pos);

        switch (view.getId()) {
            case R.id.increase_amt:
                staticDatas.itemListData.get(pos).setAmt(String.valueOf(amt + 1));
                staticDatas.itemListData.get(pos).setNeedRefreshAdapter(false);
                adapter.notifyDataSetChanged();
                break;

            case R.id.decrease_amt:
                if (amt == 0) {
                    return;
                } else {
                    staticDatas.itemListData.get(pos).setAmt(String.valueOf(amt - 1));
                    staticDatas.itemListData.get(pos).setNeedRefreshAdapter(false);
                    adapter.notifyDataSetChanged();
                }
                break;


            case R.id.order:
                if("0".equals(data.getAmt())){
                    Toast.makeText(getContext(), "최소 1개부터 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                MakeOrderAsyncTask makeOrderAsyncTask = new MakeOrderAsyncTask(getContext(), new AsyncDone() {
                    @Override
                    public void asyncDone(String result) {
                        if(SUCCESS.equals(result)){
                            Toast.makeText(getContext(), "주문되었습니다.", Toast.LENGTH_SHORT).show();
                            data.setAmt("0");
                            onRefresh();
                        } else if("CNT NOT ENOUGH".equals(result)){
                            Toast.makeText(getContext(), "재고가 부족합니다.\n재고 수량을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(), "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                makeOrderAsyncTask.execute(
                        String.valueOf(Integer.parseInt(data.getAmt()) * Integer.parseInt(data.getPrice())),
                        String.valueOf(Integer.parseInt(data.getAmt()) * Integer.parseInt(data.getPrice())) + "/",
                        data.getName() + "/",
                        data.getAmt() + "/",
                        data.getOption() + "/",
                        data.getNo() + "/",
                        data.getOption_no() + "/"
                );
                break;

            case R.id.add_cart:
                if("0".equals(data.getAmt())){
                    Toast.makeText(getContext(), "최소 1개부터 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                staticDatas.cartData.add(new CartData(
                        data.getImg_url(),
                        data.getName(),
                        data.getOption(),
                        data.getAmt(),
                        data.getPrice(),
                        data.getNo(),
                        data.getOption_no()
                ));
                Toast.makeText(getContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                fab.setTitle(String.valueOf(staticDatas.cartData.size()));
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        int pos = (int)textView.getTag();
        if(i == EditorInfo.IME_ACTION_DONE){
            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString())));
            staticDatas.itemListData.get(pos).setAmt(textView.getText().toString());
            adapter.notifyDataSetChanged();
        } else if(i == EditorInfo.IME_ACTION_NEXT){
            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString())));
            staticDatas.itemListData.get(pos).setAmt(textView.getText().toString());
            adapter.notifyDataSetChanged();
        }
        return false;
    }


    class MakeOrderAsyncTask extends AsyncTask<String, String, String>{
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
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
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
            try{
                Request request = new Request.Builder().url(staticDatas.baseUrl + "order/c_order").post(formBody).build();
                Response response = client.newCall(request).execute();
                String myResponse = response.body().string();
                return myResponse;
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                Log.d("makeOrder result", s);
                JSONArray resultArray = new JSONArray(s);
                JSONArray innerArray = resultArray.getJSONArray(0);
                JSONObject result = innerArray.getJSONObject(0);
                if(SUCCESS.equals(result.getString("result"))){
                    asyncDone.asyncDone(SUCCESS);
                } else if("CNT NOT ENOUGH".equals(result.getString("result"))){
                   asyncDone.asyncDone("CNT NOT ENOUGH");
                } else{
                    asyncDone.asyncDone(FAILED);
                }
            } catch (Exception e){
                asyncDone.asyncDone(FAILED);
            } finally {
                dialog.dismiss();
            }
        }
    }


    @Override
    public void onRefresh() {
        page = 0;
        staticDatas.itemListData.clear();
        GetItemListAsyncTask refreshList = new GetItemListAsyncTask(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                swipeRefreshLayout.setRefreshing(false);
                if (SUCCESS.equals(result)) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "상품 목록을 불러오는데 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        refreshList.execute(String.valueOf(page), staticDatas.s_no);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = (int)adapterView.getTag();
        ItemListData data = staticDatas.itemListData.get(pos);
        int optionPrice = Integer.parseInt(data.getOptions().get(i).getOption_price());
        data.setPrice(String.valueOf(Integer.parseInt(data.getOriginPrice()) + optionPrice));
        data.setOption(String.valueOf(adapterView.getItemAtPosition(i)));
        data.setOption_no(data.getOptions().get(i).getOption_no());
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        int pos = (int)adapterView.getTag();
        ItemListData data = staticDatas.itemListData.get(pos);
        int optionPrice = Integer.parseInt(data.getOptions().get(0).getOption_price());
        data.setPrice(String.valueOf(Integer.parseInt(data.getOriginPrice()) + optionPrice));
        data.setOption(String.valueOf(adapterView.getItemAtPosition(0)));
        data.setOption_no(data.getOptions().get(0).getOption_no());
    }


    private class GetItemListAsyncTask extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        public GetItemListAsyncTask(Context context, AsyncDone asyncDone) {
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
            Log.d("doInBackground page", strings[0]);
            builder.url(StaticDatas.baseUrl + "product/l_product?" +
                    "page=" + strings[0] +
                    "&s_no=" + strings[1]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                String myResponse = response.body().string();
                return myResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("async result", s);
            dialog.dismiss();
            if("[]".equals(s) && !(staticDatas.itemListData.size() == 0)){
                page -= 10;
                Log.d("result page", page+"");
            }
            if (TextUtils.isEmpty(s)) {
                asyncDone.asyncDone(FAILED);
//                dialog.dismiss();
            } else {
                try {
                    ItemListJsonParser itemListJsonParser = new ItemListJsonParser(new JSONArray(s));
                    itemListJsonParser.inputToItemListData();
                    asyncDone.asyncDone(SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    asyncDone.asyncDone(FAILED);
                } finally {
//                    dialog.dismiss();
                }
            }
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void getMessage(Events.Msg msg){
        if(DIALOG_CLOSE.equals(msg.getMsg())){
            fab.setTitle(String.valueOf(staticDatas.cartData.size()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ItemList onResume", "resume");
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ItemList onPause", "pause");
        GlobalBus.getBus().unregister(this);
    }
}
