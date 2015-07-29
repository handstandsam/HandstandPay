package com.handstandsam.handstandpay.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.handstandsam.handstandpay.R;
import com.handstandsam.handstandpay.contstants.Constants;
import com.handstandsam.handstandpay.model.MagStripeData;
import com.handstandsam.handstandpay.util.MagStripeParser;
import com.handstandsam.handstandpay.util.PreferencesUtil;
import com.handstandsam.handstandpay.view.CreditCardView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by handstandtech on 7/25/15.
 */
public class EditMagstripeActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(EditMagstripeActivity.class);

    @Bind(R.id.credit_card)
    CreditCardView creditCardView;

    @Bind(R.id.swipe_data)
    EditText swipeData;

    @Bind(R.id.usb_reader)
    ImageView usbReaderImage;

    @Bind(R.id.default_button)
    Button defaultButton;

    @Bind(R.id.save_button)
    Button saveButton;

    @Bind(R.id.cancel_button)
    Button cancelButton;

    @Bind(R.id.clear_button)
    Button clearButton;

    @Bind(R.id.reset_button)
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_magstripe);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        setTitle("Set Visa Magstripe Data");
        logger.info("View Binding Complete");

        creditCardView.setClickable(false);

        swipeData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MagStripeData msd = MagStripeParser.parseTrackData(s.toString());
                creditCardView.updateValues(msd);
                if (msd.isValid()) {
                    resetButton.setVisibility(View.GONE);
                    defaultButton.setVisibility(View.GONE);
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    resetButton.setVisibility(View.VISIBLE);
                    defaultButton.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.GONE);
                }
            }
        });
        String data = PreferencesUtil.getRawSwipeData(EditMagstripeActivity.this);
        swipeData.setText(data);
    }

    @OnClick(R.id.usb_reader)
    void usbReaderImageClicked() {
        String url = getApplicationContext().getResources().getString(R.string.amazon_url_usb_reader);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.save_button)
    void saveButtonClicked() {
        String rawSwipeData = swipeData.getText().toString();
        MagStripeData msd = MagStripeParser.parseTrackData(rawSwipeData);
        if (msd.isValid()) {
            PreferencesUtil.setSwipeData(EditMagstripeActivity.this, rawSwipeData);
            Toast toast = Toast.makeText(EditMagstripeActivity.this, "Saved", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(EditMagstripeActivity.this, "Invalid Swipe Data, Cannot Save.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @OnClick(R.id.reset_button)
    void resetButtonClicked() {
        String raw = PreferencesUtil.getRawSwipeData(EditMagstripeActivity.this);
        swipeData.setText(raw);
    }

    @OnClick(R.id.clear_button)
    void clearButtonClicked() {
        swipeData.setText("");
    }

    @OnClick(R.id.default_button)
    void defaultButtonClicked() {
        swipeData.setText(Constants.DEFAULT_SWIPE_DATA);
    }

    @OnClick(R.id.cancel_button)
    void cancelButtonClicked() {
        finish();
    }

}
