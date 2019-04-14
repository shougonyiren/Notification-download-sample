package net.liuhao.italker.servicebestpractice;

/**
 * Created by hasee on 2019-03-31.
 */
public interface DownLoaderListener {
    void onProgress(int progress);
    void onSuccess();
    void onFaied();
    void onPaused();
    void onCancled();
}
