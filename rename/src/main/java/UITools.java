import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class UITools {

    private final JFrame jFrame;
    private final JComponent pane;
    private final JScrollBar jScrollBar;

    public UITools() {
        jFrame = new JFrame();
        pane = Box.createVerticalBox();
        jFrame.setBounds(100, 100, 800, 500);
        jFrame.setResizable(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        JScrollPane sp = new JScrollPane(pane);
        sp.setAutoscrolls(true);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollBar = sp.getVerticalScrollBar();
        jFrame.add(sp);
    }

    public void close() {
        System.exit(0);
    }

    public void addTxt(String txt) {
        JTextField tv = new JTextField(txt);
        tv.setEditable(false);
        tv.setBorder(new EmptyBorder(8, 0, 8, 0));
        pane.add(tv);
    }

    public void addEt(final EtListener listener) {
        final JTextField et = new JTextField();
        et.setPreferredSize(new Dimension(500, 80));
        et.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    // 移除
                    et.removeKeyListener(this);
                    pane.remove(et);
                    // 运行监听
                    listener.onInput(et.getText().trim());
                }
            }
        });
        pane.add(et);
        et.requestFocus();
        // 刷新页面
        jFrame.revalidate();
        jScrollBar.setValue(jScrollBar.getMaximum());
    }

}
