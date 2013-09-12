/*
 * @(#)Tasks.java		Created at 2013-9-12
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.open.kwt.task;

import java.awt.Window;
import java.util.concurrent.Callable;

/**
 * The coder is very lazy, nothing to write for this Tasks class
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public class UITasks
{
	public static <T> UITask<T> createUITask(Window window, Callable<T> callable)
	{
		return new UITask<T>(callable, window);
	}

	public static <T> UITask<T> createUITask(Window window, String message, Callable<T> callable)
	{
		UITask<T> uiTask = new UITask<T>(callable, window);
		uiTask.setMessage(message);
		return uiTask;
	}

	public static <T> UITask<T> createUITask(Window window, String title, String message, Callable<T> callable)
	{
		UITask<T> uiTask = new UITask<T>(callable, window);
		uiTask.setTitle(title);
		uiTask.setMessage(message);
		return uiTask;
	}
}
