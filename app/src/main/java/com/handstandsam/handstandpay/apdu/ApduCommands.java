package com.handstandsam.handstandpay.apdu;

/**
 * Source: https://github.com/dimalinux/SwipeYours/blob/master/src/main/java/to/noc/android/swipeyours/PaymentService.java
 */
public class ApduCommands {

    public static final byte[] ISO7816_UNKNOWN_ERROR_RESPONSE = {
            (byte) 0x6F, (byte) 0x00
    };

    /*
     *  PPSE (Proximity Payment System Environment)
     *
     *  This is the first select that a point of sale device will send to the payment device.
     */
    public static final byte[] PPSE_APDU_SELECT = {
            (byte) 0x00, // CLA (class of command)
            (byte) 0xA4, // INS (instruction); A4 = select
            (byte) 0x04, // P1  (parameter 1)  (0x04: select by name)
            (byte) 0x00, // P2  (parameter 2)
            (byte) 0x0E, // LC  (length of data)  14 (0x0E) = length("2PAY.SYS.DDF01")
            // 2PAY.SYS.DDF01 (ASCII values of characters used):
            // This value requests the card or payment device to list the application
            // identifiers (AIDs) it supports in the response:
            '2', 'P', 'A', 'Y', '.', 'S', 'Y', 'S', '.', 'D', 'D', 'F', '0', '1',
            (byte) 0x00 // LE   (max length of expected result, 0 implies 256)
    };

    public static final byte[] PPSE_APDU_SELECT_RESP = {
            (byte) 0x6F,  // FCI Template
            (byte) 0x23,  // length = 35
            (byte) 0x84,  // DF Name
            (byte) 0x0E,  // length("2PAY.SYS.DDF01")
            // Data (ASCII values of characters used):
            '2', 'P', 'A', 'Y', '.', 'S', 'Y', 'S', '.', 'D', 'D', 'F', '0', '1',
            (byte) 0xA5, // FCI Proprietary Template
            (byte) 0x11, // length = 17
            (byte) 0xBF, // FCI Issuer Discretionary Data
            (byte) 0x0C, // length = 12
            (byte) 0x0E,
            (byte) 0x61, // Directory Entry
            (byte) 0x0C, // Entry length = 12
            (byte) 0x4F, // ADF Name
            (byte) 0x07, // ADF Length = 7
            // Tell the POS (point of sale terminal) that we support the standard
            // Visa credit or debit applet: A0000000031010
            // Visa's RID (Registered application provider IDentifier) is 5 bytes:
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
            // PIX (Proprietary application Identifier eXtension) is the last 2 bytes.
            // 10 10 (means visa credit or debit)
            (byte) 0x10, (byte) 0x10,
            (byte) 0x87,  // Application Priority Indicator
            (byte) 0x01,  // length = 1
            (byte) 0x01,
            (byte) 0x90, // SW1  (90 00 = Success)
            (byte) 0x00  // SW2
    };

    /*
     *  MSD (Magnetic Stripe Data)
     */
    public static final byte[] VISA_MSD_SELECT = {
            (byte) 0x00,  // CLA
            (byte) 0xa4,  // INS
            (byte) 0x04,  // P1
            (byte) 0x00,  // P2
            (byte) 0x07,  // LC (data length = 7)
            // POS is selecting the AID (Visa debit or credit) that we specified in the PPSE
            // response:
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10,
            (byte) 0x00   // LE
    };


    public static final byte[] VISA_MSD_SELECT_RESPONSE = {
            (byte) 0x6F,  // File Control Information (FCI) Template
            (byte) 0x1E,  // length = 30 (0x1E)
            (byte) 0x84,  // Dedicated File (DF) Name
            (byte) 0x07,  // DF length = 7

            // A0000000031010  (Visa debit or credit AID)
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10,

            (byte) 0xA5,  // File Control Information (FCI) Proprietary Template
            (byte) 0x13,  // length = 19 (0x13)
            (byte) 0x50,  // Application Label
            (byte) 0x0B,  // length
            'V', 'I', 'S', 'A', ' ', 'C', 'R', 'E', 'D', 'I', 'T',
            (byte) 0x9F, (byte) 0x38,  // Processing Options Data Object List (PDOL)
            (byte) 0x03,  // length
            (byte) 0x9F, (byte) 0x66, (byte) 0x02, // PDOL value (Does this request terminal type?)
            (byte) 0x90,  // SW1
            (byte) 0x00   // SW2
    };


    /*
     *  GPO (Get Processing Options) command
     */
    private static final byte[] GPO_COMMAND = {
            (byte) 0x80,  // CLA
            (byte) 0xA8,  // INS
            (byte) 0x00,  // P1
            (byte) 0x00,  // P2
            (byte) 0x04,  // LC (length)
            // data
            (byte) 0x83,  // tag
            (byte) 0x02,  // length
            (byte) 0x80,    //  { These 2 bytes can vary, so we'll only        }
            (byte) 0x00,    //  { compare the header of this GPO command below }
            (byte) 0x00   // Le
    };


    /*
     *  The data in the request can vary, but it won't affect our response. This method
     *  checks the initial 4 bytes of an APDU to see if it's a GPO command.
     */
    public static boolean isGpoCommand(byte[] apdu) {
        return (apdu.length > 4 &&
                apdu[0] == GPO_COMMAND[0] &&
                apdu[1] == GPO_COMMAND[1] &&
                apdu[2] == GPO_COMMAND[2] &&
                apdu[3] == GPO_COMMAND[3]
        );
    }


    /*
     *  SwipeYours only emulates Visa MSD, so our response is not dependant on the GPO command
     *  data.
     */
    public static final byte[] GPO_COMMAND_RESPONSE = {
            (byte) 0x80,
            (byte) 0x06,  // length
            (byte) 0x00,
            (byte) 0x80,
            (byte) 0x08,
            (byte) 0x01,
            (byte) 0x01,
            (byte) 0x00,
            (byte) 0x90,  // SW1
            (byte) 0x00   // SW2
    };


    public static final byte[] READ_REC_COMMAND = {
            (byte) 0x00,  // CLA
            (byte) 0xB2,  // INS
            (byte) 0x01,  // P1
            (byte) 0x0C,  // P2
            (byte) 0x00   // length
    };
}
