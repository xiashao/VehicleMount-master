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
 * Created by WangXin on 2016/4/20 0020.
 */
public class CarInfo implements Serializable {
    private String id;
    //汽车品牌
    private String carBrand;
    //汽车简照
    private String carPhoto;
    //车牌号码
    private String carNumber;
    //汽车型号
    private String carType;
    //发动机号
    private String carMachineNo;
    //车身级别
    private String bodyLevel;
    //里程数
    private String expendNumber;
    //汽车加油类型
    private List<CheckRefuelType> refuelType=new ArrayList<>();
    //燃油量
    private String fuelAmount;
    //发动机性能
    private int enginePerformance;
    //变速器性能
    private int transmissionPerformance;
    //车灯状况
    private int lampStatus;
    //用户id
    private String userId;

    public CarInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carName) {
        this.carBrand = carName;
    }

    public String getCarPhoto() {
        return carPhoto;
    }

    public void setCarPhoto(String carPhoto) {
        this.carPhoto = carPhoto;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getBodyLevel() {
        return bodyLevel;
    }

    public void setBodyLevel(String bodyLevel) {
        this.bodyLevel = bodyLevel;
    }

    public String getCarMachineNo() {
        return carMachineNo;
    }

    public void setCarMachineNo(String carMachineNo) {
        this.carMachineNo = carMachineNo;
    }

    public String getExpendNumber() {
        return expendNumber;
    }

    public void setExpendNumber(String expendNumber) {
        this.expendNumber = expendNumber;
    }

    public int getEnginePerformance() {
        return enginePerformance;
    }

    public void setEnginePerformance(int enginePerformance) {
        this.enginePerformance = enginePerformance;
    }

    public int getTransmissionPerformance() {
        return transmissionPerformance;
    }

    public void setTransmissionPerformance(int transmissionPerformance) {
        this.transmissionPerformance = transmissionPerformance;
    }

    public int getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(int lampStatus) {
        this.lampStatus = lampStatus;
    }

    public String getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(String fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public List<CheckRefuelType> getRefuelType() {
        return refuelType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
