package de.mrstein.customheads.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.saveInternalFile;

@Getter
public class JsonFile {

    @Getter(AccessLevel.NONE)
    private static final JsonParser PARSER = new JsonParser();
    @Setter
    private static String defaultSubfolder = "";
    private JsonElement json;
    private File file;

    public JsonFile(String filename) {
        this(filename, defaultSubfolder);
    }

    public JsonFile(File file) {
        this(file.getName(), file.getPath().substring(0, file.getPath().lastIndexOf("\\")));
    }

    public JsonFile(String filename, String subFolder) {
        this.file = saveInternalFile(filename, subFolder);
        reload();
    }

    public void saveJson() {
        try {
            FileWriter writer = new FileWriter(file);
            String[] jsonSplitted = Utils.GSON_PRETTY.toJson(json).split("\n");
            for (String line : jsonSplitted) {
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            InputStream in = new URL("file:///" + file.getAbsolutePath()).openConnection().getInputStream();
            byte[] buffer = new byte[1];
            StringBuilder str = new StringBuilder();
            while (in.read(buffer) > 0) {
                str.append(new String(buffer, StandardCharsets.UTF_8));
            }
            json = PARSER.parse(str.toString());
            in.close();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to get Json from " + file.getName(), e);
        }
    }

    public String getJsonAsString() {
        return json.toString();
    }

    public void setJson(JsonElement json) {
        this.json = json;
    }

}
