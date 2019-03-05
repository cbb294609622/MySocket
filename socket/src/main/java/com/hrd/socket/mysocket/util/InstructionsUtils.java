package com.hrd.socket.mysocket.util;

/**
 * Created by HP on 2018/3/28.
 */

public class InstructionsUtils {

    public static int deCodeNum(String num){
        if (num.equals("1")){
            return 1;
        }else if (num.equals("2")){
            return 2;
        }else if (num.equals("3")){
            return 3;
        }else if (num.equals("4")){
            return 4;
        }else if (num.equals("5")){
            return 5;
        }else if (num.equals("6")){
            return 6;
        }else{
            return 0;
        }
    }

}
