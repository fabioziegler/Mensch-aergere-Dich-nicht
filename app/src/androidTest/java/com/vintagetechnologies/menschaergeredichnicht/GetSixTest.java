package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.vintagetechnologies.menschaergeredichnicht.EspressoTestMatcher.withDrawable;


/**
 * Created by Demi on 07.05.2017.
 */
    @RunWith(AndroidJUnit4.class)
    public class GetSixTest {

    @Rule
    public ActivityTestRule<Spieloberflaeche> mActivityRule =
            new ActivityTestRule<>(Spieloberflaeche.class);


    @Test
    public void CheatTest(){
        //Set Cheating auf ture ToDo funktioniert das so?
        Cheat schummeln = new Cheat();
        schummeln.setPlayerCheating(true);

        //Press DiceBtn
        onView(withId(R.id.imageButton_wuerfel)).perform(click());

        //Funktioniert nocht nicht weil es nach button klick zunächst zwischen vielen Bilder wechselt
        //ToDo: Lösungfinden
        //onView(withId(R.id.imgViewDice)).check(matches(withDrawable(R.drawable.dice6)));
    }

    }

