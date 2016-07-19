package car.ccut.com.vehicle.bean.Refuel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * Created by WangXin on 2016/3/22 0022.
 */
public class RefuelStationInfo implements Serializable {
    private String id;
    //加油站名称
    private String name;
    //城市邮编
    private String area;
    //城市区域
    private String areaname;
    //加油站地址
    private String address;
    //运营商类型
    private String brandname;
    //加油站类型
    private String type;
    //是否打折加油站
    private String discount;
    //尾气排放标准
    private String exhaust;
    //谷歌地图坐标
    private String position;
    //百度地图纬度
    private String lat;
    //百度地图经度
    private String lon;
    //省控基准油价
    private HashMap<String,Float> price = new HashMap<String,Float>();
    //加油站油价
    private HashMap<String,Float> gastprice = new HashMap<String,Float>();
    //加油卡信息
    private String fwlsmc;
    //与坐标的距离
    private int distance;
    private List<RefuelType>personPrice= new ArrayList<RefuelType>();
    private List<RefuelType>standradPrice = new ArrayList<RefuelType>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getExhaust() {
        return exhaust;
    }

    public void setExhaust(String exhaust) {
        this.exhaust = exhaust;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public HashMap<String, Float> getPrice() {
        return price;
    }

    public HashMap<String, Float> getGastprice() {
        return gastprice;
    }

    public String getFwlsmc() {
        return fwlsmc;
    }

    public void setFwlsmc(String fwlsmc) {
        this.fwlsmc = fwlsmc;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<RefuelType> getPersonPrice() {
        return personPrice;
    }

    public List<RefuelType> getStandradPrice() {
        return standradPrice;
    }
}
