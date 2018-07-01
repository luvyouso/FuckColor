package coloring.org.jp.ktcc.full.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import coloring.org.jp.ktcc.full.util.JSONSharedPreferences;

/**
 * Created by anh.trinh on 3/15/2018.
 */

public class SealObject {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private static String KEY_ID ="id";
    private static String KEY_NAME ="name";

    public SealObject(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public SealObject() {
       this.id = "";
       this.name = "";
    }
    public static SealObject getSealObjFromJson(JSONObject jsonObject) {
        SealObject sealObject = new SealObject();

        try {
            if (jsonObject.has(KEY_ID)) {
                sealObject.setId(jsonObject.getString(KEY_ID));
            }
            if (jsonObject.has(KEY_NAME)) {
                sealObject.setName(jsonObject.getString(KEY_NAME));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sealObject;
    }
    public static JSONObject getJsonFromSealObj(SealObject sealObject){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(KEY_ID,sealObject.getId());
            jsonObject.putOpt(KEY_NAME,sealObject.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static List<SealObject> getListSeals(Context context){
        List<SealObject> sealObjects = new ArrayList<>();
        try {
           JSONArray jsonArray =  JSONSharedPreferences.loadJSONArray(context, JSONSharedPreferences.PREF_NAME,JSONSharedPreferences.KEY_SEALS);
           for (int i = 0; i<jsonArray.length();i++){
               sealObjects.add(getSealObjFromJson(jsonArray.getJSONObject(i)));
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sealObjects;
    }
    public static void saveListSeals(Context context,List<SealObject> sealObjects){
        JSONArray jsonArray = new JSONArray();
        for (SealObject sealObject:sealObjects
             ) {
            jsonArray.put(getJsonFromSealObj(sealObject));
        }
        JSONSharedPreferences.saveJSONArray(context, JSONSharedPreferences.PREF_NAME,JSONSharedPreferences.KEY_SEALS,jsonArray);
    }
    public static String getName(List<SealObject> sealObjects, String id){
        String name ="";
        if(sealObjects!=null) {
            for (SealObject object : sealObjects) {
                if (object.getId().equals(id)) {
                    name = object.getName();
                    break;
                }

            }
        }
        return  name;
    }
    public static List<SealObject> addSeal(Context context, List<SealObject> sealObjects, String id, String name){

        if(sealObjects == null)
            sealObjects = new ArrayList<>();
        boolean isExist = false;
            for (SealObject object : sealObjects) {
                if (object.getId().equals(id)) {
                    object.setName(name);
                    isExist = true;
                    break;
                }

            }
            if(!isExist){
                SealObject sealObject = new SealObject(id,
                        name);
                sealObjects.add(sealObject);
            }
            saveListSeals(context,sealObjects);

        return sealObjects;
    }

}
