package com.handstandsam.handstandpay.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handstandsam.handstandpay.R;
import com.handstandsam.handstandpay.activity.PayActivity;
import com.handstandsam.handstandpay.contstants.Constants;
import com.handstandsam.handstandpay.model.MagStripeData;
import com.handstandsam.handstandpay.util.MagStripeParser;
import com.handstandsam.handstandpay.util.PreferencesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by handstandtech on 7/25/15.
 */
public class CreditCardView extends LinearLayout implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(PayActivity.class);

    public CreditCardView(Context context) {
        super(context);
    }

    public CreditCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Bind(R.id.customer_name)
    TextView customerName;

    @Bind(R.id.exp_date)
    TextView expirationDate;

    @Bind(R.id.card_number)
    TextView cardNumber;


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.view_credit_card, null);
        addView(v);
        ButterKnife.bind(CreditCardView.this);
        updateValues(new MagStripeData());
    }

    public void updateValues() {
        MagStripeData msd = PreferencesUtil.getMagStripeData(getContext());
        updateValues(msd);
    }

    public void updateValues(MagStripeData msd) {
        customerName.setText(msd.getName());
        expirationDate.setText(msd.getExpDate());
        cardNumber.setText(msd.getCardNumber());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        logger.debug("onSharedPreferenceChanged: key=" + key);
        if (PreferencesUtil.PREF_KEY_SWIPE_DATA.equals(key)) {
            String swipeData = prefs.getString(PreferencesUtil.PREF_KEY_SWIPE_DATA, Constants.DEFAULT_SWIPE_DATA);
            MagStripeData msd = MagStripeParser.parseTrackData(swipeData);
            updateValues(msd);
        }
    }
}
