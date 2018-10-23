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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Adapters.NotiThumbAdapter;
import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.Interface.GetNoti;
import human.nature.customerorderapp.ListData.NotiData;
import human.nature.customerorderapp.ListData.NotiThumbData;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StaticDatas;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static NotificationFragment notificationFragment;
    @BindView(R.id.noti_list)
    RecyclerView notiList;
    NotiThumbAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Unbinder unbinder;
    StaticDatas staticDatas = StaticDatas.getInstance();
    int page = 0;
    SwipeRefreshLayout swipeRefreshLayout;

    public static NotificationFragment getInstance() {
        if (notificationFragment == null) {
            notificationFragment = new NotificationFragment();
        }
        return notificationFragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    ArrayList<NotiThumbData> datas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        AutoLayout.setView(v);
        unbinder = ButterKnife.bind(this, v);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new NotiThumbAdapter(datas, this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notiList.setLayoutManager(linearLayoutManager);
        notiList.setAdapter(adapter);

        datas.clear();
        GetNotiThumb getNotiThumb = new GetNotiThumb(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if(SUCCESS.equals(result)){
                    adapter.notifyDataSetChanged();
                } else{
                    Toast.makeText(getContext(), "통신에 장애가있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getNotiThumb.execute(staticDatas.s_no, String.valueOf(0));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notiList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if(!notiList.canScrollVertically(1)){
                        Log.d("scrolling page", page+"");
                        GetNotiThumb paging = new GetNotiThumb(getContext(), new AsyncDone() {
                            @Override
                            public void asyncDone(String result) {
                                if (SUCCESS.equals(result)) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getContext(), "통신에 장애가있습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        paging.execute(staticDatas.s_no, String.valueOf(page+=10));
                    }
                }
            });
        }

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("notification onResume", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("notification onPause", "pause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        String no = String.valueOf(view.getTag());
        GetNoti getNoti = new GetNoti(getContext(), new human.nature.customerorderapp.Interface.GetNoti() {
            @Override
            public void noti(NotiData result) {
                try{
                    NotiDialog notiDialog = new NotiDialog(
                            getContext(),
                            result.getTitle(),
                            result.getAdd_time(),
                            result.getContent()
                    );
                    notiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    notiDialog.show();
                    Window window = notiDialog.getWindow();
                    window.setLayout(MATCH_PARENT, MATCH_PARENT);
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "공지 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getNoti.execute(no);
    }

    @Override
    public void onRefresh() {
        page = 0;
        swipeRefreshLayout.setRefreshing(false);
        datas.clear();
        GetNotiThumb getNotiThumb = new GetNotiThumb(getContext(), new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                if(SUCCESS.equals(result)){
                    adapter.notifyDataSetChanged();
                } else{
                    Toast.makeText(getContext(), "통신에 장애가있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getNotiThumb.execute(staticDatas.s_no, String.valueOf(page));
    }



    private class GetNoti extends AsyncTask<String, String, String> {
        Context context;
        human.nature.customerorderapp.Interface.GetNoti asyncDone;
        AlertDialog dialog;

        public GetNoti(Context context, human.nature.customerorderapp.Interface.GetNoti asyncDone) {
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
            // TODO: attempt authentication against a network service.
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(StaticDatas.baseUrl + "notice/s_n_one?" +
                    "n_no=" + strings[0]);
            try {
                Request request = builder.build();
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
            if("[]".equals(s) && !(datas.size() == 0)){
                page -= 10;
                Log.d("result page", page+"");
            }
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject resObj = jsonArray.getJSONObject(0);
                NotiData data = new NotiData(resObj.getString("title"), resObj.getString("add_time"), resObj.getString("content"));
                asyncDone.noti(data);
            } catch (Exception e) {
                e.printStackTrace();
                asyncDone.noti(null);
            } finally {
//                dialog.dismiss();
            }
        }
    }







    private class GetNotiThumb extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public GetNotiThumb(Context context, AsyncDone asyncDone) {
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
            // TODO: attempt authentication against a network service.
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(StaticDatas.baseUrl + "notice/s_l_notice?" +
                    "s_no=" + strings[0] +
                    "&page=" + strings[1]);
            try {
                Request request = builder.build();
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
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    datas.add(new NotiThumbData(
                            obj.getString("no"),
                            obj.getString("title"),
                            obj.getString("add_time")
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

}
