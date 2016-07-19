package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseActivity;

/**
 * Created by lenovo on 2016/5/12.
 */
public class ResetPasswordActivity extends BaseActivity{
    @Bind(R.id.reset_password)
    EditText password;
    @Bind(R.id.reset_confirm_password)
    EditText confirmPassword;
    @Bind(R.id.reset_verify_button)
    Button resetVerify;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    public void initView() {
        setTitle("设置密码");
    }

    @Override
    public void initData() {

    }

    @Override
    @OnClick({ R.id.reset_verify_button})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.reset_verify_button:
                Intent intent = getIntent();
                String phString = intent.getStringExtra("phString");
                final String passwordText = password.getText().toString();
                final String passwordConfirmText = confirmPassword.getText().toString();
                if (passwordText.equals("") || passwordConfirmText.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else if (passwordText.equals(passwordConfirmText)) {
                    Toast.makeText(ResetPasswordActivity.this, "设置成功", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    i.putExtra("phone", phString);
                    startActivity(i);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "两次输入不一致", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
