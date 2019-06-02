package org.odk.odknotifications.Activities;

import android.content.Intent;
import android.os.Bundle;

import org.opendatakit.activities.BaseLauncherActivity;
import org.opendatakit.consts.IntentConsts;
import org.opendatakit.utilities.ODKFileUtils;

/**
 * This is the activity that gets launched first. It gets the app
 * name and then opens the homepage.
 */
public class Launcher extends BaseLauncherActivity {

  // the app name
  private String mAppName;

  @Override
  public String getAppName() {
    return mAppName;
  }

  @Override
  protected void setAppSpecificPerms() {
    return;
  }

  @Override
  public void onCreateWithPermission(Bundle savedInstanceState) {

    String mAppName = getIntent().getStringExtra(IntentConsts.INTENT_KEY_APP_NAME);
    if (mAppName == null) {
      mAppName = ODKFileUtils.getOdkDefaultAppName();
    }
    this.mAppName = mAppName;

    // ensuring directories exist
    ODKFileUtils.verifyExternalStorageAvailability();
    ODKFileUtils.assertDirectoryStructure(this.mAppName);

    // Launch the Homepage
    Intent i = new Intent(this, MainActivity.class);
    i.putExtra(IntentConsts.INTENT_KEY_APP_NAME, this.mAppName);
    startActivity(i);
    finish();
  }

  /**
   * We have to have this method because we implement DatabaseConnectionListener
   */
  @Override
  public void databaseAvailable() {
  }

  /**
   * We have to have this method because we implement DatabaseConnectionListener
   */
  @Override
  public void databaseUnavailable() {
  }

}
