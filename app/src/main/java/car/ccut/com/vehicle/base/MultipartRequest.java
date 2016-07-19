package car.ccut.com.vehicle.base;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import car.ccut.com.vehicle.bean.net.AjaxResponse;

/**
 * Created by panzhuowen on 2016/3/31.
 */
public class MultipartRequest extends Request<AjaxResponse> {

    private MultipartEntity entity = new MultipartEntity();

    private Response.Listener<AjaxResponse> mListener;

    private List<File> mFileParts;
    private String mFilePartName;
    private Map<String, String> mParams;

    /**
     * 单个文件上传
     *
     * @param method
     * @param url
     * @param errListener
     * @param listener
     * @param filePartName
     * @param file
     * @param params
     */

    public MultipartRequest(int method, String url, Response.ErrorListener errListener,
                            Response.Listener<AjaxResponse> listener, String filePartName, File file,
                            Map<String, String> params) {
        super(method, url, errListener);
        mFileParts = new ArrayList<>();
        if (file != null) {
            mFileParts.add(file);
        }
        mFilePartName = filePartName;
        mListener = listener;
        mParams = params;
        this.setRetryPolicy(new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        buildMultipartEntity();
    }

    /**
     * 多个文件对应一个key
     *
     * @param url
     * @param errorListener
     * @param listener
     * @param filePartName
     * @param files
     * @param params
     */
    public MultipartRequest(String url, Response.ErrorListener errorListener,
                            Response.Listener<AjaxResponse> listener, String filePartName,
                            List<File> files, Map<String, String> params) {
        super(Method.POST, url, errorListener);
        mFilePartName = filePartName;
        mListener = listener;
        mFileParts = files;
        mParams = params;
        this.setRetryPolicy(new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        buildMultipartEntity();
    }

    @Override
    protected Response<AjaxResponse> parseNetworkResponse(NetworkResponse response) {
        if (VolleyLog.DEBUG) {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers
                        .entrySet()) {
                    VolleyLog.d(entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Gson gson = new Gson();
        String json = null;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(gson.fromJson(json, AjaxResponse.class), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(AjaxResponse response) {
        mListener.onResponse(response);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        VolleyLog.d("getHeaders");
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            Log.d("EX", e.getLocalizedMessage());
        }

        return bos.toByteArray();

    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    private void buildMultipartEntity() {
        if (mFileParts != null && mFileParts.size() > 0) {
            for (File file : mFileParts) {
                entity.addPart(mFilePartName, new FileBody(file));
            }
        }
        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(
                            entry.getKey(),
                            new StringBody(entry.getValue(), Charset
                                    .forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }

    }
}
