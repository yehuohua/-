package ui;

import dao.FinanceDAO;
import dao.ProductDAO;
import dao.StockRecordDAO;
import service.FinanceService;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 主窗口框架
 * 使用 JTabbedPane 组织各功能模块为标签页
 */
public class MainFrame extends JFrame {

    // 数据文件路径（放在项目根目录的 data/ 下）
    private static final String DATA_DIR = "data";
    private static final String PRODUCT_FILE = DATA_DIR + File.separator + "products.csv";
    private static final String STOCK_RECORD_FILE = DATA_DIR + File.separator + "stock_records.csv";
    private static final String FINANCE_FILE = DATA_DIR + File.separator + "finance.csv";

    // 数据访问层（各Panel共用）
    private ProductDAO productDAO;
    private StockRecordDAO stockRecordDAO;
    private FinanceDAO financeDAO;

    // 业务逻辑层
    private StockService stockService;
    private FinanceService financeService;

    // 面板
    private DashboardPanel dashboardPanel;
    private ProductPanel productPanel;
    private StockInPanel stockInPanel;
    private StockOutPanel stockOutPanel;
    private FinancePanel financePanel;

    /**
     * 构造方法：初始化数据层、业务层和所有界面组件
     */
    public MainFrame() {
        // 初始化数据层
        initDataLayer();
        // 初始化业务层
        initServiceLayer();
        // 初始化界面
        initUI();
    }

    /**
     * 初始化数据访问层（确保 data 目录存在，加载 CSV 文件）
     */
    private void initDataLayer() {
        // 确保数据目录存在
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        productDAO = new ProductDAO(PRODUCT_FILE);
        stockRecordDAO = new StockRecordDAO(STOCK_RECORD_FILE);
        financeDAO = new FinanceDAO(FINANCE_FILE);
    }

    /**
     * 初始化业务逻辑层
     */
    private void initServiceLayer() {
        stockService = new StockService(productDAO, stockRecordDAO);
        financeService = new FinanceService(financeDAO, productDAO, stockRecordDAO);
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setTitle("仓库管理系统 v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);  // 窗口居中显示

        // 创建标签页面板
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        // 初始化各功能面板
        dashboardPanel = new DashboardPanel(productDAO, stockRecordDAO, financeDAO,
                stockService, financeService);
        productPanel = new ProductPanel(productDAO);
        stockInPanel = new StockInPanel(productDAO, stockRecordDAO, stockService);
        stockOutPanel = new StockOutPanel(productDAO, stockRecordDAO, stockService);
        financePanel = new FinancePanel(financeDAO, financeService);

        // 添加标签页
        tabbedPane.addTab("📊 仪表盘", dashboardPanel);
        tabbedPane.addTab("📦 商品管理", productPanel);
        tabbedPane.addTab("📥 入库管理", stockInPanel);
        tabbedPane.addTab("📤 出库管理", stockOutPanel);
        tabbedPane.addTab("💰 财务管理", financePanel);

        // 标签页切换时自动刷新数据
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected == dashboardPanel) {
                dashboardPanel.refreshData();
            } else if (selected == stockInPanel) {
                stockInPanel.refreshProductCombo();     // 刷新入库商品下拉
            } else if (selected == stockOutPanel) {
                stockOutPanel.refreshProductCombo();    // 刷新出库商品下拉
            }
        });

        add(tabbedPane);
    }

    // ==================== 供外部获取各层引用的方法 ====================

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public StockRecordDAO getStockRecordDAO() {
        return stockRecordDAO;
    }

    public FinanceDAO getFinanceDAO() {
        return financeDAO;
    }

    public StockService getStockService() {
        return stockService;
    }

    public FinanceService getFinanceService() {
        return financeService;
    }
}
