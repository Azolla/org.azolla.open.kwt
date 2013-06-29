/*
 * @(#)GBC.java		Created at 2013-5-4
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.open.kwt.layout;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * via <a href='http://shaneking.org/?p=836'>java.awt.GridBagConstraints</a>
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public class GBC extends GridBagConstraints
{
	private static final long	serialVersionUID	= 1592514305499234713L;

	/**
	 * gridx,gridy：设置组件的起始单元格位置
	 * gridx设置为GridBagConstraints.RELATIVE代表此组件位于之前所加入组件的右边。
	 * gridy设置为GridBagConstraints.RELATIVE代表此组件位于以前所加入组件的下面。
	 */
	public GBC(int gridx, int gridy)
	{
		this.gridx = gridx;
		this.gridy = gridy;
	}

	/**
	 * gridx,gridy：设置组件的位置
	 * gridx设置为GridBagConstraints.RELATIVE代表此组件位于之前所加入组件的右边。
	 * gridy设置为GridBagConstraints.RELATIVE代表此组件位于以前所加入组件的下面。
	 * 
	 * gridwidth,gridheight:用来设置组件所占的单元格数，默认值皆为1。
	 * 可以使用GridBagConstraints.REMAINDER常量，代表此组件为此行或此列的最后一个组件，而且会占据所有剩余的空间。
	 */
	public GBC(int gridx, int gridy, int gridwidth, int gridheight)
	{
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
	}

	/**
	 * @see org.azolla.open.kwt.layout.GBC#GBC(int, int)
	 */
	public static GBC grid(int gridx, int gridy)
	{
		return new GBC(gridx, gridy);
	}

	/**
	 * @see org.azolla.open.kwt.layout.GBC#GBC(int, int, int, int)
	 */
	public static GBC grid(int gridx, int gridy, int gridwidth, int gridheight)
	{
		return new GBC(gridx, gridy, gridwidth, gridheight);
	}

	/**
	 * weightx,weighty:用来设置窗口变大时，各组件跟着变大的比例。
	 * 当数字越大，表示组件能得到更多的空间，默认值皆为0。
	 */
	public GBC weight(double weightx, double weighty)
	{
		this.weightx = weightx;
		this.weighty = weighty;

		return this;
	}

	/**
	 * @see org.azolla.open.kwt.layout.GBC#insets(int, int, int, int)
	 */
	public GBC insets(int distance)
	{
		return insets(distance, distance, distance, distance);
	}

	/**
	 * insets:此字段指定组件的外部填充，即组件与其显示区域边缘之间间距的最小量。
	 * 它有四个参数，分别是上，左，下，右，默认为(0,0,0,0).
	 */
	public GBC insets(int top, int left, int bottom, int right)
	{
		this.insets = new Insets(top, left, bottom, right);

		return this;
	}

	/**
	 * ipadx,ipady:此字段指定组件的内部填充，即给组件的最小宽度/高度添加多大的空间。组件的宽度/高度至少为其最小宽度/高度加上 ipadx/ipady 像素。
	 * 默认值为0。
	 */
	public GBC ipad(int ipadx, int ipady)
	{
		this.ipadx = ipadx;
		this.ipady = ipady;

		return this;
	}

	/**
	 * 当组件小于其显示区域时使用此字段。它可以确定在显示区域中放置组件的位置。
	 * 
	 * 绝对值有：			CENTER、NORTH、NORTHEAST、EAST、SOUTHEAST、SOUTH、SOUTHWEST、WEST 和 NORTHWEST。
	 * 方向相对值有：		PAGE_START、PAGE_END、LINE_START、LINE_END、FIRST_LINE_START、FIRST_LINE_END、LAST_LINE_START
	 * 相对于基线的值有：	BASELINE、BASELINE_LEADING、BASELINE_TRAILING、
	 * 					ABOVE_BASELINE、ABOVE_BASELINE_LEADING、ABOVE_BASELINE_TRAILING、
	 * 					BELOW_BASELINE、BELOW_BASELINE_LEADING 和 BELOW_BASELINE_TRAILING。
	 * 默认值为 CENTER。
	 */
	public GBC anchor(int anchor)
	{
		this.anchor = anchor;

		return this;
	}

	/**
	 * 当组件的显示区域大于它所请求的显示区域的大小时使用此字段。它可以确定是否调整组件大小，以及在需要的时候如何进行调整。
	 * 
	 * NONE：不调整组件大小。
	 * HORIZONTAL：加宽组件，使它在水平方向上填满其显示区域，但是不改变高度。
	 * VERTICAL：加高组件，使它在垂直方向上填满其显示区域，但是不改变宽度。
	 * BOTH：使组件完全填满其显示区域。
	 * 
	 * 默认值为 NONE。
	 */
	public GBC fill(int fill)
	{
		this.fill = fill;

		return this;
	}
}
