package car.ccut.com.vehicle.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import car.ccut.com.vehicle.ui.CarsManageActivity;
import car.ccut.com.vehicle.ui.MusicPlayActivity;
import car.ccut.com.vehicle.ui.NearbyFacilitesActivity;
import car.ccut.com.vehicle.ui.NearbyRefuelStationActivity;
import car.ccut.com.vehicle.ui.UserCenterActivity;
import car.ccut.com.vehicle.util.JsonParser;

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
 * Created by WangXin on 2016/5/8 0008.
 */
public class VoiceRecognition extends Service {

    private String TAG = "ivw";

    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 语音识别对象
    private SpeechRecognizer mIat;
    //唤醒得分
    private int resultScore;
    // 设置门限值 ： 门限值越低越容易被唤醒
    private final static int MIN = -20;
    private int curThresh = MIN;
    private String threshStr = "门限值：";
    private String ivwNetMode = "0";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private WakeuperListener mWakeuperListener;
    private RecognizerListener mRecognizerListener;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private InitListener mTtsInitListener;
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener;
    // 默认云端发音人
    public static String voicerCloud="xiaoyan";
    private static final int TTS_CONTROL=0;
    private static final int TTS_DEFULT = 10;
    private static int TTS_TYPE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIvw = VoiceWakeuper.createWakeuper(getApplicationContext(), null);
        mTts = SpeechSynthesizer.createSynthesizer(getApplicationContext(), mTtsInitListener);
        initListener();
        initParams();
    }

    private String getResource() {
        return ResourceUtil.generateResourcePath(getApplicationContext(),
                RESOURCE_TYPE.assets, "ivw/5729a06d.jet");
    }

    private void initParams(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw!=null){
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置识别引擎
//            mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + curThresh);
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "1:"
                    + curThresh);
            // 设置唤醒+识别模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置返回结果格式
            mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 启动唤醒
            int ret = mIvw.startListening(mWakeuperListener);
            if(ret != 0) {
                System.out.println("语音唤醒失败,错误码:"+ ret);
            } else {
                System.out.println("语音唤醒启动成功");
            }
        }
    }

    private void initListener(){

        mWakeuperListener = new WakeuperListener() {
            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onResult(WakeuperResult wakeuperResult) {
                try {
                    String text = wakeuperResult.getResultString();
                    JSONObject object;
                    object = new JSONObject(text);
                    resultScore = Integer.parseInt(object.optString("score"));
                    if (resultScore>MIN){
                        TTS_TYPE = TTS_CONTROL;
                        startTts("请说");
                      /* Toast.makeText(getApplicationContext(), "请说话",
                                Toast.LENGTH_SHORT).show();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                System.out.println(speechError.getPlainDescription(true));
            }

            @Override
            public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
            }

            @Override
            public void onVolumeChanged(int i) {

            }
        };

        /**
         * 听写监听器。
         */
        mRecognizerListener = new RecognizerListener() {

            @Override
            public void onBeginOfSpeech() {
                // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            }

            @Override
            public void onError(SpeechError error) {
                // Tips：
                // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                Toast.makeText(getApplication(),error.getPlainDescription(true),Toast.LENGTH_SHORT).show();
                mIvw.startListening(mWakeuperListener);
            }

            @Override
            public void onEndOfSpeech() {
                // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            }

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                String text = JsonParser.parseIatResult(results.getResultString());
                Intent intent;
                boolean intentFlag = false;
                if (text.contains("个人中心")){
                    intent = new Intent(getBaseContext(), UserCenterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    intentFlag = true;
                }else if (text.contains("附近设施")){
                    intent = new Intent(getBaseContext(), NearbyFacilitesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    intentFlag = true;
                }else if (text.contains("车辆管理")){
                    intent = new Intent(getBaseContext(), CarsManageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    intentFlag = true;
                }else if (text.contains("预约加油")){
                    intent = new Intent(getBaseContext(), NearbyRefuelStationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    intentFlag = true;
                }else if (text.contains("音乐播放")){
                    intent = new Intent(getBaseContext(), MusicPlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    intentFlag = true;
                }else {
                    if (!intentFlag){
//                        startTts("未识别您的命令");
                        intentFlag=false;
                    }
                }
                if(isLast) {
                    //TODO 最后的结果
                    TTS_TYPE = TTS_DEFULT;
                    mIvw.startListening(mWakeuperListener);
                }
            }

            @Override
            public void onVolumeChanged(int volume, byte[] data) {
                Log.d(TAG, "返回音频数据："+data.length);
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

            }
        };

        /**
         * 初始化监听。
         */
        mTtsInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    System.out.println("初始化失败,错误码："+code);
                } else {
                    // 初始化成功，之后可以调用startSpeaking方法
                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
                }
            }
        };

        mTtsListener = new SynthesizerListener(){

            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if (speechError==null){
                    // TODO 播放完成
                    switch (TTS_TYPE){
                        case TTS_CONTROL:
                            startVoice();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
    }

    private void startTts(String text){
        int code = mTts.startSpeaking(text,mTtsListener);
        if (code!=ErrorCode.SUCCESS){
            System.out.println(code);
        }
    }

    private void startVoice(){
        int ret=0;
        mIvw.stopListening();
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            System.out.println("听写失败,错误码：" + ret);
        } else {
        }
    }
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                System.out.println("初始化失败，错误码：" + code);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
        if (mIat!=null){
            mIat.destroy();
        }
        if (mTts!=null){
            mTts.destroy();
        }
    }
}
