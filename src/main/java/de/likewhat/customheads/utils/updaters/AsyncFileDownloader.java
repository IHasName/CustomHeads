package de.likewhat.customheads.utils.updaters;

import com.google.common.io.Files;
import de.likewhat.customheads.CustomHeads;
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

    private String userAgent = "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";
    private String fileName;
    private String path;
    private String url;

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
                    }
                    callback.failed(DownloaderStatus.HTTP_ERROR.setDescription("Server Responded with " + connection.getResponseCode() + "(" + connection.getResponseMessage() + ")"));
                } catch (IOException e) {
                    callback.failed(DownloaderStatus.ERROR.setDescription("Failed to download File").setException(e));
                }
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public enum DownloaderStatus {
        ERROR(1), HTTP_ERROR(2), FILE_ERROR(3);

        String desc;

        Exception exception;

        DownloaderStatus(int id) {}

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

        public String toString() {
            return desc == null ? name() : name() + " " + desc;
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
