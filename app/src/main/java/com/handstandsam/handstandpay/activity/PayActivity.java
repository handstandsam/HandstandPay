package com.handstandsam.handstandpay.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.handstandsam.handstandpay.R;
import com.handstandsam.handstandpay.apdu.HandstandApduService;
import com.handstandsam.handstandpay.util.DefaultPaymentAppUtil;
import com.handstandsam.handstandpay.view.CreditCardView;
import com.handstandsam.handstandpay.view.PayView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(PayActivity.class);

    @Bind(R.id.pay_view)
    PayView payView;

    @Bind(R.id.credit_card)
    CreditCardView creditCardView;

    private Context context;
    private CardEmulation cardEmulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        cardEmulation = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(getApplicationContext()));

    }

    private boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setAsPreferredHceService() {
        boolean allowsForeground = cardEmulation.categoryAllowsForegroundPreference(CardEmulation.CATEGORY_PAYMENT);
        if (allowsForeground) {
            ComponentName hceComponentName = new ComponentName(context, HandstandApduService.class);
            cardEmulation.setPreferredService(PayActivity.this, hceComponentName);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void unsetAsPreferredHceService() {
        boolean allowsForeground = cardEmulation.categoryAllowsForegroundPreference(CardEmulation.CATEGORY_PAYMENT);
        if (allowsForeground) {
            ComponentName hceComponentName = new ComponentName(context, HandstandApduService.class);
            cardEmulation.unsetPreferredService(PayActivity.this);
        }
    }

    @OnClick(R.id.credit_card)
    void creditCardClicked() {
        startEditMagstripeActivity();
    }

    BroadcastReceiver animationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(HandstandApduService.PAYMENT_SENT)) {
                logger.debug("Payment Sent Broadcast Received");
                payView.start();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DefaultPaymentAppUtil.REQUEST_CODE_DEFAULT_PAYMENT_APP) {
            logger.debug("Result Code: " + resultCode);
            if (resultCode != 0) {
                //We are the default!
                logger.debug("Now the default app.");
            } else {
                //We are NOT the default
                logger.debug("NOT the default app.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLollipopOrHigher()) {
            setAsPreferredHceService();
        }
        LocalBroadcastManager.getInstance(PayActivity.this).registerReceiver(animationBroadcastReceiver, new IntentFilter(HandstandApduService.PAYMENT_SENT));

        init();
    }

    void init() {
        creditCardView.updateValues();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isLollipopOrHigher()) {
            unsetAsPreferredHceService();
        }
        LocalBroadcastManager.getInstance(PayActivity.this).unregisterReceiver(animationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.set_as_default) {
            DefaultPaymentAppUtil.ensureSetAsDefaultPaymentApp(PayActivity.this);
            return true;
        } else if (id == R.id.play_animation) {
            LocalBroadcastManager.getInstance(PayActivity.this).sendBroadcast(new Intent(HandstandApduService.PAYMENT_SENT));
            return true;
        } else if (id == R.id.edit_magstripe) {
            startEditMagstripeActivity();
            return true;
        } else if (id == R.id.nfc_settings) {
            NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(PayActivity.this);

            //Tell the user whether NFC is enabled
            Toast.makeText(PayActivity.this, "NFC is enabled: " + mNfcAdapter.isEnabled(), Toast.LENGTH_SHORT).show();

            //Show the settings regardless of whether it is enabled or not
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void startEditMagstripeActivity() {
        Intent dialogIntent = new Intent(this, EditMagstripeActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }


}
