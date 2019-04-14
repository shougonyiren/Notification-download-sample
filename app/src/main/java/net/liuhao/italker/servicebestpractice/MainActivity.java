package net.liuhao.italker.servicebestpractice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private  DownloadService.DownLoadBinder downLoadBinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadBinder=(DownloadService.DownLoadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startDownload =(Button)findViewById(R.id.start_download);
        Button pauseDownload =(Button)findViewById(R.id.pause_download);
        Button cancelDownload =(Button)findViewById(R.id.cancel_download);
        Button Appear =(Button)findViewById(R.id.Appear);
        startDownload.setOnClickListener(this);
        pauseDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);
        Intent intent=new Intent(this,DownloadService.class);
        startService(intent);//启动服务
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest .permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
     if(downLoadBinder==null){
         return;
     }
     switch (v.getId()){
         case R.id.start_download:{
             String url="http://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=http%3A%2F%2Fp1.qhimgs4.com%2Ft010e1ebcdd25c659cc.jpg&thumburl=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D646313154%2C3870320875%26fm%3D26%26gp%3D0.jpg" ;
             downLoadBinder.startDownload(url);
             break;
         }
         case R.id.pause_download:{
             downLoadBinder.pauseDownload();
             break;
         }
         case R.id.cancel_download:{
             downLoadBinder.cancelDownload();
             break;
         }
         case R.id.Appear:{
             downLoadBinder.Appear();
             break;
         }
         default:
             break;
     }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:{
               if(grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                   finish();
               }
               break;
           }
           default:
       }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
