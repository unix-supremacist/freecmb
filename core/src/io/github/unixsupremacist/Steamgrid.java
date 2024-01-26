package io.github.unixsupremacist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Steamgrid {
    static String apiKey;

    public static String getApiKey(){
        if (apiKey == null){
            String key = CMBMain.config.steamgridKey;
            if (!key.isEmpty())
                apiKey = key;
        }
        return apiKey;
    }

    public static int steamgridSearch(String name){
        getApiKey();
        if (apiKey != null){
            if (name == "firefox") return 5248361;
            try {
                URL url = new URL("https://www.steamgriddb.com/api/v2/search/autocomplete/"+URLEncoder.encode(name));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer "+apiKey);
                JsonObject json = new JsonParser().parse(IOUtils.toString(con.getInputStream())).getAsJsonObject();
                con.disconnect();
                var data = json.get("data").getAsJsonArray();
                if (!data.isEmpty())
                    return data.get(0).getAsJsonObject().get("id").getAsInt();
                else
                    return 0;
            } catch (IOException e) {
                System.out.println(e);
                return 0;
            }
        }
        return 0;
    }

    public static String steamgridGet(int id, String type){
        getApiKey();

        if (apiKey != null && !CMBMain.config.dbcache.contains(id)){
            try {
                URL url = new URL("https://www.steamgriddb.com/api/v2/"+type+"/game/"+id);
                System.out.println(url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer "+getApiKey());
                JsonObject json = new JsonParser().parse(IOUtils.toString(con.getInputStream())).getAsJsonObject();
                con.disconnect();
                if (!json.get("data").getAsJsonArray().isEmpty()){
                    if (id == 5279790 && type == "logos")
                        return json.get("data").getAsJsonArray().get(3).getAsJsonObject().get("url").getAsString();
                    else
                        return json.get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
                }
                CMBMain.config.dbcache.add(id);
                CMBMain.writeConfig(CMBMain.config);
            } catch (IndexOutOfBoundsException e){
                System.out.println(id+type);
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }
}
