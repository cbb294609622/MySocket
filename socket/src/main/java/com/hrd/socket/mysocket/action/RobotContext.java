package com.hrd.socket.mysocket.action;

import android.util.Log;

import com.xyirobot.base.manager.led.LedConfigs;
import com.xyirobot.base.manager.led.LedConfigs$Builder;
import com.xyirobot.open.led.RobotLedManager;
import com.xyirobot.open.led.RobotLeds;
import com.xyirobot.open.motion.RobotDirection;
import com.xyirobot.open.motion.RobotHand;
import com.xyirobot.open.motion.RobotMotionManager;
import com.xyirobot.open.motion.RobotMotions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by HP on 2018/3/30.
 */

public class RobotContext {
    private RobotMotionManager motionManager;
    private  RobotLedManager ledManager;
    public RobotContext(RobotMotionManager motionManager, RobotLedManager ledManager){
        this.motionManager = motionManager;
        this.ledManager = ledManager;
    }

    /**
     * 动作睡眠时间
     * @param ms 毫秒
     */
    public void threadSleepMs(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 机器人左转
     * @param ms    睡眠间隔
     */
    public void turnLeft(int ms){
        motionManager.action(RobotMotions.TURN_LEFT);
        threadSleepMs(ms);
    }

    /**
     * 机器人右转
     * @param ms    睡眠间隔
     */
    public void turnRight(int ms){
        motionManager.action(RobotMotions.TURN_RIGHT);
        threadSleepMs(ms);
    }

    public void turnLeftMove(int ms,int cm){
        motionManager.move(RobotDirection.LEFT, 2, 50);
        threadSleepMs(ms);
    }

    public void turnRightMove(int ms,int cm){
        motionManager.move(RobotDirection.RIGHT, 2, 50);
        threadSleepMs(ms);
    }

    /**
     * 机器人旋转
     * @param ms    睡眠间隔
     */
    public void turnRotate(int ms){
        motionManager.action(RobotMotions.TURN_AROUND);
        threadSleepMs(ms);
    }

    /**
     * 机器人头部    竖直动作
     * @param ms    睡眠间隔
     * @param head  0-45   0低头 45抬头
     */
    public void turnHeadVertical(int ms,int head){
        motionManager.controlHeadWithAbsAngle(RobotDirection.VERTICAL,head);
        threadSleepMs(ms);
    }
    /**
     * 机器人头部    水平动作
     * @param ms    睡眠间隔
     * @param head  0-180
     */
    public void turnHeadHorizontal(int ms,int head){
        motionManager.controlHeadWithAbsAngle(RobotDirection.HORIZONTAL,head);
        threadSleepMs(ms);
    }

    /**
     * 机器人手臂摆动
     */
    public void turnHandMove(int ms){
        motionManager.controlHandWithRelativeAngle(RobotHand.LEFT_HAND, RobotDirection.FORTH, 2, 150);
        motionManager.controlHandWithRelativeAngle(RobotHand.RIGHT_HAND, RobotDirection.BACK, 3, 200);
        turnRotate(2000);
        threadSleepMs(ms);
        motionManager.controlHandWithRelativeAngle(RobotHand.LEFT_HAND, RobotDirection.BACK, 4, 200);
        motionManager.controlHandWithRelativeAngle(RobotHand.RIGHT_HAND, RobotDirection.FORTH, 3, 200);
        threadSleepMs(ms);
        motionManager.controlHandWithAbsAngle(RobotHand.LEFT_HAND,2,180);
        motionManager.controlHandWithAbsAngle(RobotHand.RIGHT_HAND,3,180);
        turnRotate(2000);
        threadSleepMs(2000);
    }


    /**
     * 关闭灯源
     * 1    头部左灯源       2    头部右灯源
     * 3    手臂左灯源       4    手臂右灯源
     *
     * @param i 取值范围 1,2,3,4
     * @param i1 取值范围 1,2,3,4
     */
    public void offLed(int i, int i1) {
        final List<RobotLeds> leds = new ArrayList<RobotLeds>();
        leds.add(RobotLeds.forIntentValue(i));
        leds.add(RobotLeds.forIntentValue(i1));
        new Thread() {
            @Override
            public void run() {
                LedConfigs$Builder builder = new LedConfigs$Builder();
                builder.isOn(false);
                LedConfigs configs = builder.generate();
                for (int i = 0, size = leds.size(); i < size; i++) {
                    ledManager.setLed(leds.get(i), configs);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 开启闪烁灯源
     * 参数1 参数2
     * 1    头部左灯源       2    头部右灯源
     * 3    手臂左灯源       4    手臂右灯源
     *参数3
     * 2    LED_COLOR_WHITE     3    LED_COLOR_RED
     * 4    LED_COLOR_GREEN     5    LED_COLOR_PINK
     * 6    LED_COLOR_PURPLE    7    LED_COLOR_BLUE
     * 8    LED_COLOR_YELLOW    9    LED_COLOR_ALL
     *
     * @param i 取值范围 1,2,3,4
     * @param i1 取值范围 1,2,3,4
     * @param led 取值范围 2,3,4,5,6,7,8,9
     */
    public void lightLed(int i, int i1, final int led) {
        final List<RobotLeds> leds = new ArrayList<RobotLeds>();
        leds.add(RobotLeds.forIntentValue(i));
        leds.add(RobotLeds.forIntentValue(i1));
        new Thread() {
            @Override
            public void run() {
                LedConfigs$Builder builder = new LedConfigs$Builder();
                switch (led){
                    case 2:
                        builder.color(LedConfigs.LED_COLOR_WHITE);
                        break;
                    case 3:
                        builder.color(LedConfigs.LED_COLOR_RED);
                        break;
                    case 4:
                        builder.color(LedConfigs.LED_COLOR_GREEN);
                        break;
                    case 5:
                        builder.color(LedConfigs.LED_COLOR_PINK);
                        break;
                    case 6:
                        builder.color(LedConfigs.LED_COLOR_PURPLE);
                        break;
                    case 7:
                        builder.color(LedConfigs.LED_COLOR_BLUE);
                        break;
                    case 8:
                        builder.color(LedConfigs.LED_COLOR_YELLOW);
                        break;
                    case 9:
                        builder.color(LedConfigs.LED_COLOR_ALL);
                        break;
                }
                builder.isBlink(false);
                builder.isOn(true);
                LedConfigs configs = builder.generate();
                for (int i = 0, size = leds.size(); i < size; i++) {
                    ledManager.setLed(leds.get(i), configs);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    /**
     * 开启灯源
     * 参数1 参数2
     * 1    头部左灯源       2    头部右灯源
     * 3    手臂左灯源       4    手臂右灯源
     *参数3
     * 2    LED_COLOR_WHITE     3    LED_COLOR_RED
     * 4    LED_COLOR_GREEN     5    LED_COLOR_PINK
     * 6    LED_COLOR_PURPLE    7    LED_COLOR_BLUE
     * 8    LED_COLOR_YELLOW    9    LED_COLOR_ALL
     *
     * @param i 取值范围 1,2,3,4
     * @param i1 取值范围 1,2,3,4
     * @param led 取值范围 2,3,4,5,6,7,8,9
     */
    public void blinkLed(int i, int i1, final int led) {
        final List<RobotLeds> leds = new ArrayList<RobotLeds>();
        leds.add(RobotLeds.forIntentValue(i));
        leds.add(RobotLeds.forIntentValue(i1));
        new Thread() {
            @Override
            public void run() {
                LedConfigs$Builder builder = new LedConfigs$Builder();
                switch (led){
                    case 2:
                        builder.color(LedConfigs.LED_COLOR_WHITE);
                        break;
                    case 3:
                        builder.color(LedConfigs.LED_COLOR_RED);
                        break;
                    case 4:
                        builder.color(LedConfigs.LED_COLOR_GREEN);
                        break;
                    case 5:
                        builder.color(LedConfigs.LED_COLOR_PINK);
                        break;
                    case 6:
                        builder.color(LedConfigs.LED_COLOR_PURPLE);
                        break;
                    case 7:
                        builder.color(LedConfigs.LED_COLOR_BLUE);
                        break;
                    case 8:
                        builder.color(LedConfigs.LED_COLOR_YELLOW);
                        break;
                    case 9:
                        builder.color(LedConfigs.LED_COLOR_ALL);
                        break;
                }
                builder.isBlink(true);
                builder.isOn(true);
                builder.rate(1);
                LedConfigs configs = builder.generate();
                for (int i = 0, size = leds.size(); i < size; i++) {
                    ledManager.setLed(leds.get(i), configs);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 复位
     */
    public void reset(){
        int reset = motionManager.reset();
        Log.i("TAG",reset+"");
        threadSleepMs(3000);
        offLed(1,2);//关闭头部灯源
        offLed(3,4);//关闭手臂灯源
        Log.i("TAG","复位");
    }
    /**
     * 复位不关灯
     */
    public void resetLed(){
        int reset = motionManager.reset();
        Log.i("TAG",reset+"");
        threadSleepMs(3000);
        Log.i("TAG","复位");
    }
}
