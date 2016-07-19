package car.ccut.com.vehicle.interf;

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
 * Created by WangXin on 2016/2/18 0018.
 */
public class ConstantValue {
    //加油站信息获取key
    public static final String REFUEL_KEY = "5a193c21af3fe1601c9ee7800a65805a";
    //请求接口地址
    public static final String REDUEL_STATION_URL ="http://apis.juhe.cn/oil/local";
    //短信验证key
    public static final String APPKEY ="11827926529ce";
    public static final String APPSECRETE ="d5ada9155892bc5fcdba6878dcce775f";
    //天气key
    public static final String WEATHER_KEY = "fkajoevasb7svcfz";
    //天气Id
    public static final String WEATHER_ID = "UFBF24A191";
    //违章查询
    public static final int VIOLATION_ID = 1668;
    public static final String VIOLATION_KEY = "f65f9bd94f35241df921a4e2af913fbb";
    public static final int ORDER_UNFINISHED = 0;
    public static final int ORDER_FINISH = -1;
    public static final int NORMAL = 0;
    public static final int ABNORMAL = 1;

    //服务器IP地址
//    public static final String SERVER_IP="192.168.191.1";
    public static final String SERVER_IP="58.221.44.101";
    public static final String BASE_URL = "http://"+SERVER_IP+":8088/app/";
    public static final String VOICE_RECOGNITION = "appid=5729a06d";
    public static final String IMAGE_BASE_URL = BASE_URL+"photo/";
    public static final String USER_AVATAR_URL = IMAGE_BASE_URL+"avatar/";
    public static final String CAR_PHOTO_URL = IMAGE_BASE_URL+"car/";
    public static final String ORDER_MARK_URL = IMAGE_BASE_URL+"order/";
    public static final String CAR_RECORD = BASE_URL+"carRecord/";
    //请求汽车品牌地址
    public static final String  REQUEST_CAR_NAME = BASE_URL+"brand/getBrandList";
    //请求汽车类型
    public static final String  REQUEST_CAR_TYPE = BASE_URL+"carType/getCarTypeList";
    //请求燃油类型
    public static final String REQUEST_REFUEL_TYPE = BASE_URL+"refuelType/getAllRefuelType";
    //添加车辆信息
    public static final String REQUEST_INSRT_CAR = BASE_URL+"carInfo/addCarInfo";
    //请求登陆
    public static final String REQUEST_LOGIN = BASE_URL+"login";
    //请求注册
    public static final String REQUEST_REGISTER = BASE_URL+"register";
    //校验手机号是否被注册
    public static final String CHECK_PHONE_NUMBER = BASE_URL+"getUserByUserName";
    //保存订单
    public static final String REQUEST_SAVE_ORDER = BASE_URL+"order/addOrder";
    //请求订单
    public static final String REQUEST_ORDER = BASE_URL+"order/getOrderListPage";
    //更改订单状态
    public static final String UPDATE_ORDER_STATUS = BASE_URL+"/order/updateOrderStatus";
    //请求车身级别
    public static final String REQUEST_ALL_CAR_BODY_LEVEL = BASE_URL+"bodyLevel/getAllCarBodyLevel";
    //请求所有车辆基本信息
    public static final String REQUEST_ALL_CAR_BASE_INFO = BASE_URL +"carInfo/getCarListByUserId";
    //删除车辆
    public static final String DELETE_CAR = BASE_URL+"carInfo/deleteCar";
    //请求车辆信息
    public static final String DETAILS_CAR_INFO = BASE_URL+"carInfo/getDetailCarInfo";
    //修改呢称
    public static final String UPDATE_NICK_NAME = BASE_URL+"updateNickName";
    //修改密码
    public static final String UPDATE_PASSWORD = BASE_URL+"updatePassword";
    //修改性别
    public static final String UPDATE_SEX = BASE_URL+"updateSex";
    //修改车灯状况
    public static final String UPDATE_LAMP_STATUS = CAR_RECORD+"update/lampStatus";
    //修改引擎状况
    public static final String UPDATE_ENGINE_PERFORMANCE=CAR_RECORD+"update/enginePerformance";
    //修改变速箱状况
    public static final String UPDATE_TRANSMISSION_PERFORMANCE = CAR_RECORD+"update/transmissionPerformance";
    //修改里程数
    public static final String UPDATE_EXPEND_NUMBER = CAR_RECORD+"update/expendNumber";
    //修改燃油量
    public static final String UPDATE_FUEL_AMOUNT=CAR_RECORD+"update/fuelAmount";
    //导航线路偏好
    //推荐
    public static final int ROUTE_PLAN_MOD_RECOMMEND = 1;
    //高速优先
    public static final int ROUTE_PLAN_MOD_MIN_TIME = 2;
    //少走高速
    public static final int ROUTE_PLAN_MOD_MIN_DIST = 4;
    //少收费
    public static final int ROUTE_PLAN_MOD_MIN_TOLL = 8;
    //躲避拥堵
    public static final int ROUTE_PLAN_MOD_AVOID_TAFFICJAM = 16;

    public static final String[] payNames = new String[]{"微信支付", "支付宝支付",
            "银联在线", "百度钱包"};

    public static final String PARTNER1 = "2088121294855412";
    // 商户收款账号
    public static final String SELLER1 = "ifnr0811@163.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE1 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM43ApIVA2CqZ0BhoSZD4hhih0XCxChFqbU2UzPp+eVgC8DLXlyD3qOqUwaQY7s/ySniPRH40a7z0uu4y+fRugbZ/Fhu7MD+g1FJEngVLqs+N6El6sWwhvUzl+AiOT3wbTqX1fKLXUApKn0LkywjkvNVeGmoZTypmGoPt9gMbO7HAgMBAAECgYBr7fTFeoQLCwUBeFe8zFjAxIjiZT7/iP563tULXdGyUOO143se1tInPRU5YUchkQ/7gPLPvjgY5nqlb1Ihoe5HiTkaDJKshblNKTjCJ//MfZ9rXFAF1NW+rgh/GDei9P6BZS3H8aWG4NMHoFhU+CdwuOS5tDBl+jD11yRQb22VMQJBAOeIMKuLN3RSHZlpDUQ2uB0RVB/sj+5ziRk/rJM/2FgD9uS3GABVDRTtyHb0Ss4ReYwCkVg524DgtTAPB6LmcckCQQDkAeWj9Heh3gJITXdRtiRNYq/XC7XJCOzLgCEakgz6edwpw4+q7UST9Tur8aGhesCPLuiwOMwgbwsRJ7dnjCQPAkEAzfqbUgHIk/N54cFTzBviL/n6nWwXoEuo7I15OobSMXtVaqR49dK0kKn6bdlns2xYdfbS12qhnUZL6zVz1biuAQJAeF9EBC3hJrSVUeSL1LirK6upF4fYHd03XiwzzdpDPc0t3WKgK2X96XrzWNBFO4IZvL3QHJCvu4PEPPdRzTngIwJBAJXNzUpr9jZkUo+LDjDxJ438OD3d9rUprcC0gBsqq+Py1+Kp8nmPMqKHbANxubh/LD5Zex/KUvfE3FRNfb53LgE=";

}
