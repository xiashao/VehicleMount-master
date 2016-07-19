package car.ccut.com.vehicle.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.base.MultipartRequest;
import car.ccut.com.vehicle.bean.User;
import car.ccut.com.vehicle.bean.address.City;
import car.ccut.com.vehicle.bean.address.County;
import car.ccut.com.vehicle.bean.address.Province;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.photopick.PhotoPickerActivity;
import car.ccut.com.vehicle.util.MD5;
import car.ccut.com.vehicle.view.CityPickerDialog;
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
 * Created by WangXin on 2016/4/8 0008.
 */
public class RegisterBaseInfoActivity extends BaseActivity{
    @Bind(R.id.register_get_name)
    EditText nickName;
    @Bind(R.id.radioGroup)
    RadioGroup registerSex;
    @Bind(R.id.register_address)
    TextView registerAddress;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.confirm_password)
    EditText confirmPassword;
    @Bind(R.id.iv_title_back)
    ImageView back;
    @Bind(R.id.user_photo)
    CircleImageView userPhoto;
    @Bind(R.id.radioMale)
    RadioButton male;
    @Bind(R.id.radioFemale)
    RadioButton female;

    private ArrayList<Province> provinces = new ArrayList<Province>();
    private static final int REQUEST_PHOTO = 9002;

    private User userInfo=new User();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_base_info;
    }

    @Override
    public void initView() {
        setTitle("注册");
        setBackIcon();
        registerSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == male.getId()){
                    userInfo.setSex(male.getText().toString());
                }else if (i==female.getId()){
                    userInfo.setSex(female.getText().toString());
                }
            }
        });
    }

    @Override
    public void initData() {
        userInfo.setUsername(getIntent().getStringExtra("phone"));
    }


    @Override
    @OnClick({R.id.iv_title_back,R.id.user_photo,R.id.select_address,R.id.submit})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                break;
            case R.id.user_photo:
                selectPhoto();
                break;
            case R.id.select_address:
                if (provinces.size() > 0) {
                    showAddressDialog();
                } else {
                    new InitAreaTask(this).execute(0);
                }
                break;
            case R.id.submit:
                register();
                break;
        }
    }

    private void register() {
        if (check()) {
            Map<String, String> params = new HashMap<>();
            params.put("username", userInfo.getUsername());
            params.put("password", userInfo.getPassword());
            params.put("nickname",userInfo.getNickName());
            params.put("sex",userInfo.getSex());
            params.put("address",userInfo.getAddress());
            MultipartRequest registerRequest = new MultipartRequest(Request.Method.POST, ConstantValue.REQUEST_REGISTER,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }, new Response.Listener<AjaxResponse>() {
                @Override
                public void onResponse(AjaxResponse response) {
                    if (response.isOk()){
                        Intent intent = new Intent(RegisterBaseInfoActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(RegisterBaseInfoActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                    }
                }
            },"file",new File(userInfo.getUserAvatar()),params);
            MyApplication.getHttpQueues().add(registerRequest);
            MyApplication.getHttpQueues().start();
        }


    }

    public boolean check() {
        userInfo.setNickName(nickName.getText().toString());
        if (TextUtils.isEmpty(userInfo.getNickName())) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userInfo.getAddress() == null || userInfo.getAddress().equals("")) {
            Toast.makeText(this, "请选择地区", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "两次输入密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
            password.setText("");
            confirmPassword.setText("");
            return false;
        }else {
            userInfo.setPassword(MD5.getMd5(confirmPassword.getText().toString()));
        }
        if (userInfo.getUserAvatar() == null||"".equals(userInfo.getUserAvatar())) {
            Toast.makeText(this, "请上传头像", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getHttpQueues().cancelAll("registerRequest");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_PHOTO:
                if (data!=null){
                    List<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                    userInfo.setUserAvatar(photos.get(0));
                    ImageLoader.getInstance().displayImage("file://"+userInfo.getUserAvatar(),userPhoto);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectPhoto(){
        Intent intent = new Intent(RegisterBaseInfoActivity.this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
        startActivityForResult(intent, REQUEST_PHOTO);
    }

    private void showAddressDialog() {
        new CityPickerDialog(this, provinces, null, null, null,
                new CityPickerDialog.onCityPickedListener() {

                    @Override
                    public void onPicked(Province selectProvince,
                                         City selectCity, County selectCounty) {
                        StringBuilder address = new StringBuilder();
                        address.append(
                                selectProvince != null ? selectProvince
                                        .getAreaName() : "")
                                .append(selectCity != null ? selectCity
                                        .getAreaName() : "")
                                .append(selectCounty != null ? selectCounty
                                        .getAreaName() : "");
                        String text = selectCounty != null ? selectCounty
                                .getAreaName() : "";
                        registerAddress.setText(address);
                        userInfo.setAddress(address.toString());
                    }
                }).show();
    }


    private class InitAreaTask extends AsyncTask<Integer,Integer,Boolean>{
        private Context context;
        private DelayedProgressDialog dialog;

        public InitAreaTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = DelayedProgressDialog.showDelayed(RegisterBaseInfoActivity.this,null,"数据加载中,请稍后...",true,true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if (provinces.size()>0){
                showAddressDialog();
            }else {
                Toast.makeText(context,"数据初始化失败",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            String address = null;
            InputStream in = null;
            try {
                in = context.getResources().getAssets().open("address.txt");
                byte[] arrayOfByte = new byte[in.available()];
                in.read(arrayOfByte);
                address = EncodingUtils.getString(arrayOfByte, "UTF-8");
                JSONArray jsonList = new JSONArray(address);
                Gson gson = new Gson();
                for (int i = 0; i < jsonList.length(); i++) {
                    try {
                        provinces.add(gson.fromJson(jsonList.getString(i),
                                Province.class));
                    } catch (Exception e) {
                    }
                }
                return true;
            } catch (Exception e) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
            return false;
        }
    }
}
