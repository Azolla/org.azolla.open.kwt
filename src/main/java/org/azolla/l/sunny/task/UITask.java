/*
 * @(#)UITask.java		Created at 2013-6-28
 * 
 * Copyright (c) 2011-2013 azolla.org All rights reserved.
 * Azolla PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
package org.azolla.l.sunny.task;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 
 * UI Task (non-progress)
 *
 * @param 	<T>	Type of task result
 * @author 	sk@azolla.org
 * @since 	ADK1.0
 */
public class UITask<T> implements InvocationHandler, Callable<T>
{
    private static final Logger   LOG                   = LoggerFactory.getLogger(UITask.class);
    private static final int      DIALOG_MIN_WIDTH      = 350;
    private static final String   FLUSH_EVENT_METHOD    = "flushPendingEvents";
    private static final String   SUN_TOOLKIT_CLASS     = "sun.awt.SunToolkit";
    private static final String   PUMP_EVENTS_METHOD    = "pumpEvents";
    private static final String   AWT_EDT_CLASS         = "java.awt.EventDispatchThread";
    private static final String   AWT_CONDITIONAL_CLASS = "java.awt.Conditional";
    private static final int      SHOW_PROGRESS_DELAY   = 2000;
    private static Class<?>       conditionalClass;
    private static Method         pumpEventsMethod;
    private static Class<?>       sunToolkitClass;
    private static Method         flushEventMethod;
    private static String         defaultMessage        = "Please wait...";
    private String                cancelText            = "Cancel";

    private static ImageIcon      cartoonIcon           = new ImageIcon(UITask.class.getResource("loadingprogress.gif"));
    private SwingWorker<T, Void>  worker;
    private final List<Component> glassList             = Lists.newArrayList();
    private Timer                 timer;
    private JDialog               dialog;
    private JLabel                message;
    private JProgressBar          progress;
    private final Callable<T>     callable;
    private String                title;
    private String                messageText           = defaultMessage;
    private int                   progressValue         = -1;
    private Window                ownerWindow           = null;

    private final Action          cancelAction          = new AbstractAction(cancelText)
                                                        {
                                                            private static final long serialVersionUID = 8989611247581396162L;

                                                            @Override
                                                            public void actionPerformed(ActionEvent event)
                                                            {
                                                                cancel();
                                                            }
                                                        };
    private boolean               cancelable;

    /**JDK7+*/
    private static final String   PUSH_POP_LOCK_FIELD   = "pushPopLock";
    private static final String   PUSH_POP_COND_FIELD   = "pushPopCond";
    private static Field          pushPopLockField;
    private static Field          pushPopCondField;
    private static boolean        isJre7Plus            = true;

    public static void setDefaultMessage(String message)
    {
        defaultMessage = message;
    }

    public void setCancelText(String text)
    {
        cancelText = text;
        cancelAction.putValue("Name", cancelText);
    }

    public UITask()
    {
        callable = this;
        cancelAction.setEnabled(false);
    }

    public UITask(Window window)
    {
        callable = this;
        cancelAction.setEnabled(false);
        ownerWindow = window;
    }

    public UITask(Callable<T> callable)
    {
        this.callable = callable;
        cancelAction.setEnabled(false);
    }

    public UITask(Callable<T> callable, Window window)
    {
        this.callable = callable;
        cancelAction.setEnabled(false);
        ownerWindow = window;
    }

    public void setCancelable(final boolean cancelable)
    {
        this.cancelable = cancelable;
        invokeUI(new Runnable()
        {
            @Override
            public void run()
            {
                cancelAction.setEnabled(cancelable);
            }
        });
    }

    public void setTitle(final String title)
    {
        invokeUI(new Runnable()
        {
            @Override
            public void run()
            {
                UITask.this.title = title;
                if(dialog != null)
                {
                    dialog.setTitle(title);
                }
            }
        });
    }

    public void setMessage(final String text)
    {
        invokeUI(new Runnable()
        {
            @Override
            public void run()
            {
                messageText = text;
                if(message != null)
                {
                    message.setText(text);
                }
            }
        });
    }

    public void setProgress(final int value)
    {
        invokeUI(new Runnable()
        {
            @Override
            public void run()
            {
                progressValue = value;
                if(progress != null)
                {
                    if(progressValue < 0)
                    {
                        progress.setIndeterminate(true);
                    }
                    else
                    {
                        progress.setIndeterminate(false);
                        progress.setValue(progressValue);
                    }
                }

            }
        });
    }

    protected void invokeUI(Runnable runnable)
    {
        if(SwingUtilities.isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            SwingUtilities.invokeLater(runnable);
        }
    }

    @Override
    public T call() throws Exception
    {
        return null;
    }

