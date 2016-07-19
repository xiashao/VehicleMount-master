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
public class AddCarActivityTest extends InstrumentationTestCase{
    AddCarActivity addCar;
    EditText carNum;
    EditText engineNum;
    EditText mileage;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", AddCarActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        addCar = (AddCarActivity) instrumentation.startActivitySync(intent);
        assertNotNull(addCar);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void testOnClick() throws Exception {
        carNum=addCar.carNum;
        engineNum = addCar.engineNum;
        mileage=addCar.mileage;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                carNum.setText("吉A66666");
                engineNum.setText("1234567891111");
                mileage.setText("10000");
            }
        });
        SystemClock.sleep(1000);
        String carNameTest = carNum.getText().toString();
        String engineNumTest=engineNum.getText().toString();
        String mileageTest=mileage.getText().toString();
        assertEquals("吉A66666",carNameTest);
        assertEquals("1234567891111",engineNumTest);
        assertEquals("10000",mileageTest);
    }
}