package com.hrd.socket.mysocket.util;

import com.google.gson.Gson;

/**
 * Created by HP on 2018/1/30.
 */

public class GsonUtil {
    private static Gson mGson = new Gson();
    /**
     * 将json字符串转化成实体对象
     * @param json
     * @param classOfT
     * @return
     */
    public static Object toFrom( String json , Class classOfT){
        return  mGson.fromJson( json , classOfT ) ;
    }
}
