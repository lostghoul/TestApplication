package com.example.testapplication.job;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sunshaogang on 2018/4/3.
 * 在Service内部进行调度 JobScheduler
 */

public class MultiJobService extends JobService {
    private final static String TAG = "MultiJobService";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            JobParameters param = (JobParameters) msg.obj;
            jobFinished(param, true);
            Toast.makeText(MultiJobService.this,"job finished ", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        scheduleJob();
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        Message message = Message.obtain();
        message.obj = params;
        mHandler.sendMessage(message);
        //返回false表示执行完毕，返回true表示需要开发者自己调用jobFinished方法通知系统已执行完成
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");
        mHandler.removeMessages(0);
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob() {
        Log.d(TAG, "scheduleJob");
        JobInfo.Builder builder = new JobInfo.Builder(0,
                new ComponentName(this, MultiJobService.class));
        //设置需要的网络条件，默认为JobInfo.NETWORK_TYPE_NONE即无网络时执行
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //重启后是否还要继续执行，此时需要声明权限RECEIVE_BOOT_COMPLETED
        //否则会报错“java.lang.IllegalArgumentException: Error: requested job be persisted without holding RECEIVE_BOOT_COMPLETED permission.”
        //而且RECEIVE_BOOT_COMPLETED需要在安装的时候就要声明，如果一开始没声明，在升级时才声明，那么依然会报权限不足的错误
//        builder.setPersisted(true);
        builder.setRequiresCharging(false); //是否在充电时执行
        builder.setRequiresDeviceIdle(false); //是否在空闲时执行
        //I was having this problem and after review some blogs and the official documentation,
        // I realised that JobScheduler is having difference behavior on Android N(24 and 25).
        // JobScheduler works with a minimum periodic of 15 mins.
//        builder.setPeriodic(1000); //设置时间间隔，单位毫秒
        builder.setMinimumLatency(1000); //设置至少延迟多久后执行，单位毫秒
        builder.setOverrideDeadline(1000); //设置最多延迟多久后执行，单位毫秒
        JobInfo ji = builder.build();

        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(ji);
    }

}