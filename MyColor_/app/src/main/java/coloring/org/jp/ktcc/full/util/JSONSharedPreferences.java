package coloring.org.jp.ktcc.full.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anh.trinh on 3/14/2018.
 */

public class JSONSharedPreferences {
    private static final String PREFIX = "json";
    public static final String PREF_NAME = "seals";
    public static final String KEY_SEALS = "seals";
    public static final String PREF_NAME_FIRST_EFFECT = "effect";
    public static final String KEY_FIRST_EFFECT = "isFirst";
    public static final String PREF_NAME_SEAL = "seal";
    public static final String KEY_SEAL = "seal";

    public static void saveJSONObject(Context c, String prefName, String key, JSONObject object) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, object.toString());
        editor.commit();
    }

    public static void saveJSONArray(Context c, String prefName, String key, JSONArray array) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, array.toString());
        editor.commit();
    }

    public static JSONObject loadJSONObject(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONObject(settings.getString(JSONSharedPreferences.PREFIX+key, "{}"));
    }

    public static JSONArray loadJSONArray(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONArray(settings.getString(JSONSharedPreferences.PREFIX+key, "[]"));
    }

    public static void remove(Context c, String prefName, String key) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX+key)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(JSONSharedPreferences.PREFIX+key);
            editor.commit();
        }
    }
    public static void saveFirstComeEffect(Context c, boolean object) {
        if(c== null){
            return ;
        }
        SharedPreferences sharedPref = c.getSharedPreferences(PREF_NAME_FIRST_EFFECT, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_FIRST_EFFECT, object);
        editor.commit();
    }
    public static boolean getFirstComeEffect(Context c) {
        if(c== null){
            return true;
        }
        SharedPreferences sharedPref = c.getSharedPreferences(PREF_NAME_FIRST_EFFECT, 0);
        return   sharedPref.getBoolean(KEY_FIRST_EFFECT, true);

    }
    public static void saveSeal(Context c, String object) {
        SharedPreferences sharedPref = c.getSharedPreferences(PREF_NAME_SEAL, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_SEAL, object);
        editor.commit();
    }
    public static String getSeal(Context c) {
        if(c== null){
            return "";
        }
        SharedPreferences sharedPref = c.getSharedPreferences(PREF_NAME_SEAL, 0);
        return   sharedPref.getString(KEY_SEAL, "");

    }

}

