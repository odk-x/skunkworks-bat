package android.notifications.odk.org.odknotifications;

import android.os.Bundle;
import android.widget.Toast;

import org.opendatakit.activities.BaseActivity;
import org.opendatakit.application.CommonApplication;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ServicesAvailabilityException;
import org.opendatakit.logging.WebLogger;
import org.opendatakit.properties.CommonToolProperties;

public class ProfileActivity extends BaseActivity {
    private String appName = "android.notifications.odk.org.odknotifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toast.makeText(this,getActiveUser()  , Toast.LENGTH_SHORT).show();

    }
    public String getActiveUser() {
        try {
            return getDatabase().getActiveUser(getAppName());
        } catch (ServicesAvailabilityException e) {
            WebLogger.getLogger(getAppName()).printStackTrace(e);
            return CommonToolProperties.ANONYMOUS_USER;
        }
    }
    public UserDbInterface getDatabase() {
        return ((CommonApplication) getApplication()).getDatabase();
    }

    public String getAppName() {
        return this.appName;
    }

    @Override
    public void databaseAvailable() {

    }

    @Override
    public void databaseUnavailable() {

    }
}
