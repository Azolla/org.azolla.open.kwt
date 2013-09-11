/*
 * @(#)DragAndDropRowTableUI.java		Created at 2013-5-4
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.open.kwt.plaf.basic;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.azolla.open.kwt.table.Tables;

/**
 * Drag single row up or down
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public class DragAndDropRowTableUI extends BasicTableUI
{
	private boolean	draging;
	private Point	point;
	private int		offset;
	private boolean	direction;

	public DragAndDropRowTableUI()
	{
		this.draging = false;
		this.direction = true;
	}

	@Override
	public void paint(Graphics g, JComponent c)
	{
		super.paint(g, c);
		if(!(draging))
		{
			return;
		}
		g.setColor(table.getParent().getBackground());
		Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, false);
		int width = table.getWidth();
		int height = table.getRowHeight();
		if(direction)
		{
			g.copyArea(rect.x, rect.y, width, height, rect.x, offset * -1);

			g.fillRect(rect.x, rect.y + height - offset, width, offset);
		}
		else
		{
			g.copyArea(rect.x, rect.y, width, height, rect.x, offset);

			g.fillRect(rect.x, rect.y, width, offset * -1);
		}
	}

	@Override
	protected MouseInputListener createMouseInputListener()
	{
		return new BasicTableUI.MouseInputHandler()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				super.mousePressed(e);
				point = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				TableCellEditor cellEditor = table.getCellEditor();
				if(null != cellEditor)
				{
					cellEditor.stopCellEditing();
				}
				int fromRow = table.getSelectedRow();
				if(!(Tables.rowInBound(table, fromRow)))
				{
					return;
				}
				draging = true;
				int toRow = getDestRow(e.getPoint().y, fromRow);
				if(Tables.rowInBound(table, toRow))
				{
					changeValue(fromRow, toRow);
					point = e.getPoint();
				}
				offset = Math.abs(point.y - e.getPoint().y);
				direction = point.y - e.getPoint().y > 0;
				table.repaint();
			}

			private int getDestRow(int y, int row)
			{
				int height = table.getRowHeight();
				int middle = height * row + height / 2;
				if(y > middle + height)
				{
					return(++row);
				}
				if(y < middle - height)
				{
					return(--row);
				}
				return -1;
			}

			private void changeValue(int fromRow, int toRow)
			{
				TableModel model = table.getModel();
				int i = 0;
				for(int count = model.getColumnCount(); i < count; ++i)
				{
					Object sourceValue = model.getValueAt(fromRow, i);
					Object destValue = model.getValueAt(toRow, i);
					model.setValueAt(destValue, fromRow, i);
					model.setValueAt(sourceValue, toRow, i);
				}
				table.setRowSelectionInterval(toRow, toRow);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				super.mouseReleased(e);
				draging = false;
				table.repaint();
			}
		};
	}
}
