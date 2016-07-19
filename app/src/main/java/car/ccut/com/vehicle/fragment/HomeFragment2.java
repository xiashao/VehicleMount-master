package car.ccut.com.vehicle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.ui.OrderManageActivity;
import car.ccut.com.vehicle.ui.UserCenterActivity;

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
public class HomeFragment2 extends Fragment implements View.OnClickListener {

    @Bind(R.id.tv_title)
    TextView title;
    @Bind(R.id.iv_title_back)
    ImageView back;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment2,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    @OnClick({R.id.user_center,R.id.set,R.id.order_manage})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.user_center:
                Intent i=new Intent(getActivity(), UserCenterActivity.class);
                startActivity(i);
                break;
            case R.id.set:
                break;
            case R.id.order_manage:
                Intent intent = new Intent(getActivity(), OrderManageActivity.class);
                intent.putExtra("orderType", ConstantValue.ORDER_FINISH);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void initView(){
        title.setText("信息管理");
        back.setVisibility(View.GONE);
    }
}
