package car.ccut.com.vehicle.util;


import java.util.Comparator;

import car.ccut.com.vehicle.bean.car.Car;

public class PinyinComparator implements Comparator<Car> {

    public int compare(Car o1, Car o2) {
        if (o1.getFirstLetter().equals("@") || o2.getFirstLetter().equals("#")) {
            return -1;
        } else if (o1.getFirstLetter().equals("#")
                || o2.getFirstLetter().equals("@")) {
            return 1;
        } else {
            return o1.getFirstLetter().compareTo(o2.getFirstLetter());
        }
    }

}
