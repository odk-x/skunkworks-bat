package org.odk.odknotifications;

import android.annotation.SuppressLint;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.opendatakit.application.CommonApplication;

public class getUserData extends CommonApplication {
    @SuppressWarnings("unused")
    private static final String TAG = "ODK-X Notify";

    public static final String t = "Survey";

    private FirebaseAnalytics analytics;

    private static getUserData singleton = null;

    public static getUserData getInstance() {
        return singleton;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        singleton = this;
        super.onCreate();
        analytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public int getApkDisplayNameResourceId() {
        return R.string.app_name;
    }

    @Override
    public int getConfigZipResourceId() {
        return R.raw.configzip;
    }

    @Override
    public int getSystemZipResourceId() {
        return R.raw.systemzip;
    }

    public String getVersionedToolName() {
        String versionDetail = this.getVersionDetail();
        return getString(R.string.app_name) + versionDetail;
    }

}
