package org.odk.odknotifications.Activities;

import android.app.Activity;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.odknotifications.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static android.app.Instrumentation.ActivityResult;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant("android.permission.CAMERA");

    @Rule
    public IntentsTestRule<MainActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubQRCodeIntent() {
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK,null));
    }

    @Test
    public void databaseConnectionTest(){
        onView(allOf(withId(R.id.name_tv),not(withText("Bat"))));
    }

    @Test
    public void qrCodeIntentTest(){
        onView(withId(R.id.fab)).perform(click());
        intended(allOf(
                hasAction("com.google.zxing.client.android.SCAN")));
    }


}
