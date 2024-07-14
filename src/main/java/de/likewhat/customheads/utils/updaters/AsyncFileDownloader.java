package de.likewhat.customheads.utils.updaters;

import com.google.common.io.Files;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Utils;
import lombok.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/*
 *  Project: CustomHeads in AsyncFileDownloader
 *     by LikeWhat
 */

@AllArgsConstructor
@RequiredArgsConstructor
public class AsyncFileDownloader {

    @NonNull
    private final String url;
    @NonNull
    private final String fileName;
    @NonNull
    private final String path;
    @Setter
    private String userAgent = "AsyncFileDownloader/1.1";

    public void startDownload(FileDownloaderCallback callback) {
        CustomHeads.getPluginLogger().info("Downloading " + fileName + "...");
        Utils.runAsync(new BukkitRunnable() {
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
                    callback.failed(new DownloaderError(DownloaderStatus.HTTP_ERROR, HttpException.fromConnection(connection)));
                } catch (IOException e) {
                    callback.failed(new DownloaderError(DownloaderStatus.ERROR, e));
                }
            }
        });
    }

    @Getter
    public enum DownloaderStatus {
        ERROR, HTTP_ERROR, FILE_ERROR;

        Exception exception;

        public DownloaderStatus setException(Exception exception) {
            this.exception = exception;
            return this;
        }
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class DownloaderError {
        @NonNull
        private DownloaderStatus status;
        private Exception exception;
    }

    @Getter
    public static class HttpException extends Exception {
        private final int responseCode;
        private final URLConnection originalConnection;

        public HttpException(int responseCode, String responseMessage) {
            super(responseMessage);
            this.responseCode = responseCode;
            this.originalConnection = null;
        }

        /**
         * Gets the Response Message (equivalent to {@link java.lang.Throwable#getMessage})
         * @return The Response Message
         */
        public String getResponseMessage() {
            return super.getMessage();
        }

        /**
         * Converts a HttpURLConnection into a HttpException
         * @param connection The URL Connection
         * @return The HttpException from the
         * @throws IOException see {@link java.net.HttpURLConnection#getResponseCode} and {@link java.net.HttpURLConnection#getResponseMessage}
         */
        public static HttpException fromConnection(HttpURLConnection connection) throws IOException {
            return new HttpException(connection.getResponseCode(), connection.getResponseMessage());
        }
    }

    public interface FileDownloaderCallback {
        void complete();

        void failed(AsyncFileDownloader.DownloaderError error);
    }

    public interface AfterTask {
        void call();
    }

}
