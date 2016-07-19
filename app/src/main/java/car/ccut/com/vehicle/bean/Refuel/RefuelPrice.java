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
 * Created by WangXin on 2016/4/9 0009.
 */
public class RefuelPrice implements Serializable {
    private String refuelType;
    private String localPrice;
    private String gastprice;

    public RefuelPrice() {
    }

    public RefuelPrice(String refuelType) {
        this.refuelType = refuelType;
    }

    public String getRefuelType() {
        return refuelType;
    }

    public void setRefuelType(String refuelType) {
        this.refuelType = refuelType;
    }

    public String getLocalPrice() {
        return localPrice;
    }

    public void setLocalPrice(String localPrice) {
        this.localPrice = localPrice;
    }

    public String getGastprice() {
        return gastprice;
    }

    public void setGastprice(String gastprice) {
        this.gastprice = gastprice;
    }
}
