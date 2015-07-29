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
    public void test() {
        HandstandApduService service = new HandstandApduService();
        String rawSwipeData = Constants.DEFAULT_SWIPE_DATA;
        byte[] apduResponse = service.getReadRecordResponse(rawSwipeData);
        String apduResponseHex = HexUtil.byteArrayToHex(apduResponse);
        System.out.println(apduResponseHex);
        Assert.assertNotNull(apduResponseHex);
    }
}
