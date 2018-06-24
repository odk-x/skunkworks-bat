package android.notifications.odk.org.odknotifications;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;

public class MyJobService extends com.firebase.jobdispatcher.JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}