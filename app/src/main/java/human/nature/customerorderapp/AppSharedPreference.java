package human.nature.customerorderapp;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreference {
    SharedPreferences spf;
    SharedPreferences.Editor editor;

    public static final String TOKEN = "TOKEN";
    public static final String EMAIL_AUTH_NUMBER = "EMAIL_AUTH_NUMBER";
    public static final String USER_SEQ = "USER_SEQ";
    public static final String USER_ALIAS = "USER_ALIAS";
    public static final String USER_ADDR = "USER_ADDR";
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final String SAVE_ID = "SAVE_ID";
    public static final String IS_LOGIN = "IS_LOGIN";


    public AppSharedPreference(Context context){
        spf = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = spf.edit();
    }

    public boolean isLogin(){
        return spf.getBoolean(IS_LOGIN, false);
    }
    public void setIsLogin(boolean isLogin){
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    public String getUserAlias(){
        return spf.getString(USER_ALIAS, null);
    }
    public void setUserAlias(String alias){
        editor.putString(USER_ALIAS, alias);
        editor.commit();
    }

    public String getUserAddr(){
        return spf.getString(USER_ADDR, null);
    }
    public void setUserAddr(String addr){
        editor.putString(USER_ADDR, addr);
        editor.commit();
    }

    public String getToken(){
        return spf.getString(TOKEN, null);
    }

    public void setToken(String token){
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getEmailAuthNumber(){
        return spf.getString(EMAIL_AUTH_NUMBER, null);
    }

    public void setEmailAuthNumber(String emailAuthNumber){
        editor.putString(EMAIL_AUTH_NUMBER, emailAuthNumber);
        editor.commit();
    }

    public String getUserSeq(){
        return spf.getString(USER_SEQ, null);
    }
    public void setUserSeq(String userSeq){
        editor.putString(USER_SEQ, userSeq);
        editor.commit();
    }

    public String getLoginId(){
        return spf.getString(LOGIN_ID, null);
    }
    public void setLoginId(String id){
        editor.putString(LOGIN_ID, id);
        editor.commit();
    }

    public boolean isIdSaved(){
        return spf.getBoolean(SAVE_ID, false);
    }
    public void setSaveId(boolean b){
        editor.putBoolean(SAVE_ID, b);
        editor.commit();
    }


}
