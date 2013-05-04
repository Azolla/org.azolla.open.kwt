/*
 * @(#)Trees.java		Created at 2013-5-4
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.open.kwt.tree;

import java.util.Collections;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;

/**
 * Tree operations collect
 *
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public final class Trees
{
	public static void reload(JTree tree)
	{
		TreeModel model = tree.getModel();
		if((model instanceof DefaultTreeModel))
		{
			((DefaultTreeModel) model).reload();
		}
	}

	public static void reload(JTree tree, TreeNode treeNode)
	{
		TreeModel model = tree.getModel();
		if((model instanceof DefaultTreeModel))
		{
			((DefaultTreeModel) model).reload(treeNode);
		}
	}

	public static void expandTree(JTree tree)
	{
		expandPath(tree, tree.getPathForRow(0));
	}

	public static void expandPath(JTree tree, TreePath path)
	{
		tree.expandPath(path);
		Object comp = path.getLastPathComponent();
		if((comp instanceof TreeNode))
		{
			TreeNode node = (TreeNode) comp;
			for(int index = node.getChildCount() - 1; index >= 0; index--)
			{
				TreePath childPath = path.pathByAddingChild(node.getChildAt(index));
				expandPath(tree, childPath);
			}
		}
	}

	public static List<TreeNode> getSelectedLeafs(JTree tree)
	{
		TreePath[] selectedPaths = tree.getSelectionPaths();
		if(null == selectedPaths)
		{
			return Collections.emptyList();
		}

		List<TreeNode> selectedLeafs = Lists.newArrayList();
		for(TreePath selectedPath : selectedPaths)
		{
			Object comp = selectedPath.getLastPathComponent();
			if((comp instanceof TreeNode))
			{
				fillWithLeafsUnder(selectedLeafs, (TreeNode) comp);
			}
		}

		return selectedLeafs;
	}

	public static List<TreeNode> getLeafsUnder(TreeNode root)
	{
		List<TreeNode> leafs = Lists.newArrayList();
		fillWithLeafsUnder(leafs, root);

		return leafs;
	}

	public static void fillWithLeafsUnder(List<TreeNode> container, TreeNode root)
	{
		if(root.isLeaf())
		{
			container.add(root);
			return;
		}

		int index = 0;
		for(int end = root.getChildCount(); index < end; index++)
		{
			fillWithLeafsUnder(container, root.getChildAt(index));
		}
	}
}
