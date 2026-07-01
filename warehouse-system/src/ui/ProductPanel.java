package ui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 商品管理面板
 * 功能：商品的增删改查、按类别筛选、按名称搜索、表格排序
 */
public class ProductPanel extends JPanel {

    private final ProductDAO productDAO;

    // 表格组件
    private JTable table;
    private DefaultTableModel tableModel;

    // 表单组件
    private JTextField txtId, txtName, txtCategory, txtUnit, txtStock, txtPurchasePrice, txtSellingPrice;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterCategory;

    public ProductPanel(ProductDAO productDAO) {
        this.productDAO = productDAO;
        initUI();
        refreshTable(productDAO.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== 顶部：搜索和筛选栏 ======
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        topPanel.add(new JLabel("搜索名称/编号:"));
        txtSearch = new JTextField(15);
        topPanel.add(txtSearch);

        JButton btnSearch = new JButton("🔍 搜索");
        btnSearch.addActionListener(e -> doSearch());
        topPanel.add(btnSearch);

        JButton btnShowAll = new JButton("📋 显示全部");
        btnShowAll.addActionListener(e -> refreshTable(productDAO.getAll()));
        topPanel.add(btnShowAll);

        topPanel.add(new JLabel("  类别:"));
        cmbFilterCategory = new JComboBox<>();
        cmbFilterCategory.addItem("全部");
        cmbFilterCategory.addActionListener(e -> doFilter());
        topPanel.add(cmbFilterCategory);

        add(topPanel, BorderLayout.NORTH);

        // ====== 中部：商品表格 ======
        String[] columns = {"编号", "名称", "类别", "单位", "库存", "进价(元)", "售价(元)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 禁止直接编辑表格
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);  // 启用点击列标题排序
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        // 表格选中事件：填充表单
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    row = table.convertRowIndexToModel(row);
                    fillFormFromRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ====== 底部：表单和操作按钮 ======
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("商品信息编辑"));

        // 表单区域（两行）
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 8));

        formPanel.add(new JLabel("编号:"));
        txtId = new JTextField();
        txtId.setEditable(false);  // 编号不可手动编辑
        formPanel.add(txtId);

        formPanel.add(new JLabel("名称:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("类别:"));
        txtCategory = new JTextField();
        formPanel.add(txtCategory);

        formPanel.add(new JLabel("单位:"));
        txtUnit = new JTextField();
        formPanel.add(txtUnit);

        formPanel.add(new JLabel("库存:"));
        txtStock = new JTextField();
        formPanel.add(txtStock);

        formPanel.add(new JLabel("进价(元):"));
        txtPurchasePrice = new JTextField();
        formPanel.add(txtPurchasePrice);

        formPanel.add(new JLabel("售价(元):"));
        txtSellingPrice = new JTextField();
        formPanel.add(txtSellingPrice);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd = new JButton("➕ 新增");
        btnAdd.addActionListener(e -> doAdd());
        btnPanel.add(btnAdd);

        JButton btnUpdate = new JButton("✏️ 修改");
        btnUpdate.addActionListener(e -> doUpdate());
        btnPanel.add(btnUpdate);

        JButton btnDelete = new JButton("🗑️ 删除");
        btnDelete.addActionListener(e -> doDelete());
        btnPanel.add(btnDelete);

        JButton btnClear = new JButton("🧹 清空表单");
        btnClear.addActionListener(e -> clearForm());
        btnPanel.add(btnClear);

        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== 表格刷新 ====================

    private void refreshTable(List<Product> list) {
        tableModel.setRowCount(0);
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getUnit(),
                    p.getStock(),
                    String.format("%.2f", p.getPurchasePrice()),
                    String.format("%.2f", p.getSellingPrice())
            });
        }
        // 刷新类别下拉框
        cmbFilterCategory.removeAllItems();
        cmbFilterCategory.addItem("全部");
        for (String cat : productDAO.getAllCategories()) {
            cmbFilterCategory.addItem(cat);
        }
    }

    // ==================== 表单操作 ====================

    /**
     * 从表格行数据填充表单
     */
    private void fillFormFromRow(int modelRow) {
        txtId.setText((String) tableModel.getValueAt(modelRow, 0));
        txtName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtCategory.setText((String) tableModel.getValueAt(modelRow, 2));
        txtUnit.setText((String) tableModel.getValueAt(modelRow, 3));
        txtStock.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtPurchasePrice.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        txtSellingPrice.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
    }

    /**
     * 清空表单
     */
    private void clearForm() {
        txtId.setText(productDAO.generateNewId());
        txtName.setText("");
        txtCategory.setText("");
        txtUnit.setText("个");
        txtStock.setText("0");
        txtPurchasePrice.setText("0.00");
        txtSellingPrice.setText("0.00");
        table.clearSelection();
    }

    /**
     * 新增商品
     */
    private void doAdd() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入商品名称！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Product p = new Product(
                    productDAO.generateNewId(),
                    name,
                    txtCategory.getText().trim().isEmpty() ? "未分类" : txtCategory.getText().trim(),
                    txtUnit.getText().trim().isEmpty() ? "个" : txtUnit.getText().trim(),
                    Integer.parseInt(txtStock.getText().trim()),
                    Double.parseDouble(txtPurchasePrice.getText().trim()),
                    Double.parseDouble(txtSellingPrice.getText().trim())
            );
            if (productDAO.add(p)) {
                refreshTable(productDAO.getAll());
                clearForm();
                JOptionPane.showMessageDialog(this, "商品添加成功！");
            } else {
                JOptionPane.showMessageDialog(this, "添加失败，编号已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入正确的数字格式！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 修改商品
     */
    private void doUpdate() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择一个商品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Product p = new Product(
                    id,
                    txtName.getText().trim(),
                    txtCategory.getText().trim(),
                    txtUnit.getText().trim(),
                    Integer.parseInt(txtStock.getText().trim()),
                    Double.parseDouble(txtPurchasePrice.getText().trim()),
                    Double.parseDouble(txtSellingPrice.getText().trim())
            );
            if (productDAO.update(p)) {
                refreshTable(productDAO.getAll());
                JOptionPane.showMessageDialog(this, "商品修改成功！");
            } else {
                JOptionPane.showMessageDialog(this, "修改失败，商品不存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入正确的数字格式！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除商品
     */
    private void doDelete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择一个商品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除商品 \"" + txtName.getText() + "\" 吗？\n删除后不可恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.delete(id)) {
                refreshTable(productDAO.getAll());
                clearForm();
                JOptionPane.showMessageDialog(this, "商品删除成功！");
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== 搜索 / 筛选 ====================

    private void doSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable(productDAO.getAll());
            return;
        }
        // 同时按名称和编号搜索
        List<Product> byName = productDAO.searchByName(keyword);
        List<Product> byId = productDAO.searchById(keyword);
        // 合并去重
        java.util.Set<Product> merged = new java.util.LinkedHashSet<>(byName);
        merged.addAll(byId);
        refreshTable(new java.util.ArrayList<>(merged));
    }

    private void doFilter() {
        String category = (String) cmbFilterCategory.getSelectedItem();
        refreshTable(productDAO.filterByCategory(category));
    }
}
