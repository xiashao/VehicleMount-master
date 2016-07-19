package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.bean.CheckRefuelType;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.CharacterParser;
import car.ccut.com.vehicle.util.PinyinComparatorRefuel;
import car.ccut.com.vehicle.view.SmoothCheckBox;

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
 * Created by WangXin on 2016/4/14 0014.
 */
public class SelectRefuelTypeActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.listView)
    ListView mListView;
    private List<CheckRefuelType> mList = new ArrayList<CheckRefuelType>();
    public DelayedProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_refuel_type);
        ButterKnife.bind(this);
        dialog = DelayedProgressDialog.make(this,null,"数据加载中,请稍后..",true,true);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void initView() {
        Collections.sort(toUpperCase(mList), new PinyinComparatorRefuel());
        mListView.setAdapter(new QuickAdapter<CheckRefuelType>(this,R.layout.selecter_refuel_type_item,mList) {
            @Override
            protected void convert(BaseAdapterHelper helper, final CheckRefuelType item) {
                int section = getSectionForPosition(helper.getPosition());
                if (helper.getPosition() == getPositionForSection(section)){
                    helper.setVisible(R.id.catalog, true);
                    helper.setText(R.id.catalog, item.getType());
                }else {
                    helper.setVisible(R.id.catalog, false);
                }
                helper.setText(R.id.type,item.getName());
                final SmoothCheckBox checkBo = helper.getView(R.id.checkbox);
                checkBo.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        item.setChecked(isChecked);
                    }
                });
                helper.setOnClickListener(R.id.type_linearLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.setChecked(!item.getChecked());
                        checkBo.setChecked(item.getChecked(),true);
                    }
                });
            }
        });
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getFirstLetter().charAt(0);
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < mList.size(); i++) {
            String sortStr = mList.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private List<CheckRefuelType> toUpperCase(List<CheckRefuelType> refuelTypes){
        for (int i =0;i<refuelTypes.size();i++){
            String pinyin = null;
            String sortString = null;
            if (refuelTypes.get(i).getType()!=null){
                pinyin = CharacterParser.getInstance().getSelling(refuelTypes.get(i).getType());
            }
            if (pinyin!=null){
                sortString = pinyin.substring(0, 1).toUpperCase();
            }
            if(sortString.matches("[A-Z]")){
                refuelTypes.get(i).setFirstLetter(sortString.toUpperCase());
            }else{
                refuelTypes.get(i).setFirstLetter("#");
            }
        }
        return refuelTypes;
    }

    public void initData() {
        dialog.show();
        Map params = new HashMap<>();
        JsonRequestWithAuth<AjaxResponse> requestRefuelType = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.REQUEST_REFUEL_TYPE, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        if (response.isOk()){
                            Gson gson = new Gson();
                            mList = gson.fromJson(gson.toJson(response.getResponseData().get("refuelTypeList")), new TypeToken<List<CheckRefuelType>>(){}.getType());
                            initView();
                            dialog.dismiss();
                        }else {
                            Toast.makeText(SelectRefuelTypeActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MyApplication.getHttpQueues().add(requestRefuelType);
        MyApplication.getHttpQueues().start();
    }

    @Override
    @OnClick({R.id.submit})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.submit:
                intent();
                break;
        }
    }

    private void intent(){
        List<CheckRefuelType> result = new ArrayList<CheckRefuelType>();
        for (CheckRefuelType refuelType:mList){
            if (refuelType.getChecked()){
                result.add(refuelType);
            }
        }
        Intent intent = new Intent();
        intent.putExtra("fuelTypeList", (Serializable) result);
        setResult(RESULT_OK,intent);
        finish();
    }
}
