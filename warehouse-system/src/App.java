import ui.MainFrame;

import javax.swing.*;

/**
 * 仓库管理系统 - 程序入口
 *
 * 期末考核《Java程序设计》课程项目
 * 功能：商品管理、每日出入库记录、月度财务（房租水电）统计
 */
public class App {
    public static void main(String[] args) {
        // 设置 Swing 外观为系统原生风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 如果设置失败，使用默认风格
        }

        // 在事件调度线程中启动GUI，确保线程安全
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
