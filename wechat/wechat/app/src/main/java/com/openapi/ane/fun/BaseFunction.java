package com.openapi.ane.fun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.adobe.fre.FREArray;
import com.adobe.fre.FREBitmapData;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.openapi.Constants;

import java.util.ArrayList;
import java.util.List;

public class BaseFunction implements FREFunction {

	@Override
	public FREObject call(FREContext context, FREObject[] args) {
		return null;
	}

	/**
	 * 获取string
	 * @param object
	 * @return
	 */
	protected String getStringFromFREObject(FREObject object) {

		try {
			return object.getAsString();
		}
		catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取boolean
	 * @param object
	 * @return
	 */
	protected Boolean getBooleanFromFREObject(FREObject object) {

		try {
			return object.getAsBool();
		}
		catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取list
	 * @param array
	 * @return
	 */
	protected List<String> getListOfStringFromFREArray(FREArray array) {

		List<String> result = new ArrayList<String>();
		
		try {

            for (int i = 0; i < array.getLength(); i++) {

				try {
					result.add(getStringFromFREObject(array.getObjectAt((long)i)));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		
		return result;
	}

	/**
	 * 获取图片数据
	 * @return
	 */
	public static Bitmap getBitmapFromFreBitmapdata(final FREBitmapData as3Bitmap){
		//http://stackoverflow.com/questions/17314467/bitmap-channels-order-different-in-android
		Bitmap m_encodingBitmap         = null;
		Canvas m_canvas                 = null;
		Paint m_paint                   = null;
		final float[] m_bgrToRgbColorTransform  =
				{
						0,  0,  1f, 0,  0,
						0,  1f, 0,  0,  0,
						1f, 0,  0,  0,  0,
						0,  0,  0,  1f, 0
				};
		final ColorMatrix m_colorMatrix               = new ColorMatrix(m_bgrToRgbColorTransform);
		final ColorMatrixColorFilter m_colorFilter               = new ColorMatrixColorFilter(m_colorMatrix);
		try{
			as3Bitmap.acquire();
			int srcWidth = as3Bitmap.getWidth();
			int srcHeight = as3Bitmap.getHeight();
			m_encodingBitmap    = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
			m_canvas        = new Canvas(m_encodingBitmap);
			m_paint         = new Paint();
			m_paint.setColorFilter(m_colorFilter);

			m_encodingBitmap.copyPixelsFromBuffer(as3Bitmap.getBits());
			as3Bitmap.release();
		}catch (Exception e) {
			e.printStackTrace();
			Log.d(Constants.tag, "fail to conver image to bitmap");
		}
		//
		// Convert the bitmap from BGRA to RGBA.
		//
		m_canvas.drawBitmap(m_encodingBitmap, 0, 0, m_paint);
		return m_encodingBitmap;
	}

}
