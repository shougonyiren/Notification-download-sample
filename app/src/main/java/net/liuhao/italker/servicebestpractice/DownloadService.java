package net.liuhao.italker.servicebestpractice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {
    private DownLoadTask downLoadTask;
    private String downloadUrl;
    public DownloadService() {

    }
    private DownLoaderListener listener=new DownLoaderListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading.....",progress));
        }

        @Override
        public void onSuccess() {
            downLoadTask=null;
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadService.this,"Download Success将在不久出现一只小猫",Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onFaied() {
          downLoadTask=null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Faied",-1));
            Toast.makeText(DownloadService.this,"Download Faied",Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onPaused() {
            downLoadTask=null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            Toast.makeText(DownloadService.this,"Paused",Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onCancled() {
            downLoadTask=null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            Toast.makeText(DownloadService.this,"Cancled",Toast.LENGTH_SHORT)
                    .show();
        }
    };
    private  DownLoadBinder mBinder= new DownLoadBinder();
    @Override
    public IBinder onBind(Intent intent) {
          return mBinder;
    }
    class
    DownLoadBinder  extends Binder {
        public void startDownload(String url) {
            if (downLoadTask == null) {
                downloadUrl = url;
                downLoadTask = new DownLoadTask(listener);
                downLoadTask.execute(downloadUrl);
                //  getNotificationManager().notify(1,getNotification("Download Faied",-1));
                startForeground(1, getNotification("Downloading......", 0));
                Toast.makeText(DownloadService.this, "Downloading......", Toast.LENGTH_SHORT).show();

            }
        }

        public void pauseDownload() {
            if (downLoadTask != null) {
                downLoadTask.pasuseDownLload();
            }
        }

        public void cancelDownload() {
            if (downLoadTask != null) {
                downLoadTask.pasuseDownLload();
            } else {
                if (downloadUrl != null) {
                    //取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory//获取外部存储公共目录信息
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
        public void Appear() {
            getNotificationManager().notify(1,getNotification("Appear",1));
        }
    }
    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title,int progress){
        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME= "CHANNEL_ONE_ID";
        NotificationChannel notificationChannel= null;
//进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi =PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_launcher_background;
        builder.setChannelId(CHANNEL_ONE_ID);
        if(progress>=0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
    return builder.build();
    }
}