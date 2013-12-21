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
    private static final long serialVersionUID = 1592514305499234713L;

    /**
     * @see org.azolla.open.kwt.layout.GBC#GBC(int, int, int, int)
     */
    private GBC(int gridx, int gridy)
    {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    /**
    * @param gridx location, default 0, right GridBagConstraints.RELATIVE
    * @param gridy location, default 0, under GridBagConstraints.RELATIVE
    * @param gridwidth cell, default 1, last GridBagConstraints.REMAINDER, second last GridBagConstraints.RELATIVE
    * @param gridheight cell, default 1, last GridBagConstraints.REMAINDER, second last GridBagConstraints.RELATIVE
    */
    private GBC(int gridx, int gridy, int gridwidth, int gridheight)
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
     * @param weightx scale, default 0
     * @param weighty scale, default 0
     * @return GBC
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
     * margin
     * 
     * @param top
     * @param left
     * @param bottom
     * @param right
     * @return GBC
     */
    public GBC insets(int top, int left, int bottom, int right)
    {
        this.insets = new Insets(top, left, bottom, right);

        return this;
    }

    /**
     * padding
     * 
     * @param ipadx padding,default 0
     * @param ipady padding,default 0
     * @return GBC
     */
    public GBC ipad(int ipadx, int ipady)
    {
        this.ipadx = ipadx;
        this.ipady = ipady;

        return this;
    }

    /**
     * location of component small
     * 
     * @param anchor default CENTER,
     *              {Absolute:CENTER,NORTH,NORTHEAST,EAST,SOUTHEAST,SOUTH,SOUTHWEST,WEST,NORTHWEST}
     *              {Direction:PAGE_START,PAGE_END,LINE_START,LINE_END,FIRST_LINE_START,FIRST_LINE_END,LAST_LINE_START}
     *              {Baseline:BASELINE,BASELINE_LEADING,BASELINE_TRAILING,
     *                        ABOVE_BASELINE,ABOVE_BASELINE_LEADING,ABOVE_BASELINE_TRAILING,
     *                        BELOW_BASELINE,BELOW_BASELINE_LEADING,BELOW_BASELINE_TRAILING}
     * @return GBC
     */
    public GBC anchor(int anchor)
    {
        this.anchor = anchor;

        return this;
    }

    /**
     * fill of component small
     * 
     * @param fill default NONE,{NONE,HORIZONTAL,VERTICAL,BOTH}
     * @return GBC
     */
    public GBC fill(int fill)
    {
        this.fill = fill;

        return this;
    }
}
