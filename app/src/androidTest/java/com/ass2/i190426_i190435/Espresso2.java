package com.ass2.i190426_i190435;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Espresso2 {
    @Rule
    public ActivityTestRule<SignIn> activityRule =
            new ActivityTestRule<SignIn>(SignIn.class);

    @Before
    public void before() {
        Intents.init();
    }

    @Test
    public void Test1(){

        onView(withId(R.id.forgetpass)).perform(click());

        intended(hasComponent(ForgotPassword.class.getName()));

    }

    @Test
    public void Test2(){
        onView(withId(R.id.signup)).perform(click());

        intended(hasComponent(SignUp.class.getName()));

//        onView(withId(R.id.forgetpass)).perform(click());
//
//        intended(hasComponent(ForgotPassword.class.getName()));

    }

    @After
    public void after(){
        Intents.release();
    }
}
