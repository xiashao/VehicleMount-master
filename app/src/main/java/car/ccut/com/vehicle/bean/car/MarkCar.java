package car.ccut.com.vehicle.bean.car;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 * Created by WangXin on 2016/6/11 0011.
 */
public class MarkCar implements Serializable {
    private Car carBrand = new Car();
    private CarBodyLevel carBodyLevel = new CarBodyLevel();
    private String carNumber;
    private String carMachineNo;
    private String expendNumber;
    private List<CheckRefuelType> checkRefuelTypes = new ArrayList<CheckRefuelType>();

    public Car getCarBrand() {
        return carBrand;
    }

    public CarBodyLevel getCarBodyLevel() {
        return carBodyLevel;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getCarMachineNo() {
        return carMachineNo;
    }

    public String getExpendNumber() {
        return expendNumber;
    }

    public List<CheckRefuelType> getCheckRefuelTypes() {
        return checkRefuelTypes;
    }
}
