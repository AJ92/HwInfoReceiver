package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by aj on 29.10.16.
 */

public class IntroUpdateAppActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        boolean use_dark_theme = sharedPref.getBoolean(getString(R.string.setting_use_dark_theme_pref), false);

        if(use_dark_theme) {
            setTheme(R.style.AppTheme_Dark);
        }
        else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        //sensors
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.appupdate_slide_1_title),
                getString(R.string.appupdate_slide_1_description),
                0,
                Color.parseColor("#66BB6A")));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.appupdate_slide_2_title),
                getString(R.string.appupdate_slide_2_description),
                0,
                Color.parseColor("#66BB6A")));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.appupdate_slide_3_title),
                getString(R.string.appupdate_slide_3_description),
                0,
                Color.parseColor("#66BB6A")));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#81C784"));
        setSeparatorColor(Color.parseColor("#A5D6A7"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}