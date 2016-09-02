package com.example.androidsdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;

import cn.forward.androids.utils.AnimatorUtil;
import cn.forward.androids.utils.Util;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorUtilDemo extends Activity {

    public static enum Sex {
        FEMALE, // 性别，女
        MALE, // 性别，男
        UNKOWN // 性別，未知
    }


    private static final long ANIM_DEFAULT_DURATION = 400; // 默认动画持续时间
    private static final long ANIM_SHIELD_DURATION = 200; // 头像列表白色背景动画的持续时间

    private AnimatorUtil.AnimatorSetWrap mShowListAnimFemale, mShowListAnimMale,
            mHideListAnimFemale, mHideListAnimMale;

    private static final String DATA_URL_JPEG = "data:image/jpeg;base64,";
    private static final String DATA_URL_PNG = "data:image/png;base64,";

    private ScrollView mScrollView;
    private ClickListener mClickListener;

    private View mPortraitShield, mPortraitFemal, mPortraitMale;

    private boolean mIsShowingList = false; // 是否正在显示头像列表
    private Sex mSelectedSex = Sex.UNKOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animatorutil_demo);
        setTitle("AnimatorUtil");

        mClickListener = new ClickListener();
        mScrollView = (ScrollView) findViewById(R.id.portrait_list_container);
        mPortraitShield = findViewById(R.id.portrait_shield);
        mPortraitFemal = findViewById(R.id.register_sex_female);
        mPortraitMale = findViewById(R.id.register_sex_male);

        mPortraitFemal.setOnClickListener(mClickListener);
        mPortraitMale.setOnClickListener(mClickListener);
        findViewById(R.id.register_sex_contanier).setOnClickListener(mClickListener);

    }

    /**
     * @author huangziwei
     */
    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.register_sex_female) {
                View list = View.inflate(getApplicationContext(),
                        R.layout.register_portrait_female_list, null);
                mScrollView.removeAllViews();
                mScrollView.addView(list);
                showListAnim(Sex.FEMALE);
            } else if (v.getId() == R.id.register_sex_male) {
                View list = View.inflate(getApplicationContext(),
                        R.layout.register_portrait_male_list, null);
                mScrollView.removeAllViews();
                mScrollView.addView(list);
                showListAnim(Sex.MALE);
            } else if (v.getId() == R.id.register_sex_contanier) {
                if (mIsShowingList) {
                    hideListAnim(mSelectedSex);
                }
            }
        }
    }

    // 　显示头像列表的动画
    private void showListAnim(Sex sex) {
        mIsShowingList = true;
        if (sex == Sex.FEMALE) {
            mSelectedSex = Sex.FEMALE;
            mPortraitFemal.setClickable(false);
            if (mShowListAnimFemale == null) {
                float centerX = -mPortraitFemal.getX()
                        + (Util.getScreenWidth(this) / 2 - mPortraitFemal.getWidth() / 2);
                float maleTranX = -mPortraitMale.getX() - mPortraitMale.getWidth();
                mShowListAnimFemale = AnimatorUtil
                        .createAnimator()
                        .play(mPortraitFemal, ANIM_DEFAULT_DURATION, "translationX", 0, centerX)
                        .with(mPortraitMale, ANIM_DEFAULT_DURATION, "translationX", 0, maleTranX)
                        .with(mPortraitMale, ANIM_DEFAULT_DURATION, "alpha", 1, 0.2f)
                        .then(mPortraitShield,
                                ANIM_SHIELD_DURATION,
                                new AnimatorListenerAdapter() {
                                    public void onAnimationStart(
                                            Animator animation) {
                                        mPortraitShield.setVisibility(View.VISIBLE);
                                    }

                                    public void onAnimationEnd(
                                            Animator animation) {
                                        mScrollView.setVisibility(View.VISIBLE);
                                    }
                                }, "translationY", mPortraitShield.getHeight(),
                                0)
                        .then(mScrollView, ANIM_DEFAULT_DURATION,
                                "translationY", mScrollView.getHeight(), 0);
            }
            mShowListAnimFemale.start();
        } else {
            mSelectedSex = Sex.MALE;
            mPortraitMale.setClickable(false);
            if (mShowListAnimMale == null) {
                float portraitMaleX = -mPortraitMale.getX()
                        + (Util.getScreenWidth(this) / 2 - mPortraitMale
                        .getWidth() / 2);
                float portraitMaleY = -(mPortraitMale.getY() - mPortraitFemal
                        .getY());
                mShowListAnimMale = AnimatorUtil
                        .createAnimator()
                        .play(mPortraitMale, ANIM_DEFAULT_DURATION, "translationX", 0,
                                portraitMaleX)
                        .with(mPortraitMale, ANIM_DEFAULT_DURATION, "translationY", 0,
                                portraitMaleY)
                        .with(mPortraitFemal, ANIM_DEFAULT_DURATION,
                                "translationX", 0,
                                Util.getScreenWidth(this) - mPortraitFemal.getX())
                        .with(mPortraitFemal, ANIM_DEFAULT_DURATION, "alpha",
                                1, 0.2f)
                        .then(mPortraitShield,
                                ANIM_SHIELD_DURATION,
                                new AnimatorListenerAdapter() {
                                    public void onAnimationStart(
                                            Animator animation) {
                                        mPortraitShield.setVisibility(View.VISIBLE);
                                    }

                                    public void onAnimationEnd(
                                            Animator animation) {
                                        mScrollView.setVisibility(View.VISIBLE);
                                    }
                                }, "translationY", mPortraitShield.getHeight(),
                                0)
                        .then(mScrollView, ANIM_DEFAULT_DURATION,
                                "translationY", mScrollView.getHeight(), 0);
            }
            mShowListAnimMale.start();
        }

    }

    // 隐藏头像列表的动画
    public void hideListAnim(Sex sex) {
        mIsShowingList = false;
        mSelectedSex = Sex.UNKOWN;
        if (sex == Sex.FEMALE) {
            mPortraitFemal.setClickable(true);

            if (mHideListAnimFemale == null) {
                mHideListAnimFemale = AnimatorUtil
                        .createAnimator()
                        .play(mScrollView, ANIM_DEFAULT_DURATION, "translationY", 0,
                                mScrollView.getHeight())
                        .then(mPortraitShield, ANIM_SHIELD_DURATION,
                                "translationY", 0, mPortraitShield.getHeight())
                        .then(AnimatorUtil
                                .createAnimator()
                                .play(mPortraitMale, ANIM_DEFAULT_DURATION, "translationX", mPortraitMale.getTranslationX(), 0)
                                .with(mPortraitMale, ANIM_DEFAULT_DURATION, "alpha", mPortraitMale.getAlpha(), 1)
                                .with(mPortraitFemal, ANIM_DEFAULT_DURATION, "translationX",
                                        mPortraitFemal.getTranslationX(), 0))
                        .setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                mScrollView.removeAllViews();
                                mPortraitShield.setVisibility(View.VISIBLE);
                            }
                        });
            }
            mHideListAnimFemale.start();

        } else {
            mPortraitMale.setClickable(true);
            if (mHideListAnimMale == null) {
                mHideListAnimMale = AnimatorUtil
                        .createAnimator()
                        .play(mScrollView, ANIM_DEFAULT_DURATION, "translationY", 0,
                                mScrollView.getHeight())
                        .then(mPortraitShield, ANIM_SHIELD_DURATION,
                                "translationY", 0, mPortraitShield.getHeight())
                        .then(AnimatorUtil
                                .createAnimator()
                                .play(mPortraitMale, ANIM_DEFAULT_DURATION, "translationX",
                                        mPortraitMale.getTranslationX(), 0)
                                .with(mPortraitMale, ANIM_DEFAULT_DURATION, "translationY",
                                        mPortraitMale.getTranslationY(), 0)
                                .with(mPortraitFemal, ANIM_DEFAULT_DURATION,
                                        "translationX",
                                        mPortraitFemal.getTranslationX(), 0)
                                .with(mPortraitFemal, ANIM_DEFAULT_DURATION,
                                        "alpha", mPortraitFemal.getAlpha(), 1)
                                .setListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(
                                            Animator animation) {
                                        mScrollView.removeAllViews();
                                        mScrollView.setVisibility(View.GONE);
                                        mPortraitShield.setVisibility(View.GONE);
                                    }
                                }));
            }
            mHideListAnimMale.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mIsShowingList) {
            hideListAnim(mSelectedSex);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
