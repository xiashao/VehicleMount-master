package car.ccut.com.vehicle.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import butterknife.Bind;
import car.ccut.com.vehicle.R;

/**
 * Created by lenovo on 2016/5/30.
 */
public class IndentWriteActivityTest extends InstrumentationTestCase {
    IndentWriteActivity intentTest;
    EditText moeny;
    EditText fuelCount;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", IndentWriteActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        intentTest = (IndentWriteActivity) instrumentation.startActivitySync(intent);
        assertNotNull(intentTest);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetLayoutId() throws Exception {

    }

    @Test
    public void testInitView() throws Exception {

    }

    @Test
    public void testInitData() throws Exception {

    }

    @Test
    public void testOnClick() throws Exception {
        moeny = intentTest.moeny;
        fuelCount=intentTest.fuelCount;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                moeny.setText("200");
                fuelCount.setText("10");
            }
        });
        SystemClock.sleep(1000);
        String moenyNumTest=moeny.getText().toString();
        String fuelCountTest=fuelCount.getText().toString();
        assertEquals("200",moenyNumTest);
        assertEquals("10",fuelCountTest);
    }

    @Test
    public void testOnDismiss() throws Exception {

    }

    @Test
    public void testOnOtherButtonClick() throws Exception {

    }

    @Test
    public void testShowDate() throws Exception {

    }

    @Test
    public void testCreateOrderId() throws Exception {

    }

    @Test
    public void testOnBackPressed() throws Exception {

    }
}