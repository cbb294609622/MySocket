package com.hrd.socket.mysocket.net;

import java.util.List;

/**
 * Created by HP on 2018/1/26.
 */

public class Api {
//    private static final String URL_HOST = "http://192.168.1.220/";
    private static final String URL_HOST = "https://mhfq.huirendai.com/";

    private static final String URL_IP_ADDRESS = URL_HOST+"robot/qrcode";//获取二维码
    public static String getIPAddress(){
        return URL_IP_ADDRESS;
    }

    public static String[] books = new String[]{"登鹳雀楼,唐代：王之涣,白日依山尽，黄河入海流。,欲穷千里目，更上一层楼。",
            "相思,唐代：王维,红豆生南国，春来发几枝。愿君多采撷，此物最相思。",
            "春晓,唐代：孟浩然,春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。",
            "九月九日忆山东兄弟,唐代：王维,独在异乡为异客，每逢佳节倍思亲。遥知兄弟登高处，遍插茱萸少一人。"};

    public static String[] jokes = new String[]{"一只蚂蚁在路上看见一头大象，蚂蚁钻进土里，只有一只腿露在外面。同伴看见不解的问：为什么把腿露在外面？这只蚂蚁说：嘘！别出声，我绊他一跤!",
            "一个财主请了一位老师教儿子识字。第一天老师教写“一二三”，“一”字就是“一横”，“二”字就是“两横”，“三”字就是“三横”，财主的儿子一想“原来字这么简单”，就跟财主说他已经学会写字了，财主非常高兴，就考一考他，叫他写个“百”字，他随即拿起笔写了好久才写好，拿给财主看，财主当场气晕了过去，原来他在纸上画了100条横线。",
            "在澳大利亚的草原上,有两只奶牛在吃草.其中一头对另一头说:最近流行疯牛病,你说我们会不会得? 另一头答道:怕什么,我们不是袋鼠么?",
            "说有一只北极熊，因为雪地太刺眼了，必须要戴墨镜才能看东西， 可是他找不到墨镜，于是闭着眼睛爬来爬去在地上找，爬呀爬呀，把手脚都爬的脏兮兮的才找到墨镜。 戴上墨镜，对着镜子一照，这才发现：原来我是一只熊猫呀",
            "一只北极熊孤单的呆在冰上发呆，实在无聊就开始拔自己的毛玩，一根，两根，三根，最后拔的一根不剩，然后他就冷死了。",
            "小明新理了发，第二天来到学校，同学们看到他的新发型，笑道：小明，你的头型好像个风筝呀！小明觉得很委屈，就跑到外面哭，哭着哭着，他就飞起来了。",
            "有一天小明走在路上！走着走着突然觉得脚很酸！为什么会这样呢?因为小明踩到柠檬了！"};
    public static String[] str = new String[]{
            "从今天开始和我作朋友吧!",
            "我们一起玩成语接龙吧！",
            "我们一起跳舞吧！",
            "我们可以一起玩耍啦"};
}
