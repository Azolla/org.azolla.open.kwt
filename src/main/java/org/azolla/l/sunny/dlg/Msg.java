/*
 * @(#)Msg.java		Created at 15/9/3
 * 
 * Copyright (c) azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.l.sunny.dlg;

import org.azolla.l.ling.net.Url0;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * The coder is very lazy, nothing to write for this class
 *
 * @author sk@azolla.org
 * @since ADK1.0
 */
public class Msg
{
    private static Icon icon = new ImageIcon(Url0.getURL("img/favicon.png"));

    public static void info(Component parentComponent, Object message)
    {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        info(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle", l));
    }

    public static void info(Component parentComponent, Object message, String title)
    {
        info(parentComponent, message, title, icon);
    }

    public static void info(Component parentComponent, Object message, String title, Icon icon)
    {
        msg(parentComponent,message,title,icon,JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parentComponent, Object message)
    {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        error(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle", l));
    }

    public static void error(Component parentComponent, Object message, String title)
    {
        error(parentComponent, message, title, icon);
    }

    public static void error(Component parentComponent, Object message, String title, Icon icon)
    {
        msg(parentComponent,message,title,icon,JOptionPane.ERROR_MESSAGE);
    }

    public static void warn(Component parentComponent, Object message)
    {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        warn(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle", l));
    }

    public static void warn(Component parentComponent, Object message, String title)
    {
        warn(parentComponent, message, title, icon);
    }

    public static void warn(Component parentComponent, Object message, String title, Icon icon)
    {
        msg(parentComponent,message,title,icon,JOptionPane.WARNING_MESSAGE);
    }

    public static void msg(Component parentComponent, Object message, String title, Icon icon, int messageType)
    {
        JOptionPane.showMessageDialog(parentComponent,message,title,messageType,icon);
    }
}
