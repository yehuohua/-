package ui;

import dao.FinanceDAO;
import dao.ProductDAO;
import dao.StockRecordDAO;
import service.FinanceService;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * 仪表盘面板
 * 显示系统概览：今日出入库统计、库存总览、本月财务概况
 */
public class DashboardPanel extends JPanel {

    private final ProductDAO productDAO;
    private final StockRecordDAO stockRecordDAO;
    private final FinanceDAO financeDAO;
    private final StockService stockService;
    private final FinanceService financeService;

    // 数值标签引用
    private JLabel lblTotalProducts;
    private JLabel lblTotalStock;
    private JLabel lblInventoryValue;
    private JLabel lblTodayIn;
    private JLabel lblTodayOut;
    private JLabel lblMonthProfit;
    private JLabel lblMonthExpense;

    public DashboardPanel(ProductDAO productDAO, StockRecordDAO stockRecordDAO,
                          FinanceDAO financeDAO, StockService stockService,
                          FinanceService financeService) {
        this.productDAO = productDAO;
        this.stockRecordDAO = stockRecordDAO;
        this.financeDAO = financeDAO;
        this.stockService = stockService;
        this.financeService = financeService;
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 顶部标题
        JLabel lblTitle = new JLabel("仓库管理系统 - 仪表盘", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // 中部：2行×4列 统计卡片
        JPanel grid = new JPanel(new GridLayout(2, 4, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 第一行：基础统计
        grid.add(makeCard("📅 今日日期", LocalDate.now().toString()));
        lblTotalProducts = makeCardValue(grid, "📦 商品种类", "");
        lblTotalStock    = makeCardValue(grid, "🏗️ 总库存量", "");
        lblInventoryValue = makeCardValue(grid, "💎 库存总价值(进价)", "");

        // 第二行：出入库 + 财务
        lblTodayIn       = makeCardValue(grid, "📥 今日入库总量", "");
        lblTodayOut      = makeCardValue(grid, "📤 今日出库总量", "");
        lblMonthProfit   = makeCardValue(grid, "📈 本月毛利润", "");
        lblMonthExpense  = makeCardValue(grid, "💸 本月固定支出", "");

        add(grid, BorderLayout.CENTER);

        // 底部
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnRefresh = new JButton("🔄 刷新数据");
        btnRefresh.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        btnRefresh.addActionListener(e -> refreshData());
        bottomPanel.add(btnRefresh);

        JLabel lblHint = new JLabel("数据文件位于 data/ 目录，切换标签页自动刷新");
        lblHint.setFont(new Font("Microsoft YaHei", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);
        bottomPanel.add(lblHint);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建一个带标题和值的统计卡片
     * @return 卡片面板
     */
    private JPanel makeCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(new Color(252, 252, 252));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Consolas", Font.BOLD, 22));
        lblValue.setForeground(new Color(50, 50, 50));
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    /**
     * 创建卡片并返回其值标签引用，同时添加到父面板
     */
    private JLabel makeCardValue(JPanel parent, String title, String value) {
        JPanel card = makeCard(title, value);
        parent.add(card);
        return (JLabel) card.getComponent(1);  // 第二个组件就是值标签
    }

    /**
     * 刷新所有统计数据
     */
    public void refreshData() {
        lblTotalProducts.setText(productDAO.getCount() + " 种");

        int totalStock = productDAO.getAll().stream().mapToInt(p -> p.getStock()).sum();
        lblTotalStock.setText(totalStock + " 件");

        lblInventoryValue.setText("¥ " + String.format("%.2f", stockService.getTotalInventoryValue()));

        lblTodayIn.setText(stockService.getTodayTotalIn() + " 件");
        lblTodayOut.setText(stockService.getTodayTotalOut() + " 件");

        double profit = financeService.calculateMonthlyProfit();
        lblMonthProfit.setText("¥ " + String.format("%.2f", profit));
        lblMonthProfit.setForeground(profit >= 0 ? new Color(0, 140, 0) : Color.RED);

        lblMonthExpense.setText("¥ " + String.format("%.2f", financeService.getCurrentMonthTotalExpense()));
    }
}
