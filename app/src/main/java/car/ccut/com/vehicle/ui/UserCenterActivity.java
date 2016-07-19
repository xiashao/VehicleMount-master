package car.ccut.com.vehicle.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.User;
import car.ccut.com.vehicle.bean.address.Province;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.MD5;
import de.hdodenhof.circleimageview.CircleImageView;

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
 * Created by WangXin on 2016/3/10 0010.
 */
public class UserCenterActivity extends BaseActivity {
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_sex)
    TextView userSex;
    @Bind(R.id.user_address)
    TextView userAddress;
    @Bind(R.id.user_avatar)
    CircleImageView userAvatar;

    private Intent it;
    private SharedPreferences myPreferences;
    private String defaultPassword="";
    private ArrayList<Province> provinces = new ArrayList<Province>();
    private static final int UPDATE_NICK_NAME = 0;
    private static final int UPDATE_PASSWORD = 1;
    private static final int UPDATE_SEX = 2;
    private String updateName="";
    private String updateSex="";
    private String updatePassword="";
    private User user;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_center;
    }

    @Override
    public void initView() {
        setTitle("个人中心");
    }

    @Override
    public void initData() {
        myPreferences = getSharedPreferences("login", LoginActivity.MODE_PRIVATE);
        try {
            userName.setText(MyApplication.getCurrentUser().getNickName());
            userSex.setText(MyApplication.getCurrentUser().getSex());
            userAddress.setText(MyApplication.getCurrentUser().getAddress());
            ImageLoader.getInstance().displayImage(ConstantValue.USER_AVATAR_URL+MyApplication.currentUser.getUserAvatar(),userAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @OnClick({R.id.iv_title_back,R.id.name_layout,R.id.sex_layout, R.id.update_phone_layout,
                R.id.update_psd_layout,R.id.register_layout,R.id.connect_service,R.id.exit_btn})
    public void onClick(final View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_title_back:
                onBackPressed();
                break;
            case R.id.name_layout:
                // 取得自定义View
                LayoutInflater layoutInflaterInfo = LayoutInflater.from(this);
                final View myInfoView = layoutInflaterInfo.inflate(R.layout.dialog_user_name, null);
                new AlertDialog.Builder(this).
                        setTitle("修改个人信息").
                        setView(myInfoView).
                        setPositiveButton("修改", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                EditText NewName = (EditText) myInfoView.findViewById(R.id.new_name);
                                updateName = NewName.getText()
                                        .toString().trim();
                                user = new User();
                                user.setNickName(updateName);
                                user.setId(MyApplication.getCurrentUser().getId());
                                updateUserInfo(user,UPDATE_NICK_NAME);
                                //收起键盘
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).
                        create().show();
                break;
            case R.id.sex_layout:
                // 取得自定义View
                LayoutInflater layoutInflaterInfo1 = LayoutInflater.from(this);
                final View mySexView = layoutInflaterInfo1.inflate(R.layout.dialog_user_sex, null);
                final RadioButton mRadio1 = (RadioButton) mySexView.findViewById(R.id.radioMale);
                final RadioButton mRadio2 = (RadioButton) mySexView.findViewById(R.id.radioFemale);
                new AlertDialog.Builder(this).
                        setTitle("修改个人信息").
                        setView(mySexView).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mRadio1.isChecked()){
                                    updateSex = mRadio1.getText().toString();
                                }else if (mRadio2.isChecked()){
                                    updateSex = mRadio2.getText().toString();
                                }
                                user = new User();
                                user.setSex(updateSex);
                                user.setId(MyApplication.getCurrentUser().getId());
                                updateUserInfo(user,UPDATE_SEX);
                            }
                        }).create().show();
                break;
            case R.id.update_phone_layout:
                // 取得自定义View
                LayoutInflater layoutInflaterPhone = LayoutInflater.from(this);
                final View myPhoneView = layoutInflaterPhone.inflate(R.layout.dialog_change_phone, null);
                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("修改绑定手机").
                        setView(myPhoneView).
                        setPositiveButton("修改", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                //获取输入的密码
                                EditText Password = (EditText) myPhoneView.findViewById(R.id.change_phone_password);
                                final String passWord = Password.getText()
                                        .toString().trim();
                                defaultPassword = MyApplication.getCurrentUser().getPassword();
                                //密码对比
                                if (defaultPassword.equals(MD5.getMd5(passWord))) {

                                    Intent it=new Intent(UserCenterActivity.this,VerifyTelActivity.class);
                                    it.putExtra("identification","change");
                                    startActivity(it);
                                } else {
                                    Toast.makeText(UserCenterActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                                    //收起键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                                    dialog.dismiss();
                                }

                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        create();
                alertDialog.show();
                break;
            case R.id.update_psd_layout:
                // 取得自定义View
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View myLoginView = layoutInflater.inflate(R.layout.dialog_change_password, null);
                final EditText OldPassword = (EditText) myLoginView.findViewById(R.id.old_password);
                OldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b){
                            defaultPassword = MyApplication.getCurrentUser().getPassword();
                            if (!defaultPassword.equals(MD5.getMd5(OldPassword.getText().toString()))){
                                Toast.makeText(UserCenterActivity.this,"密码错误,请重新输入",Toast.LENGTH_SHORT).show();
                                OldPassword.setText("");
                            }
                        }
                    }
                });
                new AlertDialog.Builder(this).
                        setTitle("修改密码").
                        setView(myLoginView).
                        setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText NewPassword = (EditText) myLoginView.findViewById(R.id.new_password);
                                EditText NewPassword2 = (EditText) myLoginView.findViewById(R.id.confirm_password);
                                final String PassWordOld = OldPassword.getText()
                                        .toString().trim();
                                final String PassWord = NewPassword.getText()
                                        .toString().trim();
                                final String PassWord2 = NewPassword2.getText()
                                        .toString().trim();
                                //密码对比
                                if ("".equals(PassWordOld)){
                                    Toast.makeText(UserCenterActivity.this, "请输入原密码", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if ("".equals(PassWord)||"".equals(PassWord2)){
                                    Toast.makeText(UserCenterActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!PassWord.equals(PassWord2)){
                                    Toast.makeText(UserCenterActivity.this, "两次密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                                    NewPassword.setText("");
                                    NewPassword2.setText("");
                                    return;
                                }
                                updatePassword = MD5.getMd5(PassWord);
                                user = new User();
                                user.setId(MyApplication.getCurrentUser().getId());
                                user.setPassword(updatePassword);
                                updateUserInfo(user,UPDATE_PASSWORD);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case R.id.register_layout:
                Intent it2=new Intent(this,VerifyTelActivity.class);
                it2.putExtra("identification","register");
                startActivity(it2);
                break;
            case R.id.connect_service:
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "15981157552"));
                startActivity(intent);
                break;
            case R.id.exit_btn:
                clearLoginInfo();
                AppManager.getAppManager().finishAllActivity();
                it = new Intent(this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
        }
    }

    private void clearLoginInfo(){
        SharedPreferences.Editor editor =myPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void updateUserInfo(User user, final int flag){
        showWaitDialog();
        Map params = new HashMap();
        if (user.getNickName()!=null){
            params.put("nickname",user.getNickName());
        }
        if (user.getSex()!=null){
            params.put("sex",user.getSex());
        }
        if (user.getPassword()!=null){
            params.put("password",user.getPassword());
        }
        params.put("id",user.getId());
        String url="";
        switch (flag){
            case UPDATE_NICK_NAME:
                url = ConstantValue.UPDATE_NICK_NAME;
                break;
            case UPDATE_PASSWORD:
                url = ConstantValue.UPDATE_PASSWORD;
                break;
            case UPDATE_SEX:
                url = ConstantValue.UPDATE_SEX;
                break;
        }
        if ("".equals(url)){
            return;
        }
        JsonRequestWithAuth<AjaxResponse> request = new JsonRequestWithAuth<AjaxResponse>(url, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                hideWaitDialog();
                Toast.makeText(UserCenterActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                if (response.isOk()){
                    if (flag==UPDATE_NICK_NAME){
                        userName.setText(updateName);
                        MyApplication.getCurrentUser().setNickName(updateName);
                    }else if (flag == UPDATE_PASSWORD){
                        MyApplication.getCurrentUser().setPassword(updatePassword);
                        SharedPreferences.Editor editor =myPreferences.edit();
                        editor.putString("password",updatePassword);
                        editor.commit();
                    }else if (flag == UPDATE_SEX){
                        userSex.setText(updateSex);
                        MyApplication.getCurrentUser().setSex(updateSex);
                    }
                }else {
                    Toast.makeText(UserCenterActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                }
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(request);
        MyApplication.getHttpQueues().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("request");
    }
}
