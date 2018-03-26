package de.mrstein.customheads.utils;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

public class JsonFile {

    private JsonElement json;

    public JsonFile(File file) {
        JsonParser parser = new JsonParser();
        try {
            InputStream in = new URL("file:///" + file.getAbsolutePath()).openConnection().getInputStream();
            byte[] buffer = new byte[1];
            StringBuilder str = new StringBuilder();
            while(in.read(buffer) > 0) {
                str.append(new String(buffer, Charsets.UTF_8));
            }
            json = parser.parse(str.toString());
            in.close();
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to get Json from " + file.getName(), e);
        }
    }

    public JsonElement getJson() { return json; }

    public String getJsonAsString() { return json.toString(); }

}
