package com.threedevs.aj.HwInfoReceiver;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by aj on 29.10.16.
 */

public class IntroServerConnectionActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        //intro
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_1_title),
                getString(R.string.server_slide_1_description),
                R.drawable.ic_wifi_black_48dp,
                Color.parseColor("#66BB6A")));

        //get hwinfo
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_2_title),
                getString(R.string.server_slide_2_description),
                R.drawable.hwinfo,
                Color.parseColor("#66BB6A")));

        //setup hwinfo
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_3_title),
                getString(R.string.server_slide_3_description),
                R.drawable.setup_hwinfo,
                Color.parseColor("#66BB6A")));

        //setup hwinfo
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_4_title),
                getString(R.string.server_slide_4_description),
                R.drawable.setup_sensors_hwinfo,
                Color.parseColor("#66BB6A")));


        //get hwinfotransmitter
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_5_title),
                getString(R.string.server_slide_5_description),
                R.drawable.ic_get_app_black_48dp,
                Color.parseColor("#66BB6A")));

        //get ip
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_6_title),
                getString(R.string.server_slide_6_description),
                R.drawable.setup_hwinforeceiver,
                Color.parseColor("#66BB6A")));

        //firewall
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_7_title),
                getString(R.string.server_slide_7_description),
                R.drawable.ic_network_check_black_48dp,
                Color.parseColor("#66BB6A")));

        //network
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_8_title),
                getString(R.string.server_slide_8_description),
                R.drawable.ic_network_check_black_48dp,
                Color.parseColor("#66BB6A")));

        //add server
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_9_title),
                getString(R.string.server_slide_9_description),
                R.drawable.ic_dns_black_48dp,
                Color.parseColor("#66BB6A")));


        //establish a connection
        addSlide(AppIntroFragment.newInstance(
                getString(R.string.server_slide_10_title),
                getString(R.string.server_slide_10_description),
                R.drawable.setup_connection,
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