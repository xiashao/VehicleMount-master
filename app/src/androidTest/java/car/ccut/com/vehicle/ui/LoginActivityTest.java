package car.ccut.com.vehicle.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.widget.EditText;

/**
 * Created by lenovo on 2016/5/28.
 */
public class LoginActivityTest extends InstrumentationTestCase {
    LoginActivity loginActivity;
    EditText userName;
    EditText password;
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", LoginActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        loginActivity = (LoginActivity) instrumentation.startActivitySync(intent);
        assertNotNull(loginActivity);

    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOnClick() throws Exception {
        password=loginActivity.password;
        userName = loginActivity.userName;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                userName.setText("15981157552");
                password.setText("74521");
            }
        });
        SystemClock.sleep(1000);
        String userNameTest = userName.getText().toString();
        String passwordTest=password.getText().toString();
        assertEquals("15981157552",userNameTest);
        assertEquals("74521",passwordTest);
    }

}