package io.github.unixsupremacist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Steamgrid {
    static String apiKey;

    public static String getApiKey(){
        if (apiKey == null){
            try {
                FileInputStream stream = new FileInputStream(CMBMain.StorageDirectory+"sgkey");
                apiKey = IOUtils.toString(stream).trim();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return apiKey;
    }

    public static int steamgridSearch(String name){
//        try {
//            return 0;
//
//            URL url = new URL("https://www.steamgriddb.com/api/v2/search/autocomplete/"+name);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            con.setRequestProperty("Authorization", "Bearer "+getApiKey());
//            JsonObject json = new JsonParser().parse(IOUtils.toString(con.getInputStream())).getAsJsonObject();
//            con.disconnect();
//            return json.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
//        } catch (IOException e) {
//            System.out.println(e);
//            return 0;
//        }

        return 0;
    }

    public static String steamgridGet(int id, String type){
        try {
            URL url = new URL("https://www.steamgriddb.com/api/v2/"+type+"/game/"+id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+getApiKey());
            JsonObject json = new JsonParser().parse(IOUtils.toString(con.getInputStream())).getAsJsonObject();
            con.disconnect();
            return json.get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
