package de.mrstein.customheads.updaters;


public interface FileDownloaderCallback {

    void complete();

    void failed(AsyncFileDownloader.DownloaderStatus status);

}
