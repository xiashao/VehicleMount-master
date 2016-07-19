package car.ccut.com.vehicle.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.SIMCardInfo;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * *
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 * Created by WangXin on 2016/4/14 0014.
 */
public class VerifyTelActivity extends BaseActivity {
    @Bind(R.id.register_phone)
    EditText registerPhone;
    @Bind(R.id.register_captcha_code)
    EditText verifyCode;
    @Bind(R.id.register_captcha_button)
    Button captchButton;
    @Bind(R.id.verify_button)
    Button verifyButton;

    private String phString;
    private SmsObserver smsObserver;
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");
    private MyCount mc;
    private  String identification;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_verify_tel;
    }

    private void getSmsFromPhone(){
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type
        String where = " date > " + (System.currentTimeMillis() - 5 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            //这里我是要获取自己短信服务号码中的验证码~~
            Log.e("body",body);
            Pattern pattern = Pattern.compile("[0-9]{4}");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String res = matcher.group();
                verifyCode.setText(res);
                verifyCode();
            }
        }
    }

    public void verifyCode() {
        if (!TextUtils.isEmpty(verifyCode.getText().toString())) {
            showWaitDialog();
            SMSSDK.submitVerificationCode("86", phString, verifyCode.getText().toString());
        } else {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    public Handler smsHandler = new Handler() {};

    @Override
    public void initView() {
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
        setTitle("短信验证");
        SMSSDK.initSDK(this, ConstantValue.APPKEY, ConstantValue.APPSECRETE);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
        SIMCardInfo siminfo = new SIMCardInfo(this);
        String mPhoneNumber = siminfo.getNativePhoneNumber();
        if(mPhoneNumber!=null){
            int len = mPhoneNumber.length();
            if (len>11){
                mPhoneNumber = mPhoneNumber.substring(len-11,len);
            }
            registerPhone.setText(mPhoneNumber);
        }
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    //判断前一个activity
                    if (identification.equals("register")) {
                        Intent it1 = new Intent(VerifyTelActivity.this, RegisterBaseInfoActivity.class);
                        it1.putExtra("phone", phString);
                        startActivity(it1);
                    }
                    else if (identification.equals("forget")){
                        Intent it1 = new Intent(VerifyTelActivity.this, ResetPasswordActivity.class);
                        it1.putExtra("phone", phString);
                        startActivity(it1);
                    }
                    else if (identification.equals("change")){
                        Intent it1 = new Intent(VerifyTelActivity.this, UserCenterActivity.class);
                        it1.putExtra("phone", phString);
                        startActivity(it1);
                    }
                    AppManager.getAppManager().finishActivity(VerifyTelActivity.this);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(VerifyTelActivity.this, "验证码已发送,请稍后", Toast.LENGTH_SHORT).show();
                }
            } else {
                hideWaitDialog();
                ((Throwable) data).printStackTrace();
                Toast.makeText(VerifyTelActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    public void initData() {
        identification = getIntent().getStringExtra("identification");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        if (mc!=null){
            mc.cancel();
        }
    }

    @OnTextChanged(R.id.register_captcha_code)
    public void usrChange(CharSequence s, int start, int before, int count){
        if (verifyCode.getText().toString().length() > 0) {
            verifyButton.setVisibility(View.VISIBLE);
        } else {
            verifyButton.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    @OnClick({R.id.iv_title_back,R.id.register_captcha_button,R.id.verify_button,R.id.iv_clear_usr})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.register_captcha_button:
                if (!TextUtils.isEmpty(registerPhone.getText().toString())){
                    phString = registerPhone.getText().toString().trim();
                    verifyCode.setText("");
                    checkNumber(phString);
                }else {
                    Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verify_button:
                verifyCode();
                break;
            case R.id.iv_clear_usr:
                registerPhone.setText("");
                break;
        }
    }

    /*定义一个倒计时的内部类*/
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            captchButton.setEnabled(true);
            captchButton.setText("获取验证码");
        }
        @Override
        public void onTick(long millisUntilFinished) {
            captchButton.setText(millisUntilFinished / 1000 + "秒...");
        }
    }

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }
    }

    private void checkNumber(String phoneNum){
        showWaitDialog();
        Map params = new HashMap();
        params.put("userName",phoneNum);
        JsonRequestWithAuth<AjaxResponse> checkNum = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.CHECK_PHONE_NUMBER, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                hideWaitDialog();
                if (response.isOk()){
                    SMSSDK.getVerificationCode("86", phString);
                    mc = new MyCount(30000, 1000);
                    mc.start();
                }else {
                    Toast.makeText(VerifyTelActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                }
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(checkNum);
        MyApplication.getHttpQueues().start();
    }

}
