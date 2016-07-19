package car.ccut.com.vehicle.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lenovo on 2016/5/27.
 */
public class ResetPasswordActivityTest extends InstrumentationTestCase{
    ResetPasswordActivity reset;
    EditText password;
    EditText confirmPassword;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", ResetPasswordActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        reset = (ResetPasswordActivity) instrumentation.startActivitySync(intent);
        assertNotNull(reset);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void testOnClick() throws Exception {
        password=reset.password;
        confirmPassword=reset.confirmPassword;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                password.setText("74521");
                confirmPassword.setText("74521");
            }
        });
        SystemClock.sleep(1000);
        String passwordTest = password.getText().toString();
        String confirmpasswordTest=confirmPassword.getText().toString();
        assertEquals("74521",passwordTest);
        assertEquals("74521",confirmpasswordTest);

    }
}