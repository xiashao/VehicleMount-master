package car.ccut.com.vehicle.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lenovo on 2016/5/27.
 */
public class RegisterBaseInfoActivityTest extends InstrumentationTestCase{
    RegisterBaseInfoActivity registerBaseInfo;
    EditText nickName;
    EditText password;
    EditText confirmPassword;
    RadioGroup registerSex;
    RadioButton male;
    RadioButton female;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", RegisterBaseInfoActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        registerBaseInfo = (RegisterBaseInfoActivity) instrumentation.startActivitySync(intent);
        assertNotNull(registerBaseInfo);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void testOnClick() throws Exception {
        nickName=registerBaseInfo.nickName;
        password = registerBaseInfo.password;
        confirmPassword=registerBaseInfo.confirmPassword;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                nickName.setText("bobo");
                password.setText("74521");
                confirmPassword.setText("74521");
            }
        });
        SystemClock.sleep(1000);
        String nickNameTest = nickName.getText().toString();
        String passwordTest=password.getText().toString();
        String confirmPasswordTest=confirmPassword.getText().toString();
        assertEquals("bobo",nickNameTest);
        assertEquals("74521",passwordTest);
        assertEquals("74521", confirmPasswordTest);
        assertEquals("男",registerBaseInfo.male.getText().toString());
        assertEquals("女",registerBaseInfo.female.getText().toString());
    }

    @Test
    public void testOnActivityResult() throws Exception {

    }
}