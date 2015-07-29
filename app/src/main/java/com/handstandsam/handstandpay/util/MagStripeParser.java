package com.handstandsam.handstandpay.util;

import com.handstandsam.handstandpay.model.MagStripeData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by handstandtech on 7/25/15.
 */
public class MagStripeParser {

    public static final Pattern TRACK_2_PATTERN = Pattern.compile(".*;(\\d{12,19}=\\d{1,128})\\?.*");

    /*
 *  Returns true if the passed in track data was successfully parsed, otherwise false.
 */
    public static MagStripeData parseTrackData(String trackData) {
        trackData = trackData.trim();
        MagStripeData msd = new MagStripeData();
        Pattern pattern = Pattern.compile("^\\s*(?:%B(\\d+)\\^([^^]+)\\^\\d+)?\\?;(\\d+)=(\\d\\d)(\\d\\d)\\d+\\?\\s*$");
        Matcher match = pattern.matcher(trackData);

        boolean isValid = match.matches();

        if (isValid) {

            String fullName = match.group(2);
            String cardNumber = match.group(3);
            String expDate = match.group(5) + "/" + match.group(4);

            msd.setValidData(fullName, cardNumber, expDate);
        }

        return msd;
    }
}
