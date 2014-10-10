package com.buzzingandroid.ui;

import android.view.View.MeasureSpec;

/**
 * This class is a helper to measure views that require a specific aspect ratio.<br />
 * <br />
 * The measurement calculation is differing depending on whether the height and width
 * are fixed (match_parent or a dimension) or not (wrap_content)
 * 
 * <pre>
 *                | Width fixed | Width dynamic |
 * ---------------+-------------+---------------|
 * Height fixed   |      1      |       2       |
 * ---------------+-------------+---------------|
 * Height dynamic |      3      |       4       |
 * </pre>
 * Everything is measured according to a specific aspect ratio.<br />
 * <br />
 * <ul>
 * <li>1: Both width and height fixed:   Fixed (Aspect ratio isn't respected)</li>
 * <li>2: Width dynamic, height fixed:   Set width depending on height</li>
 * <li>3: Width fixed, height dynamic:   Set height depending on width</li>
 * <li>4: Both width and height dynamic: Largest size possible</li>
 * </ul>
 * 
 * @author Jesper Borgstrup
 */
public class ViewAspectRatioMeasurer {
	
	private double aspectRatio;
	
	/**
	 * Create a ViewAspectRatioMeasurer instance.<br/>
	 * <br/>
	 * Note: Don't construct a new instance everytime your <tt>View.onMeasure()</tt> method
	 * is called.<br />
	 * Instead, create one instance when your <tt>View</tt> is constructed, and
	 * use this instance's <tt>measure()</tt> methods in the <tt>onMeasure()</tt> method.
	 * @param aspectRatio
	 */
	public ViewAspectRatioMeasurer(double aspectRatio) {
		this.aspectRatio = aspectRatio; 
	}
	
	/**
	 * Measure with the aspect ratio given at construction.<br />
	 * <br />
	 * After measuring, get the width and height with the {@link #getMeasuredWidth()}
	 * and {@link #getMeasuredHeight()} methods, respectively.
	 * @param widthMeasureSpec The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
	 * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
	 */
	public void measure(int widthMeasureSpec, int heightMeasureSpec) {
		measure(widthMeasureSpec, heightMeasureSpec, this.aspectRatio);
	}

	/**
	 * Measure with a specific aspect ratio<br />
	 * <br />
	 * After measuring, get the width and height with the {@link #getMeasuredWidth()}
	 * and {@link #getMeasuredHeight()} methods, respectively.
	 * @param widthMeasureSpec The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
	 * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
	 * @param aspectRatio The aspect ratio to calculate measurements in respect to 
	 */
	public void measure(int widthMeasureSpec, int heightMeasureSpec, double aspectRatio) {
		int widthMode = MeasureSpec.getMode( widthMeasureSpec );
		int widthSize = widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize( widthMeasureSpec );
		int heightMode = MeasureSpec.getMode( heightMeasureSpec );
		int heightSize = heightMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize( heightMeasureSpec );
		
		if ( heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY ) {
			/* 
			 * Possibility 1: Both width and height fixed
			 */
			measuredWidth = widthSize;
			measuredHeight = heightSize;
			
		} else if ( heightMode == MeasureSpec.EXACTLY ) {
			/*
			 * Possibility 2: Width dynamic, height fixed
			 */
			measuredHeight = (int) Math.min( heightSize, widthSize / aspectRatio );
			measuredWidth = (int) (measuredHeight * aspectRatio);
			
		} else if ( widthMode == MeasureSpec.EXACTLY ) {
			/*
			 * Possibility 3: Width fixed, height dynamic
			 */
			measuredWidth = (int) Math.min( widthSize, heightSize * aspectRatio );
			measuredHeight = (int) (measuredWidth / aspectRatio);
			
		} else {
			/* 
			 * Possibility 4: Both width and height dynamic
			 */
			if ( widthSize > heightSize * aspectRatio ) {
				measuredHeight = heightSize;
				measuredWidth = (int)( measuredHeight * aspectRatio );
			} else {
				measuredWidth = widthSize;
				measuredHeight = (int) (measuredWidth / aspectRatio);
			}
			
		}
	}
	
	private Integer measuredWidth = null;
	/**
	 * Get the width measured in the latest call to <tt>measure()</tt>.
	 */
	public int getMeasuredWidth() {
		if ( measuredWidth == null ) {
			throw new IllegalStateException( "You need to run measure() before trying to get measured dimensions" );
		}
		return measuredWidth;
	}

	private Integer measuredHeight = null;
	/**
	 * Get the height measured in the latest call to <tt>measure()</tt>.
	 */
	public int getMeasuredHeight() {
		if ( measuredHeight == null ) {
			throw new IllegalStateException( "You need to run measure() before trying to get measured dimensions" );
		}
		return measuredHeight;
	}
	
}
