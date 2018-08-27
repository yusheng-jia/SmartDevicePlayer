package com.mantic.control.utils;

import com.google.gson.Gson;
import com.mantic.control.customclass.Clock;
import com.mantic.control.customclass.ClockAdd;
import com.mantic.control.customclass.ClockSearch;

import org.json.JSONObject;

public class ClockUtil {
    public static ClockAdd handleAddandEditResponse(String response){

        return new Gson().fromJson(response,ClockAdd.class);
    }

    public static ClockSearch handleSearchResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            String stringContent=jsonObject.getJSONObject("data").toString();
            return new Gson().fromJson(stringContent,ClockSearch.class);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
