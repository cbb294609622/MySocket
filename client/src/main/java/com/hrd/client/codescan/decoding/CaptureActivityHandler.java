/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hrd.client.codescan.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hrd.client.R;
import com.hrd.client.codescan.MipcaActivityCapture;
import com.hrd.client.codescan.camera.CameraManager;
import com.hrd.client.codescan.view.ViewfinderResultPointCallback;

import java.util.Vector;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 * 这个类处理所有包括状态捕获的消息。
 */
public final class CaptureActivityHandler extends Handler {

  private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final MipcaActivityCapture activity;
  private final DecodeThread decodeThread;
  private State state;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public CaptureActivityHandler(MipcaActivityCapture activity, Vector<BarcodeFormat> decodeFormats,
      String characterSet) {
    this.activity = activity;
    decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
        new ViewfinderResultPointCallback(activity.getViewfinderView()));
    decodeThread.start();
    state = State.SUCCESS;
    // Start ourselves capturing previews and decoding.
    //开始自己拍摄预览和解码。
    CameraManager.get().startPreview();
    restartPreviewAndDecode();
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.auto_focus:
        //Log.d(TAG, "Got auto-focus message");
        // When one auto focus pass finishes, start another. This is the closest thing to当一个自动对焦通完成，开始另一个。这是最接近
        // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.连续自动对焦。它似乎捕获了一点，但我不知道自己还能做些什么
        if (state == State.PREVIEW) {
          CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
        break;
      case R.id.restart_preview:
        Log.d(TAG, "Got restart preview message");
        restartPreviewAndDecode();
        break;
      case R.id.decode_succeeded:
        Log.d(TAG, "Got decode succeeded message");
        state = State.SUCCESS;
        Bundle bundle = message.getData();
        
        /***********************************************************************/
        Bitmap barcode = bundle == null ? null :
            (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);//���ñ����߳�
        
        activity.handleDecode((Result) message.obj, barcode);//���ؽ��?        /***********************************************************************/
        break;
      case R.id.decode_failed:
        // We're decoding as fast as possible, so when one decode fails, start another.我们解码尽可能快，所以当一个解码失败，开始另一个。
        state = State.PREVIEW;
        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        break;
      case R.id.return_scan_result:
        Log.d(TAG, "Got return scan result message");//获得返回扫描结果消息
        activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
        activity.finish();
        break;
      case R.id.launch_product_query:
        Log.d(TAG, "Got product query message");//得到产品查询信息
        String url = (String) message.obj;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(intent);
        break;
    }
  }

  public void quitSynchronously() {
    state = State.DONE;
    CameraManager.get().stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
    quit.sendToTarget();
    try {
      decodeThread.join();
    } catch (InterruptedException e) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded);
    removeMessages(R.id.decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
      CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
      CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
      activity.drawViewfinder();
    }
  }

}