    public T execute() throws Exception
    {
        if(!(SwingUtilities.isEventDispatchThread()))
        {
            return callable.call();
        }

        try
        {
            worker = new SwingWorker<T, Void>()
            {
                @Override
                protected T doInBackground() throws Exception
                {
                    return callable.call();
                }

                /**
                 * @see javax.swing.SwingWorker#done()
                 */
                @Override
                protected void done()
                {
                    stopWaitCursor();
                    closeProgress();
                }
            };
            worker.execute();

            resetFocus();

            startWaitCursor();

            timer = new Timer(SHOW_PROGRESS_DELAY, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    showProgress();
                }
            });
            timer.setRepeats(false);
            timer.start();

            pumpEvents();

            return worker.get();
        }
        catch(ExecutionException e)
        {
            return null;
        }
        finally
        {
            stopWaitCursor();
            closeProgress();
        }
    }

    public void closeProgress()
    {
        if(timer != null)
        {
            timer.stop();
            timer = null;
        }

        if(dialog != null)
        {
            dialog.dispose();
            dialog = null;
        }
    }

    public T executeSilently()
    {
        try
        {
            return execute();
        }
        catch(Exception t)
        {
            throw new RuntimeException(t);
        }
    }

    public boolean isCancelled()
    {
        if(worker != null)
        {
            return worker.isCancelled();
        }

        return false;
    }

    private void showProgress()
    {
        Window owner = getCurrentOwner();

        if(owner instanceof Dialog)
        {
            dialog = new JDialog((Dialog) owner);
        }
        else if(owner instanceof Frame)
        {
            dialog = new JDialog((Frame) owner);
        }
        else
        {
            dialog = new JDialog();
        }

        dialog.setTitle(title);
        dialog.setModal(false);
        dialog.setDefaultCloseOperation(0);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                cancel();
            }
        });
        dialog.getContentPane().setLayout(new BorderLayout());

        JLabel cartoon = new JLabel(cartoonIcon);

        JPanel msg = new JPanel(new FlowLayout(0, 10, 5));
        msg.add(cartoon);
        message = new JLabel(messageText);
        msg.add(message);
        dialog.getContentPane().add(msg, "North");

        JPanel center = new JPanel(new FlowLayout(0, 10, 5));
        center.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        progress = new JProgressBar(0, 100);
        setProgress(progressValue);
        progress.setPreferredSize(new Dimension(330, 21));
        center.add(progress);
        if(cancelAction.isEnabled())
        {
            progress.setPreferredSize(new Dimension(240, 21));
            JButton cancellBtn = new JButton(cancelAction);
            cancellBtn.setPreferredSize(new Dimension(80, 21));
            center.add(cancellBtn);
            cancellBtn.setVisible(cancellBtn.isEnabled());

            cancellBtn.setCursor(Cursor.getDefaultCursor());
        }

        dialog.getContentPane().add(center, "Center");
        dialog.setCursor(Cursor.getPredefinedCursor(3));
        dialog.setCursor(Cursor.getDefaultCursor());

        dialog.setResizable(false);
        dialog.pack();

        Dimension size = dialog.getPreferredSize();
        if(size.width < DIALOG_MIN_WIDTH)
        {
            size.width = DIALOG_MIN_WIDTH;
            dialog.setSize(size);
        }

        dialog.setLocationRelativeTo(owner);

        dialog.setVisible(true);
    }

    private Window getCurrentOwner()
    {
        if(ownerWindow != null)
        {
            return ownerWindow;
        }

        Window[] wins = getWindows();

        for(Window w : wins)
        {
            if(w.isActive())
            {
                return w;
            }

        }

        return Frame.getFrames()[0];
    }

    private void cancel()
    {
        if(cancelable)
        {
            worker.cancel(true);
            closeProgress();
        }
    }

    private void startWaitCursor()
    {
        Window[] windows = getWindows();

        for(Window window : windows)
        {
            if(window instanceof RootPaneContainer)
            {
                Component glass = ((RootPaneContainer) window).getGlassPane();
                glass.setCursor(Cursor.getPredefinedCursor(3));
                glass.setVisible(true);
                glassList.add(glass);
            }
        }
    }

    private void stopWaitCursor()
    {
        for(Component glass : glassList)
        {
            glass.setCursor(Cursor.getDefaultCursor());
            glass.setVisible(false);
        }

        glassList.clear();
    }

    private void pumpEvents()
    {
        try
        {
            Object conditional = Proxy.newProxyInstance(conditionalClass.getClassLoader(), new Class[] {conditionalClass}, this);

            pumpEventsMethod.invoke(Thread.currentThread(), new Object[] {conditional});
        }
        catch(Exception e)
        {
            LOG.warn("", e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        while(!worker.isDone())
        {
            AWTEvent event = waitForEvent();
            if(event == null)
            {
                return Boolean.valueOf(false);
            }

            if(acceptEvent(event))
            {
                return Boolean.valueOf(true);
            }

            if(getNextEvent() == null)
            {
                return Boolean.valueOf(false);
            }
        }

        return Boolean.valueOf(false);
    }

    private boolean acceptEvent(AWTEvent event)
    {
        if((event.getID() == 401) && (event instanceof KeyEvent))
        {
            KeyEvent keyEvent = (KeyEvent) event;
            if((keyEvent.getKeyCode() == 3) && (keyEvent.isControlDown()))
            {
                cancel();
                return true;
            }

        }

        if((dialog != null) && (!(dialog.isActive())))
        {
            dialog.requestFocus();
        }

        if((dialog != null) && (event.getSource() == dialog))
        {
            return true;
        }

        if(event instanceof InputEvent)
        {
            return false;
        }

        return(event.getID() != 201);
    }

    private void resetFocus()
    {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        Component focus = focusManager.getPermanentFocusOwner();
        if(focus != null)
        {
            focus.requestFocus();
        }
    }

    private EventQueue getEventQueue()
    {
        return Toolkit.getDefaultToolkit().getSystemEventQueue();
    }

    private AWTEvent getNextEvent()
    {
        try
        {
            return getEventQueue().getNextEvent();
        }
        catch(InterruptedException x)
        {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private AWTEvent waitForEvent()
    {
        EventQueue queue = getEventQueue();
        AWTEvent nextEvent = null;

        while((nextEvent = peekEvent(queue)) == null)
        {
            try
            {
                if(isJre7Plus)
                {
                    boolean again = true;
                    while(again)
                    {
                        Lock pushPopLock = null;
                        Condition pushPopCond = null;
                        try
                        {
                            pushPopLock = Lock.class.cast(pushPopLockField.get(queue));
                            pushPopCond = Condition.class.cast(pushPopCondField.get(queue));
                            again = false;
                        }
                        catch(Exception e)
                        {
                            again = true;
                        }

                        if(!again && null != pushPopLock && null != pushPopCond)
                        {
                            pushPopLock.lock();
                            try
                            {
                                pushPopCond.await();
                            }
                            finally
                            {
                                pushPopLock.unlock();
                            }
                        }
                    }
                }
                else
                {
                    synchronized(queue)
                    {
                        queue.wait();
                    }
                }

            }
            catch(InterruptedException x)
            {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        return nextEvent;
    }

    private AWTEvent peekEvent(EventQueue queue)
    {
        flushPendingEvents();
        return queue.peekEvent();
    }

    private void flushPendingEvents()
    {
        try
        {
            flushEventMethod.invoke(sunToolkitClass, new Object[0]);
        }
        catch(Exception e)
        {
            LOG.error("", e);
        }
    }

    private static Window[] getWindows()
    {
        List<Window> windows = Lists.newArrayList();
        Frame[] frames = Frame.getFrames();

        for(Frame frame : frames)
        {
            addWindow(frame, windows);
        }

        Window[] windowArray = new Window[windows.size()];
        windows.toArray(windowArray);
        return windowArray;
    }

    private static void addWindow(Window owner, List<Window> windows)
    {
        windows.add(owner);
        Window[] owned = owner.getOwnedWindows();
        for(Window w : owned)
        {
            addWindow(w, windows);
        }
    }

    static
    {
        try
        {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            conditionalClass = loader.loadClass(AWT_CONDITIONAL_CLASS);
            Class<?> edtClass = loader.loadClass(AWT_EDT_CLASS);
            pumpEventsMethod = edtClass.getDeclaredMethod(PUMP_EVENTS_METHOD, new Class[] {conditionalClass});

            pumpEventsMethod.setAccessible(true);

            sunToolkitClass = loader.loadClass(SUN_TOOLKIT_CLASS);
            flushEventMethod = sunToolkitClass.getMethod(FLUSH_EVENT_METHOD, new Class[0]);
            try
            {
                pushPopLockField = EventQueue.class.getDeclaredField(PUSH_POP_LOCK_FIELD);
                pushPopCondField = EventQueue.class.getDeclaredField(PUSH_POP_COND_FIELD);
                pushPopLockField.setAccessible(true);
                pushPopCondField.setAccessible(true);
            }
            catch(NoSuchFieldException ex)
            {
                isJre7Plus = false;
            }
        }
        catch(Exception e)
        {
            LOG.error(e.toString(), e);
            try
            {
                Thread.sleep(2000L);
            }
            catch(InterruptedException e1)
            {
            }
            System.exit(-1);
        }
    }
}
