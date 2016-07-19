package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.PoiInfos;

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
 * Created by WangXin on 2016/5/7 0007.
 */
public class SearchInfosActivity extends BaseActivity {
    @Bind(R.id.list_view)
    ListView listView;
    private List<PoiInfos> poiInfosList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_infos;
    }

    @Override
    public void initView() {
        setTitle("检索结果");
        setBackIcon();
        if (poiInfosList!=null&&poiInfosList.size()>0){
            listView.setAdapter(new QuickAdapter<PoiInfos>(this,R.layout.search_facilites_item,poiInfosList) {
                @Override
                protected void convert(BaseAdapterHelper helper, final PoiInfos item) {
                    helper.setText(R.id.name,item.getName());
                    helper.setText(R.id.address,item.getAddress());
                    helper.setText(R.id.tel,item.getPhoneNum());
                    helper.setOnClickListener(R.id.navi, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent it = new Intent(SearchInfosActivity.this,NaviRouterActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("finishNode",item);
                            it.putExtras(bundle);
                            startActivity(it);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void initData() {
        poiInfosList = (List<PoiInfos>) getIntent().getSerializableExtra("poiInfos");
    }

    @Override
    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_title_back:
                onBackPressed();
                break;
        }
    }
}
