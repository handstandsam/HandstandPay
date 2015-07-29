package com.handstandsam.handstandpay.model;

/**
 * Created by handstandtech on 7/25/15.
 */
public class MagStripeData {

    String name;
    String number;
    String expDate;
    boolean valid = false;

    public MagStripeData() {
        name = "XXXX XXXX";
        number = "XXXXXXXXXXXXXXXX";
        expDate = "XX/XX";
        valid = false;
    }

    public void setValidData(String name, String number, String expDate) {
        this.name = name;
        this.expDate = expDate;
        this.number = number;
        this.valid = true;
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] tokens = name.split("/");
        for (int i = tokens.length - 1; i >= 0; i--) {
            stringBuilder.append(tokens[i]);
            if (i != 0) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public String getExpDate() {
        return expDate;
    }

    public String getCardNumber() {
        StringBuilder displayNumber = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if (i != 0 && i % 4 == 0) {
                displayNumber.append(" ");
            }
            char c = number.charAt(i);
            displayNumber.append(c);

        }
        return displayNumber.toString();
    }


    public boolean isValid() {
        return valid;
    }
}
