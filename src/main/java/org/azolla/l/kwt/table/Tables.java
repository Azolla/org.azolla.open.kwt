/*
 * @(#)Tables.java		Created at 2013-5-4
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.l.kwt.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

/**
 * Table operations collect
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public final class Tables
{
    public static void hideColumn(JTable table, int column)
    {
        if(column < 0 || column > table.getColumnCount())
        {
            return;
        }
        TableColumn headColumn = table.getTableHeader().getColumnModel().getColumn(column);
        headColumn.setWidth(0);
        headColumn.setPreferredWidth(0);
        headColumn.setMaxWidth(0);
        headColumn.setMinWidth(0);

        TableColumn dataColumn = table.getColumnModel().getColumn(column);
        dataColumn.setWidth(0);
        dataColumn.setPreferredWidth(0);
        dataColumn.setMaxWidth(0);
        dataColumn.setMinWidth(0);
    }

    public static void fitToContent(JTable table)
    {
        fitToContentWithMargin(table, 0);
    }

    public static void fitToContentWithMargin(JTable table, int margin)
    {
        TableColumnModel colModel = table.getColumnModel();
        Enumeration<TableColumn> columns = colModel.getColumns();
        while(columns.hasMoreElements())
        {
            TableColumn column = columns.nextElement();

            fitToContentWithMargin(table, colModel.getColumnIndex(column.getIdentifier()), margin);
        }
    }

    public static void fitToContent(JTable table, int col)
    {
        fitToContentWithMargin(table, col, 0);
    }

    public static void fitToContentWithMargin(JTable table, int col, int margin)
    {
        int fitWidth = 0;

        JTableHeader header = table.getTableHeader();
        TableColumn column = table.getColumnModel().getColumn(col);

        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if(null == headerRenderer)
        {
            headerRenderer = header.getDefaultRenderer();
        }
        Component headerComp = headerRenderer.getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col);

        int headerWidth = headerComp.getPreferredSize().width;
        fitWidth = Math.max(fitWidth, headerWidth);

        int row = 0;
        for(int end = table.getRowCount(); row < end; row++)
        {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
            Component cellComp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col);

            int cellWidth = cellComp.getPreferredSize().width;
            fitWidth = Math.max(fitWidth, cellWidth);
        }

        fitWidth += table.getIntercellSpacing().width;
        fitWidth += margin;

        column.setPreferredWidth(fitWidth);
    }

    public static void enableTerminateEditOnFocusLost(JTable table)
    {
        setTerminateEditOnFocusLost(table, Boolean.TRUE.booleanValue());
    }

    public static void setTerminateEditOnFocusLost(JTable table, boolean yon)
    {
        table.putClientProperty("terminateEditOnFocusLost", yon ? Boolean.TRUE : Boolean.FALSE);
    }

    public static boolean isTerminateEditOnFocusLost(JTable table)
    {
        return Boolean.TRUE.equals(table.getClientProperty("terminateEditOnFocusLost"));
    }

    public static void terminateCellEditing(JTable table)
    {
        int editingRow = table.getEditingRow();
        int editingCol = table.getEditingColumn();
        if(!cellInBound(table, editingRow, editingCol))
        {
            return;
        }

        TableCellEditor cellEditor = table.getCellEditor();
        if(null == cellEditor)
        {
            return;
        }

        if(cellEditor.stopCellEditing())
        {
            return;
        }
        cellEditor.cancelCellEditing();
    }

    public static void cancelCellEditing(JTable table)
    {
        TableCellEditor cellEditor = table.getCellEditor();
        if(null != cellEditor)
        {
            cellEditor.cancelCellEditing();
        }
    }

    public static void selectRowAndScrollToVisible(JTable table, int row)
    {
        selectRow(table, row);
        if(table.isRowSelected(row))
        {
            scrollRowToVisible(table, row);
        }
    }

    public static void selectRow(JTable table, int row)
    {
        if(rowInBound(table, row))
        {
            table.setRowSelectionInterval(row, row);
        }
    }

    public static void scrollRowToVisible(JTable table, int row)
    {
        if(rowInBound(table, row))
        {
            Rectangle rect = table.getCellRect(row, 0, true);

            rect.x = table.getVisibleRect().x;
            table.scrollRectToVisible(rect);
        }
    }

    public static void scrollColumnToVisible(JTable table, int col)
    {
        if(columnInBound(table, col))
        {
            Rectangle rect = table.getCellRect(0, col, true);

            rect.y = table.getVisibleRect().y;
            table.scrollRectToVisible(rect);
        }
    }

    public static void scrollCellToVisible(JTable table, int row, int col)
    {
        if(cellInBound(table, row, col))
        {
            table.scrollRectToVisible(table.getCellRect(row, col, true));
        }
    }

    public static Object getValueAt(JTable table, MouseEvent evt)
    {
        return getValueAt(table, evt.getPoint());
    }

    public static Object getValueAt(JTable table, Point pos)
    {
        int row = table.rowAtPoint(pos);
        int col = table.columnAtPoint(pos);

        if(cellInBound(table, row, col))
        {
            return table.getValueAt(row, col);
        }

        return null;
    }

    public static Object getFocusedValue(JTable table)
    {
        Object cell = null;
        try
        {
            cell = table.getValueAt(table.getSelectionModel().getLeadSelectionIndex(), table.getColumnModel().getSelectionModel().getLeadSelectionIndex());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return cell;
    }

    public static boolean rowInBound(JTable table, int row)
    {
        return (0 <= row) && (table.getRowCount() > row);
    }

    public static boolean columnInBound(JTable table, int col)
    {
        return (0 <= col) && (table.getColumnCount() > col);
    }

    public static boolean cellInBound(JTable table, int row, int col)
    {
        return (rowInBound(table, row)) && (columnInBound(table, col));
    }

    public static boolean isTableChangedEntirely(JTable table, TableModelEvent evt)
    {
        return (0 == evt.getType()) && (-1 == evt.getColumn()) && (0 == evt.getFirstRow()) && ((table.getRowCount() - 1 == evt.getLastRow()) || (2147483647 == evt.getLastRow()));
    }

    public static List<Object> getAllIdentifiers(JTable table)
    {
        TableColumnModel model = table.getColumnModel();
        int count = table.getColumnCount();
        List<Object> identifiers = Lists.newArrayListWithCapacity(count);
        for(int index = 0; index < count; index++)
        {
            TableColumn col = model.getColumn(index);
            identifiers.add(col.getIdentifier());
        }

        return identifiers;
    }

    public static List<Action> defaultCopyToClipboardActions(JTable table)
    {
        List<Action> actions = Lists.newArrayListWithCapacity(5);
        actions.add(new CopyCellAction(table));
        actions.add(new CopyRowAction(table));
        actions.add(new CopyColumnAction(table));
        actions.add(new CopyTableAction(table));

        return actions;
    }

    private static class CopyTableAction extends AbstractAction
    {
        private static final long serialVersionUID = -587598878625749292L;
        private final JTable      table;

        public CopyTableAction(JTable table)
        {
            super();
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            StringBuilder builder = new StringBuilder();
            for(int row = 0; row < table.getRowCount(); row++)
            {
                for(int col = 0; col < table.getColumnCount(); col++)
                {
                    Object val = MoreObjects.firstNonNull(table.getValueAt(row, col), "");
                    builder.append(String.valueOf(val));
                    builder.append('\t');
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append('\n');
            }
            builder.deleteCharAt(builder.length() - 1);

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
        }

        @Override
        public boolean isEnabled()
        {
            return 0 < table.getRowCount();
        }
    }

    private static class CopyColumnAction extends AbstractAction
    {
        private static final long serialVersionUID = 3331577231220953025L;
        private final JTable      table;

        public CopyColumnAction(JTable table)
        {
            super();
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            StringBuilder builder = new StringBuilder();
            int[] cols = table.getSelectedColumns();
            int row = 0;
            for(int end = table.getRowCount(); row < end; row++)
            {
                for(int col : cols)
                {
                    Object val = MoreObjects.firstNonNull(table.getValueAt(row, col), "");
                    builder.append(String.valueOf(val));
                    builder.append('\t');
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append('\n');
            }
            builder.deleteCharAt(builder.length() - 1);

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
        }

        @Override
        public boolean isEnabled()
        {
            return 0 < table.getSelectedColumnCount();
        }
    }

    private static class CopyRowAction extends AbstractAction
    {
        private static final long serialVersionUID = 6159926541502989555L;
        private final JTable      table;

        public CopyRowAction(JTable table)
        {
            super();
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            StringBuilder builder = new StringBuilder();
            int[] rows = table.getSelectedRows();
            for(int row : rows)
            {
                int col = 0;
                for(int end = table.getColumnCount(); col < end; col++)
                {
                    Object val = MoreObjects.firstNonNull(table.getValueAt(row, col), "");
                    builder.append(String.valueOf(val));
                    builder.append('\t');
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append('\n');
            }
            builder.deleteCharAt(builder.length() - 1);

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
        }

        @Override
        public boolean isEnabled()
        {
            return 0 < table.getSelectedRowCount();
        }
    }

    private static class CopyCellAction extends AbstractAction
    {
        private static final long serialVersionUID = 1221144650126973340L;
        private final JTable      table;

        public CopyCellAction(JTable table)
        {
            super();
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            int row = table.getSelectionModel().getLeadSelectionIndex();
            int col = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            Object val = MoreObjects.firstNonNull(table.getValueAt(row, col), "");

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.valueOf(val)), null);
        }

        @Override
        public boolean isEnabled()
        {
            return (0 <= table.getSelectionModel().getLeadSelectionIndex()) && (0 <= table.getColumnModel().getSelectionModel().getLeadSelectionIndex());
        }
    }
}
