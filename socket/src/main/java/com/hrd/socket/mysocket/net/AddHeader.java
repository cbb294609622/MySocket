package com.hrd.socket.mysocket.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by HP on 2018/1/26.
 * 参数请求class
 * 首先 实例化AddHeader
 * AddHeader adds = new AddHeader();//实例化
 * adds.addData(key,value);//传参
 * adds.getHeader();//不加密
 * <p>
 * adds.getHeaderDes();//加密
 */

public class AddHeader {

    JSONObject dataJson = new JSONObject();


    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    public void addData(String key, String value) {
        try {
            dataJson.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 header
     *
     * @return 不加密
     */
    public String getHeader() {
        String info = dataJson.toString().replaceAll(" ", "");
        return info;
    }
}
