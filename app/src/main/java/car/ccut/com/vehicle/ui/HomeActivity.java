package car.ccut.com.vehicle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.fragment.HomeFragment1;
import car.ccut.com.vehicle.fragment.HomeFragment2;
import car.ccut.com.vehicle.view.DragLayout;

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
 * Created by WangXin on 2016/5/11 0011.
 */
public class HomeActivity extends FragmentActivity {
    @Bind(R.id.drag_layout)
    DragLayout dragLayout;

    private HomeFragment1 fragment1;
    private HomeFragment2 fragment2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.first, fragment1).add(R.id.second, fragment2)
                .commit();
        DragLayout.ShowNextPageNotifier nextPageNotifier = new DragLayout.ShowNextPageNotifier() {
            @Override
            public void onDragNext() {
                fragment2.initView();
            }
        };
        dragLayout.setNextPageListener(nextPageNotifier);

    }

    private void initData() {
        fragment1 = new HomeFragment1();
        fragment2 = new HomeFragment2();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                Intent intent = new Intent();
//                intent.setClass(HomeActivity.this, VoiceRecognition.class);
//                stopService(intent);
//                System.exit(0);
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
