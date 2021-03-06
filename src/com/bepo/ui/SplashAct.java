package com.bepo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.update.UpdateManager;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.yunt.ui.HomeAct2;

public class SplashAct extends BaseAct {

	private static final String uploadUrl = PathConfig.ADDRESS + "gsm/upload/android/version.xml";
	private UpdateManager manager = new UpdateManager(uploadUrl, this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		start();
	}

	private void start() {
		new Thread() {
			public void run() {
				checkUpdate();

				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Looper.prepare();

			}
		}.start();
	}

	public void checkUpdate() { // 版本检测方式2：带更新回调监听
		PgyUpdateManager.register(SplashAct.this, new UpdateManagerListener() {
			@Override
			public void onUpdateAvailable(final String result) {
				final AppBean appBean = getAppBeanFromString(result);

				new AlertDialog.Builder(SplashAct.this).setTitle("更新").setMessage("")
						.setNegativeButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								startDownloadTask(SplashAct.this, appBean.getDownloadURL());
							}
						}).show();

			}

			@Override
			public void onNoUpdateAvailable() {
				Intent mIntent = new Intent(SplashAct.this, HomeAct2.class);
				SplashAct.this.startActivity(mIntent);
				finish();
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		// overridePendingTransition(R.anim.fade_in, R.anim.hold);
	}

	// private void initChartData() {
	// String url = PathConfig.ADDRESS +
	// "gsm/charts/bpersoncharts/personCharts";
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("gridcode", PathConfig.girdCode);
	// Request<JSONObject> request = new VolleyCommonPost(url, new
	// Response.Listener<JSONObject>() {
	// @Override
	// public void onResponse(JSONObject response) {
	//
	// String jsondata = response.toString();
	// Map<String, String> message = JSON.parseObject(jsondata, new
	// TypeReference<Map<String, String>>() {
	// });
	//
	// String xdataStr = message.get("categories");
	// if (xdataStr.indexOf(",") >= 0) {
	// // xdata = (ArrayList<String>)
	// // Arrays.asList(xdataStr.split(","));
	//
	// String ss[] = xdataStr.split(",");
	// for (String m : ss) {
	// PathConfig.xdata.add(m);
	// }
	//
	// } else {
	// PathConfig.xdata.add(xdataStr);
	// }
	//
	// String ydataStr = message.get("seriesdata");
	// if (ydataStr.indexOf(",") >= 0) {
	// // ydata = (ArrayList<String>)
	// // Arrays.asList(ydataStr.split(","));
	//
	// String ss[] = ydataStr.split(",");
	// for (String m : ss) {
	// PathConfig.ydata.add(m);
	// }
	//
	// } else {
	// PathConfig.ydata.add(ydataStr);
	// }
	// start();
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// }
	// }, params);
	//
	// ApplicationController.getInstance().addToRequestQueue(request);
	// }

}
