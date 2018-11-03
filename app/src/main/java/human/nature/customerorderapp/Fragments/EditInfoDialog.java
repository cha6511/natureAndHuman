package human.nature.customerorderapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.AppSharedPreference;
import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.Interface.AsyncDone;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StaticDatas;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

public class EditInfoDialog extends Dialog {
    EditText alias, addr, contact;
    Button confirm, close;
    Context context;

    AppSharedPreference spf;

    public EditInfoDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        this.spf = new AppSharedPreference(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info_dialog);

        alias = findViewById(R.id.nickname);
        addr = findViewById(R.id.address);
        contact = findViewById(R.id.contact);

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditInfo editInfo = new EditInfo(context, new AsyncDone() {
                    @Override
                    public void asyncDone(String result) {
                        if(SUCCESS.equals(result)){
                            Toast.makeText(context, "정상적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else{
                            Toast.makeText(context, "통신에 장애가있습니다.\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                editInfo.execute(
                        spf.getUserSeq(),
                        alias.getText().toString(),
                        addr.getText().toString(),
                        contact.getText().toString()
                );
            }
        });
        close = findViewById(R.id.cancel);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditInfoDialog.this.dismiss();
            }
        });


    }



    public class EditInfo extends AsyncTask<String ,String ,String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public EditInfo(Context context, AsyncDone asyncDone) {
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
            // TODO: attempt authentication against a network service.
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(StaticDatas.baseUrl + "user/u_update?" +
                    "u_no=" + strings[0] +
                    "&alias=" + strings[1] +
                    "&addr=" + strings[2] +
                    "&addr_detail=" + " " +
                    "&tel=" + strings[3]);
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
        protected void onPostExecute(final String s) {
            try {
                Log.d("edit info result", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String result = jsonObject.getString("result");
                if (SUCCESS.equals(result)) {
                    asyncDone.asyncDone(SUCCESS);
                } else if ("FAIL".equals(result)) {
                    asyncDone.asyncDone(FAILED);
                } else {
                    asyncDone.asyncDone(FAILED);
                }
            } catch (Exception e) {
                asyncDone.asyncDone(FAILED);
            } finally {
                dialog.dismiss();
            }
        }

    }
}
