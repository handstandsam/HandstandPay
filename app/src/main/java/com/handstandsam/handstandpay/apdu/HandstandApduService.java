package com.handstandsam.handstandpay.apdu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;

import com.handstandsam.handstandpay.activity.PayActivity;
import com.handstandsam.handstandpay.util.HexUtil;
import com.handstandsam.handstandpay.util.MagStripeParser;
import com.handstandsam.handstandpay.util.PreferencesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * Terminology
 * APDU - Proximity Payment System Environment
 * PPSE - Proximity Payment System Environment
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class HandstandApduService extends HostApduService {

    public static final String PAYMENT_SENT = "PAYMENT_SENT";

    private static final Logger logger = LoggerFactory.getLogger(HandstandApduService.class);

    boolean isProcessing = false;

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

        if (!isProcessing) {
            startPaymentActivityInBackgroundThread();
            isProcessing = true;
        }

        String inboundApduDescription;
        byte[] responseApdu;

        if (Arrays.equals(ApduCommands.PPSE_APDU_SELECT, commandApdu)) {
            inboundApduDescription = "Received PPSE select: ";
            responseApdu = ApduCommands.PPSE_APDU_SELECT_RESP;
        } else if (Arrays.equals(ApduCommands.VISA_MSD_SELECT, commandApdu)) {
            inboundApduDescription = "Received Visa-MSD select: ";
            responseApdu = ApduCommands.VISA_MSD_SELECT_RESPONSE;
        } else if (ApduCommands.isGpoCommand(commandApdu)) {
            inboundApduDescription = "Received GPO (get processing options): ";
            responseApdu = ApduCommands.GPO_COMMAND_RESPONSE;
        } else if (Arrays.equals(ApduCommands.READ_REC_COMMAND, commandApdu)) {
            inboundApduDescription = "Received READ REC: ";
            String rawSwipeData = PreferencesUtil.getRawSwipeData(getApplicationContext());
            responseApdu = getReadRecordResponse(rawSwipeData);
        } else {
            inboundApduDescription = "Received Unhandled APDU: ";
            responseApdu = ApduCommands.ISO7816_UNKNOWN_ERROR_RESPONSE;
        }

        String inputHex = HexUtil.byteArrayToHex(commandApdu);
        String outputHex = HexUtil.byteArrayToHex(responseApdu);
        logger.debug(inboundApduDescription);
        logger.debug("Input Hex\n" + inputHex);
        logger.debug("Output Hex\n" + outputHex);

        return responseApdu;
    }

    @Override
    public void onDeactivated(int reason) {
        isProcessing = false;
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(PAYMENT_SENT));
    }


    private void startPaymentActivityInBackgroundThread() {
        new Runnable() {
            @Override
            public void run() {

                //Start Payment Activity
                Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getApplicationContext().startActivity(intent);

                //Vibrate
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }.run();
    }

    public byte[] getReadRecordResponse(String swipeData) {
        byte[] readRecResponse = null;
        Matcher matcher = MagStripeParser.TRACK_2_PATTERN.matcher(swipeData);
        if (matcher.matches()) {

            String track2EquivData = matcher.group(1);
            // convert the track 2 data into the required byte representation
            track2EquivData = track2EquivData.replace('=', 'D');
            if (track2EquivData.length() % 2 != 0) {
                // add an 'F' to make the hex string a whole number of bytes wide
                track2EquivData += "F";
            }

            // Each binary byte is represented by 2 4-bit hex characters
            int track2EquivByteLen = track2EquivData.length() / 2;

            readRecResponse = new byte[6 + track2EquivByteLen];

            ByteBuffer bb = ByteBuffer.wrap(readRecResponse);
            bb.put((byte) 0x70);                            // EMV Record Template tag
            bb.put((byte) (track2EquivByteLen + 2));        // Length with track 2 tag
            bb.put((byte) 0x57);                                // Track 2 Equivalent Data tag
            bb.put((byte) track2EquivByteLen);                   // Track 2 data length
            bb.put(HexUtil.hexToByteArray(track2EquivData));           // Track 2 equivalent data
            bb.put((byte) 0x90);                            // SW1
            bb.put((byte) 0x00);                            // SW2
        } else {
            logger.warn("ApduService processed bad swipe data");
        }
        return readRecResponse;
    }
}
