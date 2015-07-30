import com.handstandsam.handstandpay.apdu.ApduCommands;
import com.handstandsam.handstandpay.apdu.HandstandApduService;
import com.handstandsam.handstandpay.contstants.Constants;
import com.handstandsam.handstandpay.util.HexUtil;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by handstandtech on 7/26/15.
 */
public class ApduTest {

    @Test
    public void testDefaultSwipeDataReadRecordResponse() {
        String apduResponseHex = getDefaultSwipeDataReadRecordResponse();
        System.out.println(apduResponseHex);
        Assert.assertNotNull(apduResponseHex);
    }

    public static String getDefaultSwipeDataReadRecordResponse() {
        HandstandApduService service = new HandstandApduService();
        String rawSwipeData = Constants.DEFAULT_SWIPE_DATA;
        byte[] apduResponse = service.getReadRecordResponse(rawSwipeData);
        return HexUtil.byteArrayToHex(apduResponse);
    }

    @Test
    public void apduSelectPPSE() {
        String exampleGPORequest = "80A80000048302800000";

        //SELECT PPSE
        System.out.println("SELECT PPSE");
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.PPSE_APDU_SELECT));
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.PPSE_APDU_SELECT_RESP));

        //SELECT VISA AID
        System.out.println("SELECT VISA AID");
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.VISA_MSD_SELECT));
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.VISA_MSD_SELECT_RESPONSE));

        //Get Processing Options
        System.out.println("GET PROCESSING OPTIONS - GPO");
        System.out.println(exampleGPORequest);
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.GPO_COMMAND_RESPONSE));

        //Read Record
        System.out.println("READ RECORD");
        System.out.println(HexUtil.byteArrayToHex(ApduCommands.READ_REC_COMMAND));
        System.out.println(getDefaultSwipeDataReadRecordResponse());
    }
}
