package de.mrstein.customheads.updaters;

import com.google.common.io.Files;
import de.mrstein.customheads.CustomHeads;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/*
 *  Project: CustomHeads in AsyncFileDownloader
 *     by LikeWhat
 */

public class AsyncFileDownloader {

    private String url;
    private String path;
    private String fileName;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";

    public AsyncFileDownloader(String url, String fileName, String toPath) {
        this.url = url;
        this.fileName = fileName;
        path = toPath;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void startDownload(FileDownloaderCallback callback) {
        System.out.println("Downloading " + fileName + "...");
        new BukkitRunnable() {
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setReadTimeout(5000);
                    connection.addRequestProperty("User-Agent", userAgent);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        File file = new File(path, fileName);
                        if (!file.exists())
                            Files.createParentDirs(file);
                        ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
                        FileOutputStream out = new FileOutputStream(file);
                        out.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
                        out.flush();
                        out.close();
                        callback.complete();
                        return;
                    }
                    callback.failed(DownloaderStatus.HTTP_ERROR.setDescription("Server Responded with " + connection.getResponseCode()));
                } catch (IOException e) {
                    callback.failed(DownloaderStatus.ERROR.setDescription("Failed to download File").setException(e));
                    return;
                }

                callback.complete();
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public enum DownloaderStatus {
        ERROR(1, "ERROR"), HTTP_ERROR(2, "HTTP_ERROR"), FILE_ERROR(3, "FILE_ERROR");

        String desc;
        String name;

        Exception exception;

        DownloaderStatus(int id, String name) {
            this.name = name;
        }

        public Exception getException() {
            return exception;
        }

        public DownloaderStatus setException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public String getDescription() {
            return desc;
        }

        public DownloaderStatus setDescription(String description) {
            desc = description;
            return this;
        }
    }

    public interface FileDownloaderCallback {

        void complete();

        void failed(AsyncFileDownloader.DownloaderStatus status);

    }

    public interface AfterTask {
        void call();
    }

}
