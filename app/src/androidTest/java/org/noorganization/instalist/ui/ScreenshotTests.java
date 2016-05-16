package org.noorganization.instalist.ui;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityTestCase;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.noorganization.instalist.R;
import org.noorganization.instalist.view.activity.MainShoppingListView;


import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Enables the function to take screenshots of the app with fastlane tool screengrab
 * Created by lunero on 16.05.16.
 */
@RunWith(JUnit4.class)
public class ScreenshotTests {

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainShoppingListView> activityRule  = new ActivityTestRule<>(MainShoppingListView.class);

    @Test
    public void testTakeScreenshot(){
        Screengrab.screenshot("mainList");
        onView(withContentDescription(R.string.nav_drawer_open)).perform(click());

        Screengrab.screenshot("drawerView");
    }

}
