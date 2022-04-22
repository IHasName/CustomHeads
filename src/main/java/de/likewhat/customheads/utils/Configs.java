package de.likewhat.customheads.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/*
 *  Project: CustomHeads in Configs
 *     by LikeWhat
 */

public class Configs {

    private FileConfiguration config = null;

    private File file = null;

    private final boolean internalFile;

    private final String subfolder;
    private final String filename;

    private final Plugin plugin;

    public Configs(Plugin plugin, String filename, boolean internalFile, String... subfolder) {
        this.plugin = plugin;
        this.filename = filename;
        this.subfolder = subfolder.length > 0 ? "plugins/" + plugin.getName() + "/" + subfolder[0] : "plugins/" + plugin.getName();
        this.internalFile = internalFile;
        if (this.internalFile) {
            Utils.saveInternalFile(filename, this.subfolder);
        }
        get().options().copyDefaults(true);
    }

    public FileConfiguration get() {
        if (config == null) {
            reload();
        }
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save File " + file.getName(), e);
        }
    }

    public void reload() {
        this.file = new File(this.subfolder, this.filename);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (this.internalFile) {
            InputStream dataStream = this.plugin.getResource(this.filename);
            if (dataStream != null) {
                this.config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(dataStream)));
            }
        }
    }

    public File getFile() {
        if (this.file == null) {
            reload();
        }
        return this.file;
    }

}
