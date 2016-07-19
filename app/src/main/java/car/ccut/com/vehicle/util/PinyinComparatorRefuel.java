package car.ccut.com.vehicle.util;

import java.util.Comparator;

import car.ccut.com.vehicle.bean.CheckRefuelType;

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
 * Created by WangXin on 2016/5/26 0026.
 */
public class PinyinComparatorRefuel implements Comparator<CheckRefuelType> {
    @Override
    public int compare(CheckRefuelType t1, CheckRefuelType t2) {
        if (t1.getFirstLetter().equals("@") || t2.getFirstLetter().equals("#")) {
            return -1;
        } else if (t1.getFirstLetter().equals("#")
                || t2.getFirstLetter().equals("@")) {
            return 1;
        } else {
            return t1.getFirstLetter().compareTo(t2.getFirstLetter());
        }
    }
}
