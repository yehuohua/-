package ui;

import dao.ProductDAO;
import dao.StockRecordDAO;
import model.Product;
import model.StockRecord;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 出库管理面板
 * 功能：选择商品出库（自动检查库存）、查看出库记录、搜索筛选
 */
public class StockOutPanel extends JPanel {

    private final ProductDAO productDAO;
    private final StockRecordDAO recordDAO;
    private final StockService stockService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<Product> cmbProduct;
    private JTextField txtQuantity, txtOperator, txtRemark;
    private JTextField txtSearch;
    private JLabel lblCurrentStock;  // 显示选中商品的当前库存

    public StockOutPanel(ProductDAO productDAO, StockRecordDAO recordDAO, StockService stockService) {
        this.productDAO = productDAO;
        this.recordDAO = recordDAO;
        this.stockService = stockService;
        initUI();
        refreshTable(recordDAO.filterByType(StockRecord.Type.OUT));
        refreshProductCombo();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== 顶部：搜索 ======
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("搜索（商品/经手人）:"));
        txtSearch = new JTextField(15);
        topPanel.add(txtSearch);
        JButton btnSearch = new JButton("搜索");
        btnSearch.addActionListener(e -> doSearch());
        topPanel.add(btnSearch);
        JButton btnShowAll = new JButton("显示全部");
        btnShowAll.addActionListener(e -> refreshTable(recordDAO.filterByType(StockRecord.Type.OUT)));
        topPanel.add(btnShowAll);
        add(topPanel, BorderLayout.NORTH);

        // ====== 中部：出库记录表格 ======
        String[] columns = {"记录编号", "商品名称", "数量", "日期", "经手人", "备注"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ====== 底部：出库表单 ======
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("新增出库"));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        formPanel.add(new JLabel("选择商品:"));
        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new Dimension(180, 25));
        cmbProduct.addActionListener(e -> updateStockHint());
        formPanel.add(cmbProduct);

        // 当前库存提示
        lblCurrentStock = new JLabel("库存: --");
        lblCurrentStock.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        lblCurrentStock.setForeground(new Color(0, 120, 0));
        formPanel.add(lblCurrentStock);

        formPanel.add(new JLabel("出库数量:"));
        txtQuantity = new JTextField(8);
        formPanel.add(txtQuantity);

        formPanel.add(new JLabel("经手人:"));
        txtOperator = new JTextField(8);
        formPanel.add(txtOperator);

        formPanel.add(new JLabel("备注:"));
        txtRemark = new JTextField(12);
        formPanel.add(txtRemark);

        JButton btnStockOut = new JButton("📤 确认出库");
        btnStockOut.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        btnStockOut.setBackground(new Color(217, 83, 79));
        btnStockOut.setForeground(Color.WHITE);
        btnStockOut.addActionListener(e -> doStockOut());
        formPanel.add(btnStockOut);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JLabel lblHint = new JLabel("⚠ 出库前请确认库存充足，出库后自动扣减库存");
        lblHint.setFont(new Font("Microsoft YaHei", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);
        bottomPanel.add(lblHint, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 更新库存提示
     */
    private void updateStockHint() {
        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected != null) {
            lblCurrentStock.setText("库存: " + selected.getStock() + " " + selected.getUnit());
            if (selected.getStock() <= 0) {
                lblCurrentStock.setForeground(Color.RED);
            } else if (selected.getStock() < 10) {
                lblCurrentStock.setForeground(Color.ORANGE);
            } else {
                lblCurrentStock.setForeground(new Color(0, 120, 0));
            }
        }
    }

    public void refreshProductCombo() {
        cmbProduct.removeAllItems();
        for (Product p : productDAO.getAll()) {
            cmbProduct.addItem(p);
        }
        updateStockHint();
    }

    private void refreshTable(List<StockRecord> list) {
        tableModel.setRowCount(0);
        for (StockRecord r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getProductName(), r.getQuantity(),
                    r.getDate().toString(), r.getOperator(), r.getRemark()
            });
        }
    }

    /**
     * 执行出库
     */
    private void doStockOut() {
        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个商品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String qtyStr = txtQuantity.getText().trim();
        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入出库数量！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int quantity = Integer.parseInt(qtyStr);
            String operator = txtOperator.getText().trim().isEmpty() ? "管理员" : txtOperator.getText().trim();
            String remark = txtRemark.getText().trim();

            StockRecord record = new StockRecord(
                    recordDAO.generateNewId(),
                    selected.getId(),
                    selected.getName(),
                    StockRecord.Type.OUT,
                    quantity,
                    LocalDate.now(),
                    operator,
                    remark
            );

            String result = stockService.doStockOut(record);
            JOptionPane.showMessageDialog(this, result);

            // 刷新界面
            refreshTable(recordDAO.filterByType(StockRecord.Type.OUT));
            refreshProductCombo();
            txtQuantity.setText("");
            txtRemark.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入正确的数量！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable(recordDAO.filterByType(StockRecord.Type.OUT));
            return;
        }
        List<StockRecord> results = recordDAO.search(keyword);
        results.removeIf(r -> r.getType() != StockRecord.Type.OUT);
        refreshTable(results);
    }
}
