package car.ccut.com.vehicle.ui;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lenovo on 2016/5/30.
 */
public class NearbyFacilitesActivityTest extends InstrumentationTestCase{
    NearbyFacilitesActivity near;
    AutoCompleteTextView search;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("car.ccut.com.vehicle", NearbyFacilitesActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Instrumentation instrumentation = getInstrumentation();
        near = (NearbyFacilitesActivity) instrumentation.startActivitySync(intent);
        assertNotNull(near);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testOnClick() throws Exception {
        search=near.search;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                search.setText("ccut");
            }
        });
        SystemClock.sleep(1000);
        String searchTest = search.getText().toString();
        assertEquals("ccut",searchTest);
    }

}