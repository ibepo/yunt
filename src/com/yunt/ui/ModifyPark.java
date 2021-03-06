package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

/**
 * 
 * @author kefanbufan
 * @date 2015年11月11日15:58:30
 * 
 */

public class ModifyPark extends BaseAct {

	LinearLayout linBg, linFragment, linStep;
	RelativeLayout rlSubmit, rlZujin, rlTime;
	public TextView tvCancle;
	public TextView tvMapAddress;// 地图定位
	public TextView tvXiaoqu;// 小区
	public TextView tvChewei;// 车位
	public TextView tvMenjin;// 门禁
	public TextView tvPlate;// 车牌
	public TextView tvPriceHour;
	public TextView tvPriceMonth;
	public TextView tvXingqi;
	public TextView tvTime;
	public TextView tvPhone;

	// 1.基本信息字段
	public static String Address, positionX, positionY, ParkAddress, CarParkCode, ParkNumber, Plate, CodePosition,
			Garage;

	// 2.门禁类型字段
	public static String CodeControlType, HasParkControl, ParkImg, Remarks;

	// 3.时间价格字段
	// 价格
	public static String PriceHour = "";
	public static String PriceMonth = "";
	// 时间
	public static String allTime;
	public static String startTime = "";
	public static String endTime = "";
	// 可租日期
	public static String week = "";
	int dateFlag = 1;

	// 图片
	ArrayList<String> list = new ArrayList<String>();
	private Bitmap myBitmap;
	private byte[] myByte;
	private int picFlag = 0;

