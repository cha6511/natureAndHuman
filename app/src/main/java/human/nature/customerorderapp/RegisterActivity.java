package human.nature.customerorderapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import human.nature.customerorderapp.Interface.AsyncDone;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static human.nature.customerorderapp.EventBus.Events.FAILED;
import static human.nature.customerorderapp.EventBus.Events.SUCCESS;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.get_auth)
    Button getAuth;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_confirm)
    EditText passwordConfirm;
    @BindView(R.id.nickname)
    EditText nickname;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.email_auth_number)
    EditText emailAuthNumber;
    @BindView(R.id.address)
    EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        AutoLayout.setResizeView(this);
    }

    @OnClick(R.id.get_auth)
    public void onGetAuthClicked() {
        AppSharedPreference spf = new AppSharedPreference(RegisterActivity.this);
        if (email.getText().toString().length() > 30) {
            Toast.makeText(RegisterActivity.this, "아이디는 30자 이하입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        GetEmailAuth getEmailAuth = new GetEmailAuth(RegisterActivity.this, new AsyncDone() {
            @Override
            public void asyncDone(String result) {
                Log.d("Email auth result", result);
                if ("DUP".equals(result)) {
                    Toast.makeText(RegisterActivity.this, "이미 사용중인 Email 입니다.", Toast.LENGTH_SHORT).show();
                } else if (SUCCESS.equals(result)) {
                    Toast.makeText(RegisterActivity.this, "인증번호를 발송했습니다.\nEmail을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    register.setEnabled(true);
                } else if ("not send".trim().equals(result)) {
                    Toast.makeText(RegisterActivity.this, "메일이 전송되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "통신에 장애가 있습니다.\n확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d("input Email", email.getText().toString());
        getEmailAuth.execute(email.getText().toString());
    }

    @OnClick(R.id.register)
    public void onRegisterClicked() {
        AppSharedPreference spf = new AppSharedPreference(RegisterActivity.this);
        if (TextUtils.isEmpty(emailAuthNumber.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (emailAuthNumber.getText().toString().length() < 5) {
            Toast.makeText(RegisterActivity.this, "인증번호 5자리를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().length() > 30) {
            Toast.makeText(RegisterActivity.this, "아이디는 30자 이하입니다.", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().length() > 20) {
            Toast.makeText(RegisterActivity.this, "비밀번호는 20자 이하입니다.", Toast.LENGTH_SHORT).show();
        } else if (nickname.getText().toString().length() > 20) {
            Toast.makeText(RegisterActivity.this, "닉네임(상호명)은 20자 이하입니다.", Toast.LENGTH_SHORT).show();
        } else if (address.getText().toString().length() > 45){
            Toast.makeText(RegisterActivity.this, "배송지 주소는 45자 이하입니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (spf.getEmailAuthNumber().equals(emailAuthNumber.getText().toString())) {
                Register register = new Register(this, new AsyncDone() {
                    @Override
                    public void asyncDone(String result) {
                        if (SUCCESS.equals(result)) {
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if ("DUP".equals(result)) {
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 ID 입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                register.execute(email.getText().toString(),
                        password.getText().toString(),
                        TextUtils.isEmpty(spf.getToken()) ? FirebaseInstanceId.getInstance().getToken() : spf.getToken(),
                        nickname.getText().toString(),
                        address.getText().toString(),
                        " ");
            } else {
                Toast.makeText(RegisterActivity.this, "인증번호가 틀렸습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetEmailAuth extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        AppSharedPreference spf;

        public GetEmailAuth(Context context, AsyncDone asyncDone) {
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
            formBodyBuilder.add("email", strings[0]);
            FormBody formBody = formBodyBuilder.build();

            try {
                Request request = new Request.Builder().url(StaticDatas.baseUrl + "user/email_auth_confirm").post(formBody).build();
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
            if (TextUtils.isEmpty(s)) {
                asyncDone.asyncDone(FAILED);
                dialog.dismiss();
            } else {
                Log.d("getEmailAuto", s);
                try {
                    JSONObject resultObj = new JSONObject(s);
                    if ("DUP".equals(resultObj.getString("result"))) {
                        asyncDone.asyncDone("DUP");
                    } else if ("SEND".equals(resultObj.getString("result"))) {
                        asyncDone.asyncDone(SUCCESS);
                        spf.setEmailAuthNumber(resultObj.getString("confirm_no"));
                    } else if ("not send".trim().equals(s)) {
                        asyncDone.asyncDone("not send");
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
    }


    private class Register extends AsyncTask<String, String, String> {
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        AppSharedPreference spf;

        public Register(Context context, AsyncDone asyncDone) {
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
            formBodyBuilder.add("email", strings[0]);
            formBodyBuilder.add("pwd", strings[1]);
            formBodyBuilder.add("token", strings[2]);
            formBodyBuilder.add("alias", strings[3]);
            formBodyBuilder.add("addr", strings[4]);
            formBodyBuilder.add("addr_detail", strings[5]);
            FormBody formBody = formBodyBuilder.build();

            try {
                Request request = new Request.Builder().url(StaticDatas.baseUrl + "user/u_add").post(formBody).build();
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
                Log.d("register result", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject resultObj = jsonArray.getJSONObject(0);

                if ("DUP".equals(resultObj.getString("result"))) {
                    asyncDone.asyncDone("DUP");
                } else if (SUCCESS.equals(resultObj.getString("result"))) {
                    asyncDone.asyncDone(SUCCESS);
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
