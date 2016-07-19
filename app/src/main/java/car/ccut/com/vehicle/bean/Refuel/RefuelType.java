package car.ccut.com.vehicle.bean.Refuel;

import java.io.Serializable;

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
 * Created by WangXin on 2016/4/2 0002.
 */
public class RefuelType implements Serializable {
    //加油类型
    private String refuelType;
    //油价
    private float price;

    public RefuelType() {
    }

    public RefuelType(String refuelType, float price) {
        this.refuelType = refuelType;
        this.price = price;
    }

    public String getRefuelType() {
        return refuelType;
    }

    public void setRefuelType(String refuelType) {
        this.refuelType = refuelType;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
