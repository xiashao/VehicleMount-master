package car.ccut.com.vehicle.bean.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by panzhuowen on 3/18/2016.
 */
public class AjaxResponse {

    private String responseStatus;

    private String returnMsg;

    private Map<String, Object> responseData = new HashMap<>();

    private boolean error;

    private boolean success;

    private boolean waring;

    public boolean isOk() {
        return success;
    }

    public boolean isWarning() {
        return waring;
    }

    public boolean isError() {
        return error;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }

    public void setResponseData(Map<String, Object> responseData) {
        this.responseData = responseData;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }


}
