package com.example.testapplication.jobscheduler;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sunshaogang on 2018/4/3.
 */

public class JobSchedulerUtils {
    public static final String TAG = "JobSchedulerUtils";
    //将任务作业发送到作业调度中去
    public static void scheduleSimpleJob(Activity activity) {
        Log.d(TAG, "start scheduleJob");
        JobInfo.Builder builder = new JobInfo.Builder(0,
                new ComponentName(activity, SimpleJobService.class));
        //设置需要的网络条件，默认为JobInfo.NETWORK_TYPE_NONE即无网络时执行
        //NETWORK_TYPE_NONE
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //builder.setPersisted(true); //重启后是否还要继续执行
        builder.setRequiresCharging(false); //是否在充电时执行
        builder.setRequiresDeviceIdle(false); //是否在空闲时执行
        //builder.setPeriodic(1000); //设置时间间隔，单位毫秒
        //setPeriodic不能和setMinimumLatency、setOverrideDeadline这两个同时调用
        //否则会报错“java.lang.IllegalArgumentException: Can't call setMinimumLatency() on a periodic job”
        //“java.lang.IllegalArgumentException: Can't call setOverrideDeadline() on a periodic job”
        builder.setMinimumLatency(500); //设置至少延迟多久后执行，单位毫秒
        builder.setOverrideDeadline(3000); //设置最多延迟多久后执行，单位毫秒
        JobInfo ji = builder.build();

        JobScheduler js = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(ji);
    }
    public static void scheduleMultiJob(Activity activity) {
        Intent intent = new Intent(activity,MultiJobService.class);
        activity.startService(intent);
    }
}
