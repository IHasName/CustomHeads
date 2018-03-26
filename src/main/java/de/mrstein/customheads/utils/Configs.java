package de.mrstein.customheads.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.saveInternalFile;

public class Configs {

    private FileConfiguration config = null;
    private File file = null;

    private String filename;
    private String subfolder;

    private boolean inPlugin;

    private Plugin plugin;

    public Configs(Plugin plugin, String filename, boolean internalFile, String... subfolder) {
        this.plugin = plugin;
        this.filename = filename;
        this.subfolder = subfolder.length > 0 ? "plugins/" + plugin.getName() + "/" + subfolder[0] : "plugins/" + plugin.getName();
        inPlugin = internalFile;
        get().options().copyDefaults(true);
        if (internalFile) {
            saveInternalFile(plugin, filename, subfolder.length > 0 ? subfolder[0] : "");
        }
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
        } catch(Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save File " + file.getName(), e);
        }
    }

    public void reload() {
        file = new File(subfolder, filename);
        config = YamlConfiguration.loadConfiguration(file);
        if (inPlugin) {
            InputStream dataStream = plugin.getResource(filename);
            if (dataStream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(dataStream)));
            }
        }
    }

    public File getFile() {
        if (file == null) {
            reload();
        }
        return file;
    }


//    private void saveRes() {
//        InputStream in = plugin.getResource(filename);
//        File outFile = new File(subfolder, filename);
//        File outDir = new File(subfolder);
//        if (!outDir.exists()) {
//            outDir.mkdirs();
//        }
//        try {
//            if (!outFile.exists()) {
//                OutputStream out = new FileOutputStream(outFile);
//                byte[] b = new byte[1024];
//                int read;
//                while ((read = in.read(b)) > 0) {
//                    out.write(b, 0, read);
//                }
//                out.close();
//                in.close();
//            }
//        } catch (IOException e) {
//            plugin.getLogger().log(Level.WARNING, "Failed to create File " + file.getName(), e);
//        }
//    }
}
