package human.nature.customerorderapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Adapters.StoreListAdapter;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.ListData.StoreListData;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

public class StoreListActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.store_list)
    RecyclerView storeList;
    LinearLayoutManager linearLayoutManager;
    StoreListAdapter storeListAdapter;
    ArrayList<StoreListData> datas = new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;

    AppSharedPreference spf;
    StaticDatas staticDatas = StaticDatas.getInstance();

    long backKeyPressedTime = 0;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
//                showGuide(); return;
            Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
//                toast.cancel();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        ButterKnife.bind(this);
        AutoLayout.setResizeView(this);

        spf = new AppSharedPreference(this);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        GetStoreListAsyncTask getStoreListAsyncTask = new GetStoreListAsyncTask(this, new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    linearLayoutManager = new LinearLayoutManager(StoreListActivity.this, LinearLayoutManager.VERTICAL, false);
                    storeListAdapter = new StoreListAdapter(datas, StoreListActivity.this);
                    storeList.setAdapter(storeListAdapter);
                    storeList.setLayoutManager(linearLayoutManager);
                    storeListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(StoreListActivity.this, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getStoreListAsyncTask.execute();

    }

    @Override
    public void onClick(View view) {
        String u_no = spf.getUserSeq();
        String s_no = ((StoreListData) view.getTag()).getStore_no();
        String s_name = ((StoreListData)view.getTag()).getStore_name();

        if("이동".equals( ((Button)view).getText().toString()) ){
            Intent intent = new Intent(StoreListActivity.this, MainActivity.class);
            staticDatas.s_no = s_no;
            staticDatas.s_name = s_name;
            startActivity(intent);
        } else {
            RequestAccept requestAccept = new RequestAccept(StoreListActivity.this, new AsyncDone() {
                @Override
                public void asyncDone(String result) {
                    if ("DUP".equals(result)) {
                        Toast.makeText(StoreListActivity.this, "요청 처리중입니다.", Toast.LENGTH_SHORT).show();
                    } else if (SUCCESS.equals(result)) {
                        Toast.makeText(StoreListActivity.this, "승인요청 완료", Toast.LENGTH_SHORT).show();
                        onRefresh();
                    } else {
                        Toast.makeText(StoreListActivity.this, "승인요청 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            requestAccept.execute(u_no, s_no);
        }
    }

    @Override
    public void onRefresh() {
        GetStoreListAsyncTask getStoreListAsyncTask = new GetStoreListAsyncTask(this, new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if (SUCCESS.equals(result)) {
                    swipeRefreshLayout.setRefreshing(false);
                    linearLayoutManager = new LinearLayoutManager(StoreListActivity.this, LinearLayoutManager.VERTICAL, false);
                    storeListAdapter = new StoreListAdapter(datas, StoreListActivity.this);
                    storeList.setAdapter(storeListAdapter);
                    storeList.setLayoutManager(linearLayoutManager);
                    storeListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(StoreListActivity.this, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getStoreListAsyncTask.execute();
    }

    private class RequestAccept extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public RequestAccept(Context context, AsyncDone asyncDone) {
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
            builder.url(StaticDatas.baseUrl + "store/store_acpt_req?" +
                    "u_no=" + strings[0] +
                    "&s_no=" + strings[1]);

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
            Log.d("store request acpt", s);
            datas.clear();
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject obj = jsonArray.getJSONObject(0);
                if ("DUP".equals(obj.getString("result"))) {
                    asyncDone.asyncDone("DUP");
                } else if (SUCCESS.equals(obj.getString("result"))) {
                    asyncDone.asyncDone(SUCCESS);
                } else {
                    asyncDone.asyncDone(FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                asyncDone.asyncDone(FAILED);
            } finally {
                dialog.dismiss();
            }
        }

    }


    private class GetStoreListAsyncTask extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        AppSharedPreference spf;

        public GetStoreListAsyncTask(Context context, AsyncDone asyncDone) {
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
            Request.Builder builder = new Request.Builder();
            builder.url(StaticDatas.baseUrl + "store/app_store_list?" +
                    "u_no=" + spf.getUserSeq());

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
            Log.d("store list result", s);
            datas.clear();
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    datas.add(new StoreListData(
                            obj.getString("store_name"),
                            obj.getString("store_no"),
                            obj.getString("acpt_yn")
                    ));
                }
                asyncDone.asyncDone(SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                asyncDone.asyncDone(FAILED);
            } finally {
                dialog.dismiss();
            }
        }
    }
}
