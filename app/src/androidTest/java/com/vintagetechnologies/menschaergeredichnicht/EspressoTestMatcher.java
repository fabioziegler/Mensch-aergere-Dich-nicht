package com.vintagetechnologies.menschaergeredichnicht;

import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created by Demi on 07.05.2017.
 */

public class EspressoTestMatcher {

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }
}