	// 获取到的详情信息
	String code;
	public HashMap<String, String> detailMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.modify_park);
		code = getIntent().getExtras().getString("code");
		initView();
		getParkInfo();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		getParkInfo();

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

	private void initView() {

		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("车位列表");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		// rlSubmit.setOnClickListener(this);

		tvMapAddress = (TextView) this.findViewById(R.id.tvMapAddress);
		tvXiaoqu = (TextView) this.findViewById(R.id.tvXiaoqu);
		tvChewei = (TextView) this.findViewById(R.id.tvCheWei);
		tvMenjin = (TextView) this.findViewById(R.id.tvMenjin);
		tvPlate = (TextView) this.findViewById(R.id.tvPlate);
		tvPriceHour = (TextView) this.findViewById(R.id.tvPriceHour);
		tvPriceMonth = (TextView) this.findViewById(R.id.tvPriceMonth);
		tvXingqi = (TextView) this.findViewById(R.id.tvXingqi);
		tvTime = (TextView) this.findViewById(R.id.tvTime);
		tvPhone = (TextView) this.findViewById(R.id.tvPhone);

		rlZujin = (RelativeLayout) this.findViewById(R.id.rlZujin);
		rlZujin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(ModifyPark.this, ModifyParkJiaGe.class);
				intent.putExtra("code", detailMap.get("CODE").toString());
				ModifyPark.this.startActivity(intent);

			}
		});

		rlTime = (RelativeLayout) this.findViewById(R.id.rlTime);
		rlTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent2 = new Intent(ModifyPark.this, ModifyParkTime.class);
				intent2.putExtra("code", detailMap.get("CODE").toString());
				ModifyPark.this.startActivity(intent2);

			}
		});

	}

	private void getParkInfo() {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<HashMap<String, String>>() {

				});

				tvMapAddress.setText(detailMap.get("ADDRESS"));
				tvXiaoqu.setText(detailMap.get("PARK_ADDRESS"));
				if (detailMap.get("GARAGE") != null) {
					tvChewei.setText(detailMap.get("GARAGE") + detailMap.get("PARK_NUMBER") + "("
							+ detailMap.get("CODE_POSITION_NAME") + ")");
				} else {
					tvChewei.setText(detailMap.get("PARK_NUMBER") + " (" + detailMap.get("CODE_POSITION_NAME")
							+ ")");
				}

				if (detailMap.get("HAS_PARK_CONTROL").equals("0")) {
					tvMenjin.setText(detailMap.get("CODE_CONTROL_TYPE_NAME"));
				} else {
					tvMenjin.setText("小区门禁为" + detailMap.get("CODE_CONTROL_TYPE_NAME") + ",车库有门禁");
				}

				tvPlate.setText(detailMap.get("PLATE"));
				// Log.e("车牌", detailMap.get("PLATE"));
				tvPriceHour.setText(detailMap.get("PRICE_HOUR") + "元/时");
				tvPriceMonth.setText(detailMap.get("PRICE_MONTH") + "元/月");
				tvXingqi.setText(detailMap.get("WEEKNAME"));
				tvPhone.setText(detailMap.get("PARK_PHONE"));

				if (detailMap.get("ALL_TIME").equals("0")) {
					// 判断时间间隔
					String ss = MyTextUtils.noSpace(detailMap.get("START_TIME"));
					String ee = MyTextUtils.noSpace(detailMap.get("END_TIME"));

					int s = Integer.parseInt(ss.replace(":", ""));
					int e = Integer.parseInt(ee.replace(":", ""));
					if (s - e < 0) {
						tvTime.setText(detailMap.get("START_TIME") + " — " + (detailMap.get("END_TIME")));
					} else {
						tvTime.setText(detailMap.get("START_TIME") + " — " + (detailMap.get("END_TIME")) + "(次日)");
					}

				} else {
					tvTime.setText("全天可租");
				}

				// positionX = detailMap.get("POSITION_X");
				// positionY = detailMap.get("POSITION_Y");

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.rlSubmit:
	// getBascinfo();
	// getRentalInfo();
	// getRentalTime();
	// }
	// }
	//
	// // 得到基本信息的数据
	// @SuppressWarnings("unchecked")
	// private void getBascinfo() {
	//
	// // 地图定位
	// TextView temp = (TextView) this.findViewById(R.id.tvMapAddress);
	// Address = (String) temp.getText();
	//
	// // 经纬度
	// HashMap<String, String> map = new HashMap<String, String>();
	// map = (HashMap<String, String>) temp.getTag();
	// positionX = map.get("positionX");
	// positionY = map.get("positionY");
	// if (MyTextUtils.isEmpty(positionX)) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "地图定位不能为空");
	// return;
	// }
	//
	// // 小区全称
	// TextView etXiaoqu = (TextView) this.findViewById(R.id.etXiaoqu);
	// ParkAddress = etXiaoqu.getText().toString();
	// if (MyTextUtils.isEmpty(ParkAddress)) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "小区全称不能为空");
	// return;
	// }
	// // 小区code
	// CarParkCode = BasicInfo4SubmitFragment.CarParkCode;
	//
	// // 车库类型
	// CodePosition = BasicInfo4SubmitFragment.CodePosition;
	// EditText etDikuName = (EditText) this.findViewById(R.id.etDikuName);
	// Garage = etDikuName.getText().toString();
	//
	// // 车位编号
	// EditText etBianhao = (EditText) this.findViewById(R.id.etBianhao);
	// ParkNumber = etBianhao.getText().toString();
	//
	// // 业主车牌
	// TextView tvjc = (TextView) this.findViewById(R.id.tvJiancheng);
	// EditText etCarNumber = (EditText) this.findViewById(R.id.etCarNumber);
	// Plate = tvjc.getText() + etCarNumber.getText().toString();
	//
	// if (MyTextUtils.isEmpty(ParkNumber) &&
	// MyTextUtils.isEmpty(etCarNumber.getText().toString())) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(),
	// "车位编号和业主车牌不能同时为空");
	// return;
	// }
	//
	// Log.e("提交车位_基本信息", Address + " " + positionX + "," + positionY + " " +
	// "小区全称:" + ParkAddress + " "
	// + "小区code:" + CarParkCode + " " + "车库类型:" + CodePosition + " " + "车库名称:"
	// + Garage + " " + "车位编号:"
	// + ParkNumber + " " + Plate);
	//
	// }
	//
	// private void getRentalInfo() {
	// // 车库有无门禁
	// HasParkControl = RentalInfo4SubmitFragment.HasParkControl;
	//
	// // 门禁类型
	// CodeControlType = RentalInfo4SubmitFragment.CodeControlType;
	//
	// // 备注
	// EditText etRemarks = (EditText) this.findViewById(R.id.etRemarks);
	// Remarks = etRemarks.getText().toString().trim();
	//
	// Log.e("提交车位_门禁类型", "车库门禁:" + HasParkControl + " " + "小区门禁:" +
	// CodeControlType + " " + "备注:" + Remarks);
	//
	// }
	//
	// private void getRentalTime() {
	//
	// // 日租价格
	// EditText etPriceHour = (EditText) this.findViewById(R.id.etPriceHour);
	// PriceHour = etPriceHour.getText().toString().trim();
	// if (MyTextUtils.isEmpty(PriceHour)) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "日租价格不能为空");
	// return;
	// }
	//
	// // 月租价格
	// EditText etPriceMonth = (EditText) this.findViewById(R.id.etPriceMonth);
	// PriceMonth = etPriceMonth.getText().toString().trim();
	// if (MyTextUtils.isEmpty(PriceMonth)) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "月租价格不能为空");
	// return;
	// }
	//
	// // 24小时可租
	// allTime = RentalTime4SubmitFragment.allTime;
	//
	// // 开始时间
	// TextView tvstartTime = (TextView) this.findViewById(R.id.tvStartTime);
	// startTime = tvstartTime.getText().toString().trim();
	// // 结束时间
	// TextView tvEndTime = (TextView) this.findViewById(R.id.tvEndTime);
	// endTime = tvEndTime.getText().toString().trim();
	//
	// // 非全天可租时的非空判断
	// if (allTime.equals("0")) {
	// if (startTime.equals("点击选择")) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "开始时间不能为空");
	// return;
	// }
	// if (endTime.equals("点击选择")) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "结束时间不能为空");
	// return;
	// }
	// // 判断时间间隔
	// int s = Integer.parseInt(startTime.replace(":", "").trim());
	// int e = Integer.parseInt(endTime.replace(":", "").trim());
	// if (s - e >= 0) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "开始时间不能大于结束时间");
	// return;
	// }
	// } else {
	// startTime = "0";
	// endTime = "0";
	// }
	//
	// // 可租星期
	// for (String temp : RentalTime4SubmitFragment.week) {
	// week = week + "," + temp;
	// }
	// if (!week.isEmpty()) {
	// week = week.substring(1);
	// } else {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "可租日期不能为空");
	// return;
	// }
	//
	// Log.e("提交车位_时间价格", " 日租价格:" + PriceHour + " " + "月租价格:" + PriceMonth +
	// " " + "开始时间:" + startTime + " "
	// + "结束后时间:" + endTime + " " + " 24小时可租:" + allTime + " " + " 可租日期:" +
	// week);
	//
	// // 提交地址
	// showDialog();
	// if (Bimp.tempSelectBitmap.size() > 0) {
	//
	// for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
	// myBitmap = Bimp.tempSelectBitmap.get(i).getBitmap();
	// myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
	// list.add(CameraUtil.byte2hex(myByte));
	// }
	//
	// for (int i = 0; i < list.size(); i++) {
	// submitPic(list.get(i));
	// }
	//
	// } else {
	// submitData();
	// }
	//
	// }
	//
	// private void submitPic(String s) {
	//
	// String picUrl = PathConfig.ADDRESS + "/zcw/base/bupload/uploadApp";
	// picUrl = MyTextUtils.urlPlusFoot(picUrl);
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("picString", s);
	// params.put("inCode", ParkImg);
	//
	// Request<JSONObject> request = new VolleyCommonPost(picUrl, new
	// Response.Listener<JSONObject>() {
	//
	// @Override
	// public void onResponse(JSONObject response) {
	//
	// String jsondata = response.toString();
	// Map<String, String> message = JSON.parseObject(jsondata, new
	// TypeReference<Map<String, String>>() {
	// });
	//
	// if (picFlag <= Bimp.tempSelectBitmap.size()) {
	// picFlag++;
	// } else {
	// submitData();
	// }
	//
	// // ToastUtils.showSuperToastAlert(getApplication(), jsondata);
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// ToastUtils.showSuperToastAlert(getApplicationContext(), "上传图片失败");
	// }
	// }, params);
	// request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
	// ApplicationController.getInstance().addToRequestQueue(request);
	// }
	//
	// protected void submitData() {
	//
	// String url;
	//
	// // if (tvlogin.getText().equals("确认修改")) {
	// // url = PathConfig.ADDRESS + "/base/breleasepark/modify";
	// // url = MyTextUtils.urlPlusFoot(url);
	// // } else {
	// url = PathConfig.ADDRESS + "/base/breleasepark/add";
	// url = MyTextUtils.urlPlusFoot(url);
	// // }
	//
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("Address", Address);
	// params.put("positionX", positionX);
	// params.put("positionY", positionY);
	// params.put("ParkAddress", ParkAddress);
	// params.put("CarParkCode", CarParkCode);
	// params.put("ParkNumber", ParkNumber);
	// params.put("Plate", Plate);
	// params.put("CodePosition", CodePosition);
	// params.put("Garage", Garage);
	//
	// params.put("CodeControlType", CodeControlType);
	// params.put("HasParkControl", HasParkControl);
	// params.put("ParkImg", ParkImg);
	// params.put("Remarks", Remarks);
	//
	// params.put("PriceHour", PriceHour);
	// params.put("PriceMonth", PriceMonth);
	// params.put("allTime", allTime);
	// params.put("startTime", startTime);
	// params.put("endTime", endTime);
	// params.put("week", week);
	//
	// Request<JSONObject> request = new VolleyCommonPost(url, new
	// Response.Listener<JSONObject>() {
	// @Override
	// public void onResponse(JSONObject response) {
	// String jsondata = response.toString();
	// Map<String, String> message = JSON.parseObject(jsondata, new
	// TypeReference<Map<String, String>>() {
	// });
	//
	// if (message.get("status").equals("true")) {
	// dismissDialog();
	// finish();
	// Intent intent03 = new Intent(ModifyPark.this, SubmitParkSucess.class);
	// startActivity(intent03);
	// } else {
	// ToastUtils.showSuperToastAlert(ModifyPark.this, message.get("info"));
	// finish();
	// }
	//
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// dismissDialog();
	// ToastUtils.showSuperToastAlert(ModifyPark.this, "连接服务器失败,请稍后重试!");
	// }
	// }, params);
	// request.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
	// ApplicationController.getInstance().addToRequestQueue(request);
	// }
	//
	// protected void submitDate(String parkCode) {
	// String url;
	// url = PathConfig.ADDRESS + "/base/bwake/add";
	// url = MyTextUtils.urlPlusFoot(url);
	//
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("parkCode", parkCode);
	// params.put("week", week);
	// params.put("startTime", startTime);
	// params.put("endTime", endTime);
	// params.put("allTime", allTime);
	//
	// Request<JSONObject> request = new VolleyCommonPost(url, new
	// Response.Listener<JSONObject>() {
	// @Override
	// public void onResponse(JSONObject response) {
	// String jsondata = response.toString();
	// Map<String, String> message = JSON.parseObject(jsondata, new
	// TypeReference<Map<String, String>>() {
	// });
	//
	// dismissDialog();
	// if (message.get("status").equals("true")) {
	// finish();
	// Intent intent03 = new Intent(ModifyPark.this, SubmitParkSucess.class);
	// startActivity(intent03);
	//
	// } else {
	// ToastUtils.showSuperToastAlert(ModifyPark.this, message.get("info"));
	// finish();
	// }
	//
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// dismissDialog();
	// ToastUtils.showSuperToastAlert(ModifyPark.this, "连接服务器失败,请稍后重试!");
	// }
	// }, params);
	// request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
	// ApplicationController.getInstance().addToRequestQueue(request);
	//
	// }

}
