package model.enums;

/**
 * Created by IntelliJ IDEA.
 *
 * @User: Zacky Kharboutli
 * @Date: 2018-05-10
 * @Project : HotelSystem
 */


public enum Adjacent {
    YES {
        @Override
        public String toString() {
            return "Yes";
        }
    },

    NO {
        @Override
        public String toString() {
            return "No";
        }
    };
    public static Adjacent toEnum(String str){
        if(str.equals("Yes")){
            return YES;
        } else if(str.equals("No")){
            return NO;
        }
        return null;
    }
}
