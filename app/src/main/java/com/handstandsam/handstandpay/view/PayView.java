package com.handstandsam.handstandpay.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.handstandsam.handstandpay.R;
import com.handstandsam.handstandpay.activity.PayActivity;
import com.handstandsam.handstandpay.util.MediaPlayerUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by handstandtech on 7/25/15.
 */
public class PayView extends RelativeLayout {
    private static final Logger logger = LoggerFactory.getLogger(PayActivity.class);

    public PayView(Context context) {
        super(context);
    }

    public PayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Bind(R.id.android_guy)
    View androidGuyView;

    @Bind(R.id.imgFront)
    ImageView imgFront;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.coin_container)
    View coinContainer;

    @Bind(R.id.credit_card)
    CreditCardView creditCardView;

    boolean isBackVisible = false; // Boolean variable to check if the ic_launcher image is visible currently

    int coinFlipCount = 0;
    private static final int FLIP_COUNT = 5;

    boolean isFlipping = false;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.view_pay, null);
        addView(v);
        ButterKnife.bind(PayView.this);
    }


    void androidGuyFlipAnimation() {
        int coinFlipDuration = getResources().getInteger(R.integer.coin_flip_duration);
        int resetDelay = getResources().getInteger(R.integer.reset_delay);

        AnimationSet coinAnimationSet = newAnimationSet();

        Animation moveDown = AnimationUtils.loadAnimation(getContext(), R.anim.coin_fall_down);
        coinAnimationSet.addAnimation(moveDown);
        imgFront.setVisibility(View.INVISIBLE);
        imgBack.setVisibility(View.INVISIBLE);
        coinContainer.setVisibility(View.INVISIBLE);


        long theTime = moveDown.getDuration();

        for (int i = 0; i < FLIP_COUNT + 1; i++) {
            Animation coinHitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.coin_hit);
            coinHitAnimation.setStartOffset(theTime);
            theTime += coinFlipDuration;
            coinAnimationSet.addAnimation(coinHitAnimation);
        }


        //-------------


        //Andrioid Guy Animation
        AnimationSet androidGuyAnimationSet = newAnimationSet();

        long animationTime = 0;

        //Rotate/Flip
        int preHandstandDuration = getResources().getInteger(R.integer.pre_handstand_duration);
        int handstandDuration = getResources().getInteger(R.integer.handstand_duration);
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.handstand_from_left);
        animationTime += preHandstandDuration + handstandDuration;

        androidGuyAnimationSet.addAnimation(rotateAnimation);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                coinFlipCount = 0;
                doFlip();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        for (int i = 0; i < FLIP_COUNT; i++) {
            Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.jump_up);
            slideUpAnimation.setStartOffset(animationTime);

            Animation slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.jump_down);
            slideDownAnimation.setStartOffset(animationTime + slideUpAnimation.getDuration());
            animationTime += coinFlipDuration;

            androidGuyAnimationSet.addAnimation(slideUpAnimation);
            androidGuyAnimationSet.addAnimation(slideDownAnimation);
        }

        animationTime += resetDelay;
        Animation flip180Animation = AnimationUtils.loadAnimation(getContext(), R.anim.flip_180);
        flip180Animation.setStartOffset(animationTime);
        androidGuyAnimationSet.addAnimation(flip180Animation);

        Animation coinUpAndOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.coin_up_and_out);
        coinUpAndOutAnimation.setStartOffset(animationTime);
        coinAnimationSet.addAnimation(coinUpAndOutAnimation);

        //Credit Card Animations
        AnimationSet creditCardAnimationSet = newAnimationSet();

        //Fade Out
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        creditCardAnimationSet.addAnimation(fadeOut);

        //Start Animations
        creditCardView.startAnimation(creditCardAnimationSet);
        androidGuyView.startAnimation(androidGuyAnimationSet);
        coinContainer.startAnimation(coinAnimationSet);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                coinContainer.setVisibility(View.VISIBLE);
                imgFront.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.VISIBLE);
            }
        }, 0);

        androidGuyAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Credit Card Animations
                AnimationSet creditCardAnimationSet = newAnimationSet();

                //Fade Out
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                creditCardAnimationSet.addAnimation(fadeOut);

                //Start Animations
                creditCardView.setAlpha(0);
                creditCardView.setVisibility(View.INVISIBLE);
                creditCardView.startAnimation(creditCardAnimationSet);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        creditCardView.setAlpha(1);
                        creditCardView.setVisibility(View.VISIBLE);
                    }
                }, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private AnimationSet newAnimationSet() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setFillEnabled(true);
        animationSet.setFillBefore(true);
        animationSet.setFillAfter(true);
        return animationSet;
    }


    private void doFlip() {

        logger.debug("doFlip()");
        coinFlipCount++;
        //Play Coin Sound in background thread
        MediaPlayerUtil.playSuperMarioBrosCoinSound(getContext());

        final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.coin_flip_right_out);
        final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.coin_flip_left_in);
        if (!isBackVisible) {
            //Is showing ic_launcher
            setRightOut.setTarget(imgFront);
            setLeftIn.setTarget(imgBack);
            isBackVisible = true;
        } else {
            //Is showing back
            setRightOut.setTarget(imgBack);
            setLeftIn.setTarget(imgFront);
            isBackVisible = false;
        }

        setRightOut.start();
        setLeftIn.start();

        setRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logger.debug("Flip # " + coinFlipCount + " Complete");
                if (coinFlipCount < FLIP_COUNT) {
                    doFlip();
                } else {
                    isFlipping = false;
                    coinFlipCount = 0;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void start() {
        if (!isFlipping) {
            isFlipping = true;
            androidGuyFlipAnimation();
        }
    }
}
