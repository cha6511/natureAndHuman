package human.nature.customerorderapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    @BindView(R.id.save_id)
    CheckBox saveId;
    AppSharedPreference spf;

    @BindView(R.id.find_pw)
    TextView findPw;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    Button mEmailSignInButton;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.addTextChangedListener(inputIdWatcher);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEmailView.getText().toString()) && TextUtils.isEmpty(mPasswordView.getText().toString())) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } else {
                    attemptLogin();
                }
            }
        });

        spf = new AppSharedPreference(this);

        saveId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spf.setSaveId(b);
            }
        });

        if (spf.isIdSaved()) {
            mEmailView.setText(spf.getLoginId());
            saveId.setChecked(true);
            mPasswordView.requestFocus();
        }


        AutoLayout.setResizeView(this);
    }


    /**
     * Callback received when a permissions request has been completed.
     */

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }
//
//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("비밀번호가 너무 짧습니다.");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email은 필수 입력 항목입니다.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("Email을 확인해주세요.");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (mEmailView.getText().toString().length() > 30) {
                Toast.makeText(LoginActivity.this, "아이디는 30자 이하입니다.", Toast.LENGTH_SHORT).show();
            } else if (mPasswordView.getText().toString().length() > 20) {
                Toast.makeText(LoginActivity.this, "비밀번호는 20자 이하입니다.", Toast.LENGTH_SHORT).show();
            }
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            final AppSharedPreference spf = new AppSharedPreference(LoginActivity.this);
            mAuthTask = new UserLoginTask(LoginActivity.this, new AsyncDone() {
                @Override
                public void asyncDone(String result) {
                    if ("FAIL_PWD".equals(result)) {
                        Toast.makeText(LoginActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else if ("NO_ACCOUNT".equals(result)) {
                        Toast.makeText(LoginActivity.this, "Email 아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else if (SUCCESS.equals(result)) {
                        if (spf.isIdSaved()) {
                            spf.setLoginId(mEmailView.getText().toString());
                        }
                        spf.setIsLogin(true);
                        Intent intent = new Intent(LoginActivity.this, StoreListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "통신에 장애가 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mAuthTask.execute(email, password, TextUtils.isEmpty(spf.getToken()) ? FirebaseInstanceId.getInstance().getToken() : spf.getToken());
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    @OnClick(R.id.find_pw)
    public void onFindPwClicked() {

    }

    public class FindPw extends AsyncTask<String ,String ,String>{
        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;

        public FindPw(Context context, AsyncDone asyncDone) {
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
            builder.url(StaticDatas.baseUrl + "user/find_pwd?" +
                    "email=" + strings[0]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                String myResponse = response.body().string();
                return myResponse;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            try {
                Log.d("find pw result", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String result = jsonObject.getString("result");
                if ("SEND".equals(result)) {
                    asyncDone.asyncDone(SUCCESS);
                } else if ("not send".equals(result)) {
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

    /**
     * Shows the progress UI and hides the login form.
     */

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, String, String> {

        Context context;
        AsyncDone asyncDone;
        AlertDialog dialog;
        AppSharedPreference spf;

        public UserLoginTask(Context context, AsyncDone asyncDone) {
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
            // TODO: attempt authentication against a network service.
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            formBodyBuilder.add("id", strings[0]);
            formBodyBuilder.add("pwd", strings[1]);
            formBodyBuilder.add("token", strings[2]);
            FormBody formBody = formBodyBuilder.build();

            try {
                Request request = new Request.Builder().url(StaticDatas.baseUrl + "user/u_login").post(formBody).build();
                Response response = client.newCall(request).execute();
                String myResponse = response.body().string();
                return myResponse;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            try {
                Log.d("login result", s);
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String result = jsonObject.getString("result");
                if (SUCCESS.equals(result)) {
                    spf.setUserSeq(jsonObject.getString("user_no"));
                    asyncDone.asyncDone(SUCCESS);
                } else if ("FAIL_PWD".equals(result)) {
                    asyncDone.asyncDone("FAIL_PWD");
                } else if ("NO_ACCOUNT".equals(result)) {
                    asyncDone.asyncDone("NO_ACCOUNT");
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


    TextWatcher inputIdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 0 && charSequence.toString().contains("@")) {
                mEmailSignInButton.setText("로그인");
            } else {
                mEmailSignInButton.setText("회원가입");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}

