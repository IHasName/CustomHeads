package de.likewhat.customheads.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/*
 *  Project: CustomHeads in JsonFile
 *     by LikeWhat
 */

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
        this.file = file;
        reload();
    }

    public JsonFile(String filename, String subFolder) {
        file = Utils.saveInternalFile(filename, subFolder);
        reload();
    }

    public void saveJson() {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            String[] jsonSplitted = Utils.GSON_PRETTY.toJson(json).split("\n");
            for (String line : jsonSplitted) {
                writer.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            json = PARSER.parse(reader);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to read Json from " + file.getName(), e);
        }
    }

    public String getJsonAsString() {
        return json.toString();
    }

    public void setJson(JsonElement json) {
        this.json = json;
    }

}
