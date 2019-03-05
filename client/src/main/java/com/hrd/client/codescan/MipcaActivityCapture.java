package com.hrd.client.codescan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hrd.client.MainActivity;
import com.hrd.client.R;
import com.hrd.client.codescan.camera.CameraManager;
import com.hrd.client.codescan.decoding.CaptureActivityHandler;
import com.hrd.client.codescan.decoding.InactivityTimer;
import com.hrd.client.codescan.view.ViewfinderView;
import com.hrd.client.util.ToastUtil;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends Activity implements Callback,View.OnClickListener {

	private CaptureActivityHandler handler;
	/**
	 * 自定义View
	 */
	private ViewfinderView viewfinderView;
	/**
	 * 相机初始化
	 */
	private boolean hasSurface;
	/**
	 *条形码格式
	 */
	private Vector<BarcodeFormat> decodeFormats;
	/**
	 * 字符集
	 */
	private String characterSet;
	/**
	 * 定时器
	 */
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	/**
	 * 是否播放声响
	 */
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	/**
	 * 振动
	 */
	private boolean vibrate;

	private ImageButton ibFlashLight;
	private ImageButton ibReturn;

    private boolean isOpen = false;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		setContentView(R.layout.activity_capture);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());//  初始化相机
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		ibFlashLight = (ImageButton) findViewById(R.id.ib_flashlight_control);
		ibReturn = (ImageButton) findViewById(R.id.ib_code_scan_cancel);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		ibFlashLight.setOnClickListener(this);
		ibReturn.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);//初始化相机
		} else {
			surfaceHolder.addCallback(this);//添加回调SurfaceHolder，对SurfaceView进行控制。
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//只能拍照不能绘制了//SurfaceView没有自己的buffer
		}

		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		//调用系统音频服务
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		//如果不是铃声模式
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();//退出同步
			handler = null;
		}
		//关闭相机
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();//活动计时器关闭
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
//		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "扫描失败！", Toast.LENGTH_SHORT).show();
		}else {
			Log.i("scannnnnnn", resultString);
            //扫描成功
			processCode(resultString);

		}
	}
    private void processCode(String resultString) {
		Intent intent = new Intent(MipcaActivityCapture.this, MainActivity.class);
		intent.putExtra("ips",resultString);
		startActivity(intent);
		this.finish();
    }

    /**
	 * 初始化相机
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	//SurfaceHolder.Callback接口需实现三个方法：
	/*----------------------开始----------------------*/
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	/*----------------------结束----------------------*/

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 初始化声音
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	/**
	 * 播放嘟嘟的声音和振动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 * 声音播放完
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_flashlight_control:
                if(!isOpen){
                    CameraManager.get().openLight();
                    isOpen = true;
                    ibFlashLight.setImageResource(R.mipmap.qrcode_scan_btn_flashlight_off);
                }else{
                    CameraManager.get().offLight();
                    isOpen = false;
                    ibFlashLight.setImageResource(R.mipmap.qrcode_scan_btn_flashlight_on);
                }

                break;
            case R.id.ib_code_scan_cancel:
                finish();
                break;
        }
    }
}