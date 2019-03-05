package com.hrd.socket.mysocket.net;

import com.hrd.socket.mysocket.bean.PublicBean;
import com.hrd.socket.mysocket.util.GsonUtil;

/**
 * Created by HP on 2018/1/31.
 */

public class JsonParser {

    public static PublicBean getDesJson(String response){
        PublicBean bean = (PublicBean) GsonUtil.toFrom(response, PublicBean.class);
        return bean;
    }
}
