package com.threedevs.aj.HwInfoReceiver;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by aj on 29.10.16.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        //Hi
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_slide_1_title),
                getString(R.string.intro_slide_1_description),
                R.drawable.intro_hi,
                Color.parseColor("#66BB6A")));

        //help
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_slide_2_title),
                getString(R.string.intro_slide_2_description),
                R.drawable.ic_help_outline_white_48dp,
                Color.parseColor("#66BB6A")));

        //beta
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_slide_3_title),
                getString(R.string.intro_slide_3_description),
                R.drawable.icon,
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