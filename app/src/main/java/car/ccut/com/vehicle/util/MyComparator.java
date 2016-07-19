package car.ccut.com.vehicle.util;

import java.util.Comparator;

import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;

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
 * Created by WangXin on 2016/3/31 0031.
 */
public class MyComparator implements Comparator {

    private int flag;
    private String refuelType;

    public MyComparator(int flag) {
        this.flag = flag;
    }

    public MyComparator(int flag,String refuelType) {
        this.refuelType = refuelType;
        this.flag = flag;
    }

    @Override
    public int compare(Object o, Object t1) {
        RefuelStationInfo stationInfo1 = (RefuelStationInfo) o;
        RefuelStationInfo stationInfo2 = (RefuelStationInfo) t1;
        switch (flag){
            case 0:
                return stationInfo1.getDiscount().compareTo(stationInfo2.getDiscount());
            case 1:
                return stationInfo1.getDistance()-stationInfo2.getDistance();
            case 2:
                return stationInfo1.getPrice().get(refuelType).compareTo(stationInfo2.getPrice().get(refuelType));
        }
        return 0;
    }
}
