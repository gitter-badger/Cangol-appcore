/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import mobi.cangol.mobile.logging.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;


public class LocationUtils {
//	/**
//	 * 获取偏移值
//	 * @param longitude
//	 * @param latitude
//	 * @param path
//	 * @return
//	 */
//	public static GeoPoint adjustLoction(double lng, double lat) {
//		String offsetString = getOffset(lat,lng);
//		int index = offsetString.indexOf(",");
//		if (index > 0) {
//			// 将坐标值转为18级相应的像素值
//			double lngPixel = lonToPixel(lng, 18);
//			double latPixel = latToPixel(lat, 18);
//			// 获取偏移值
//			String OffsetX = offsetString.substring(0, index).trim();
//			String OffsetY = offsetString.substring(index + 1).trim();
//			//加上偏移值
//			double adjustLngPixel = lngPixel + Double.valueOf(OffsetX);
//			double adjustLatPixel = latPixel + Double.valueOf(OffsetY);
//			//由像素值再转为经纬度
//			double adjustLng = pixelToLon(adjustLngPixel, 18);
//			double adjustLat = pixelToLat(adjustLatPixel, 18);
//
//			return new GeoPoint((int) (adjustLat * 1000000),
//					(int) (adjustLng * 1000000));
//		}
//		//经验公式
//		return new GeoPoint((int) ((lat - 0.0025) * 1000000),
//				(int) ((lng + 0.0045) * 1000000));
//	}
	/**
	 * 获取偏移变量
	 * @param lat
	 * @param lng
	 * @return
	 */
     private static String getOffset(double lat,double lng){
		String url = String  
                .format("http://www.mapdigit.com/guidebeemap/offsetinchina.php?lng=%f&lat=%f",  
                		lat, lng);
		String response=null;
		AndroidHttpClient httpClient=AndroidHttpClient.newInstance("android");
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {	
				  HttpEntity httpEntity = httpResponse.getEntity();
				  response = EntityUtils.toString(httpEntity, "UTF-8");
			  }else{
				  Log.d("response fail :"+httpResponse.getStatusLine().getStatusCode() );
			  }
			 return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}  finally {
			 httpClient.close();
	    }
	}
	
	/**
	 * 经度到像素X值
	 * 
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static double lonToPixel(double lng, int zoom) {
		return (lng + 180) * (256L << zoom) / 360;
	}

	/**
	 * 像素X到经度
	 * 
	 * @param pixelX
	 * @param zoom
	 * @return
	 */
	public static double pixelToLon(double pixelX, int zoom) {
		return pixelX * 360 / (256L << zoom) - 180;
	}

	/**
	 * 纬度到像素Y
	 * 
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static double latToPixel(double lat, int zoom) {
		double siny = Math.sin(lat * Math.PI / 180);
		double y = Math.log((1 + siny) / (1 - siny));
		return (128 << zoom) * (1 - y / (2 * Math.PI));
	}

	/**
	 * 像素Y到纬度
	 * 
	 * @param pixelY
	 * @param zoom
	 * @return
	 */
	public static double pixelToLat(double pixelY, int zoom) {
		double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));
		double z = Math.pow(Math.E, y);
		double siny = (z - 1) / (z + 1);
		return Math.asin(siny) * 180 / Math.PI;
	}
	
	/**
	 * 获取UTC时间
	 * @return
	 */
	public static Long getUTCTime() {
		Calendar cal=Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("gmt"));
		return cal.getTimeInMillis();
	}
	
	/**
	 * 获取本地时间
	 * @return
	 */
	public static Long getLocalTime() {
		Calendar cal=Calendar.getInstance();
		return cal.getTimeInMillis();
	}
	/**
	 * 百度逆地理编码(需要密钥access key)
	 * <br>http://developer.baidu.com/map/webservice-geocoding.htm#.E8.BF.94.E5.9B.9E.E6.95.B0.E6.8D.AE.E8.AF.B4.E6.98.8E
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static String getAddressByBaidu(double lat,double lng,String ak){
		String url = String  
                .format("http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&location=%f,%f&output=json&pois=0",  
                		ak,lat, lng);
		String address=null;
		AndroidHttpClient httpClient=AndroidHttpClient.newInstance("android");
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {	
				  HttpEntity httpEntity = httpResponse.getEntity();
				  String response = EntityUtils.toString(httpEntity, "UTF-8");
				  int start="renderReverse&&renderReverse(".length();
				  int end=response.lastIndexOf(")");
				  JSONObject json=new JSONObject(response.substring(start, end));	
				  address=json.getJSONObject("result").getString("formatted_address");
			  }else{
				  Log.d("response fail :"+httpResponse.getStatusLine().getStatusCode() );
			  }
			 return address;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} finally {
			 httpClient.close();
	    }
	}
	
	/**
	 * Google逆地理编码
	 * <br>https://developers.google.com/maps/documentation/geocoding/?hl=zh-cn#ReverseGeocoding
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static String getAddressByGoogle(double lat,double lng){
		String url = String  
                .format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=zh-cn&sensor=true",  
                        lat, lng);
		String address=null;
		AndroidHttpClient httpClient=AndroidHttpClient.newInstance("android");
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {	
				  HttpEntity httpEntity = httpResponse.getEntity();
				  String response = EntityUtils.toString(httpEntity, "UTF-8");
				  JSONObject json=new JSONObject(response);
				  address=json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
			  }else{
				  Log.d("response fail :"+httpResponse.getStatusLine().getStatusCode() );
			  }
			 return address;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} finally {
			 httpClient.close();
	    }
		
	}
}
