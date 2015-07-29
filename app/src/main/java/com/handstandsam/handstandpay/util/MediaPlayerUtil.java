package com.handstandsam.handstandpay.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.handstandsam.handstandpay.R;

/**
 * Created by handstandtech on 7/24/15.
 */
public class MediaPlayerUtil {
    public static void playSuperMarioBrosCoinSound(final Context context) {
        new Runnable() {
            @Override
            public void run() {
                MediaPlayer
                        mp = MediaPlayer.create(context, R.raw.smb_coin);
                mp.setVolume(0.2f, 0.2f);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                });
            }
        }.run();
    }
}
