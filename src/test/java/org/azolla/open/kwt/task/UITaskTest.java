/*
 * @(#)UITaskTest.java		Created at 2013-6-28
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.open.kwt.task;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The coder is very lazy, nothing to write for this UITaskTest class
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public class UITaskTest
{

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				//do nothing
				final JFrame jframe = new JFrame("Test");
				jframe.setSize(521, 520);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jframe.setLayout(new BorderLayout());

				JButton jbutton1 = new JButton("TestButton");
				jbutton1.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						UITask<Boolean> task = new UITask<Boolean>(new Callable<Boolean>()
						{

							@Override
							public Boolean call() throws Exception
							{
								TimeUnit.SECONDS.sleep(30);
								return true;
							}
						}, jframe);
						try
						{
							task.execute();
						}
						catch(Exception e1)
						{
							e1.printStackTrace();
						}
					}
				});
				jframe.add(jbutton1, BorderLayout.WEST);

				JButton jbutton2 = new JButton("TestButton");
				jbutton2.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						UITask<Boolean> task = new UITask<Boolean>(new Callable<Boolean>()
						{

							@Override
							public Boolean call() throws Exception
							{
								TimeUnit.SECONDS.sleep(30);
								return true;
							}
						}, jframe);
						try
						{
							task.execute();
						}
						catch(Exception e1)
						{
							e1.printStackTrace();
						}
					}
				});
				jframe.add(jbutton2, BorderLayout.EAST);

				jframe.setVisible(true);
			}
		});
	}
}
