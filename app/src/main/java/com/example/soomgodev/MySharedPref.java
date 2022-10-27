package com.example.soomgodev;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPref {

    private static MySharedPref instance;
    private static Context prefContext;

    private MySharedPref(){}    // 생성자


    private static class LazyHolder{
        public static final MySharedPref MY_SHARED_INSTANCE = new MySharedPref();
    }

    public static MySharedPref getInstance(Context context){
        prefContext = context;
        return LazyHolder.MY_SHARED_INSTANCE;
    }

    // String 값 불러오기
    public String getStringPref(String key){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        return (pref.getString(key, "no key"));
    }

    // String 값 저장하기, 수정하기(같은 키값에 대해 덮어 저장하면 된다.)
    public void saveStringPref(String key, String value){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //정수값 저장 하기, 수정하기(같은 키값에 대해 덮어 저장하면 된다.)
    public void saveIntPref(String key, int value){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    //정수값 불러오기
    public int getIntPref(String key){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        return (pref.getInt(key, 0));
    }

    //boolean값 저장 하기, 수정하기(같은 키값에 대해 덮어 저장하면 된다.)
    public void saveBooleanPref(String key, boolean value){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //boolean값 불러오기
    public boolean getBooleanPref(String key){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        return (pref.getBoolean(key, false));
    }

    // 값(Key Data) 삭제하기
    public void removePref(String key){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Log.d("test", "삭제 시 key:" + key);
        editor.remove(key);
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    public void removeAllPref(){
        SharedPreferences pref = prefContext.getSharedPreferences("pref", prefContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
