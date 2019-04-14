package net.liuhao.italker.servicebestpractice;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hasee on 2019-03-31.
 */
public class DownLoadTask extends AsyncTask<String,Integer,Integer> {

    public  static  final int TYPE_SUCCESS =0;//成功
    public  static  final int TYPE_FAILED =1;//失败
    public  static  final int TYPE_PAUSED =2;//暂停
    public  static  final int TYPE_CANCELED =3;//取消
    private DownLoaderListener listener;
    private boolean isCanceled =false;
    private boolean isPaused=false;
    private int lastProgress;

    public DownLoadTask(DownLoaderListener listener) {
        this.listener = listener;
    }
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is=null;
        RandomAccessFile saveFile =null;
        File file=null;
        try {
            long downloadedLength=0;//记录已下载的文件长度
            String downloadUrl=params[0];
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath();
            file=new File(directory+fileName);
            if(file.exists()){
                file.delete();
                file=new File(directory+fileName);
                downloadedLength=0;
            }
            long contentLength=getConTentLength(downloadUrl);
            if(contentLength==0){
                return TYPE_FAILED;
            } else if(contentLength ==downloadedLength){
                 return TYPE_SUCCESS;
            }
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder()//断点续传
                    .addHeader("RANGE","bytes="+downloadedLength+"-")
                    .url(downloadUrl)
                    .build();
            Response response=client.newCall(request).execute();
            if(response!=null){
                is=response.body().byteStream();
                saveFile=new RandomAccessFile(file,"rw");
                saveFile.seek(downloadedLength);
                byte[] b=new byte[1024];
                int total=0;
                int len;
                while ((len=is.read(b))!=-1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        saveFile.write(b,0,len);
                        int progress=(int)((total+downloadedLength)*100/contentLength); //计算已下载的百分比
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(is!=null){
                    is.close();
                }
                if(saveFile!=null){
                    saveFile.close();
                }
                if(isCanceled&&file!=null){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }




    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case  TYPE_FAILED:
                listener.onFaied();
                break;
            case  TYPE_PAUSED:
                listener.onPaused();
                break;
            case  TYPE_CANCELED:
                listener.onCancled();
                break;
            default:
                    break;
        }
    }
   public void pasuseDownLload(){
        isPaused=true;
   }
   public void cancelDownload(){
        isCanceled=true;
   }
    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress=values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    private long getConTentLength(String downloadUrl) throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response =client.newCall(request).execute();
        if(response!=null&&response.isSuccessful()){
            long contentLength=response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}





