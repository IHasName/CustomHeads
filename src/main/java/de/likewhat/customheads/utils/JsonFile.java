package de.likewhat.customheads.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.likewhat.customheads.CustomHeads;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Setter
    private JsonElement json;

    private final File file;

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
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(Utils.GSON_PRETTY.toJson(json));
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to write Json to File", e);
        }
    }

    public void reload() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            json = PARSER.parse(reader);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to read Json from " + file.getName(), e);
        }
    }

    public String getJsonAsString() {
        return json.toString();
    }

}
