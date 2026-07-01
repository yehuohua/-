package ui;

import dao.FinanceDAO;
import model.FinanceRecord;
import service.FinanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 财务管理面板
 * 功能：记录每月房租、水电等支出，查看月度利润统计
 */
public class FinancePanel extends JPanel {

    private final FinanceDAO financeDAO;
    private final FinanceService financeService;

    private JTable table;
    private DefaultTableModel tableModel;

    // 表单
    private JTextField txtMonth, txtRent, txtWater, txtElectric, txtOther, txtRemark;
    private JLabel lblProfit;

    public FinancePanel(FinanceDAO financeDAO, FinanceService financeService) {
        this.financeDAO = financeDAO;
        this.financeService = financeService;
        initUI();
        refreshTable(financeDAO.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== 顶部：利润统计条 ======
        JPanel profitBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        profitBar.setBackground(new Color(245, 245, 250));
        profitBar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 220)));

        profitBar.add(new JLabel("本月（" + LocalDate.now().toString().substring(0, 7) + "）毛利润："));
        lblProfit = new JLabel("¥ 0.00");
        lblProfit.setFont(new Font("Consolas", Font.BOLD, 24));
        lblProfit.setForeground(new Color(0, 120, 0));
        profitBar.add(lblProfit);

        JButton btnCalcProfit = new JButton("重新计算");
        btnCalcProfit.addActionListener(e -> updateProfitDisplay());
        profitBar.add(btnCalcProfit);

        add(profitBar, BorderLayout.NORTH);

        // ====== 中部：财务记录表格 ======
        String[] columns = {"编号", "月份", "房租(元)", "水费(元)", "电费(元)", "其他支出(元)", "合计(元)", "备注"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        // 选中行填充表单
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    row = table.convertRowIndexToModel(row);
                    fillFormFromRow(row);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ====== 底部：表单 ======
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("财务记录编辑"));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 8));

        formPanel.add(new JLabel("月份(yyyy-MM):"));
        txtMonth = new JTextField();
        formPanel.add(txtMonth);

        formPanel.add(new JLabel("房租(元):"));
        txtRent = new JTextField("0");
        formPanel.add(txtRent);

        formPanel.add(new JLabel("水费(元):"));
        txtWater = new JTextField("0");
        formPanel.add(txtWater);

        formPanel.add(new JLabel("电费(元):"));
        txtElectric = new JTextField("0");
        formPanel.add(txtElectric);

        formPanel.add(new JLabel("其他支出(元):"));
        txtOther = new JTextField("0");
        formPanel.add(txtOther);

        formPanel.add(new JLabel("备注:"));
        txtRemark = new JTextField();
        formPanel.add(txtRemark);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd = new JButton("➕ 新增/保存");
        btnAdd.addActionListener(e -> doAddOrUpdate());
        btnPanel.add(btnAdd);

        JButton btnDelete = new JButton("🗑️ 删除");
        btnDelete.addActionListener(e -> doDelete());
        btnPanel.add(btnDelete);

        JButton btnClear = new JButton("🧹 清空");
        btnClear.addActionListener(e -> clearForm());
        btnPanel.add(btnClear);

        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        updateProfitDisplay();
    }

    // ==================== 表格操作 ====================

    private void refreshTable(List<FinanceRecord> list) {
        tableModel.setRowCount(0);
        for (FinanceRecord r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getMonth(),
                    String.format("%.2f", r.getRent()),
                    String.format("%.2f", r.getWaterBill()),
                    String.format("%.2f", r.getElectricBill()),
                    String.format("%.2f", r.getOtherExpense()),
                    String.format("%.2f", r.getTotalExpense()),
                    r.getRemark()
            });
        }
        updateProfitDisplay();
    }

    private void fillFormFromRow(int modelRow) {
        txtMonth.setText((String) tableModel.getValueAt(modelRow, 1));
        txtRent.setText(tableModel.getValueAt(modelRow, 2).toString());
        txtWater.setText(tableModel.getValueAt(modelRow, 3).toString());
        txtElectric.setText(tableModel.getValueAt(modelRow, 4).toString());
        txtOther.setText(tableModel.getValueAt(modelRow, 5).toString());
        txtRemark.setText(tableModel.getValueAt(modelRow, 7) != null
                ? tableModel.getValueAt(modelRow, 7).toString() : "");
    }

    private void clearForm() {
        // 默认填充当前月份
        txtMonth.setText(LocalDate.now().toString().substring(0, 7));
        txtRent.setText("0");
        txtWater.setText("0");
        txtElectric.setText("0");
        txtOther.setText("0");
        txtRemark.setText("");
        table.clearSelection();
    }

    private void updateProfitDisplay() {
        double profit = financeService.calculateMonthlyProfit();
        lblProfit.setText("¥ " + String.format("%.2f", profit));
        lblProfit.setForeground(profit >= 0 ? new Color(0, 140, 0) : Color.RED);
    }

    // ==================== 增删改 ====================

    private void doAddOrUpdate() {
        String month = txtMonth.getText().trim();
        if (month.isEmpty() || !month.matches("\\d{4}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "请输入正确的月份格式（如：2026-06）！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double rent = Double.parseDouble(txtRent.getText().trim());
            double water = Double.parseDouble(txtWater.getText().trim());
            double electric = Double.parseDouble(txtElectric.getText().trim());
            double other = Double.parseDouble(txtOther.getText().trim());
            String remark = txtRemark.getText().trim();

            // 检查该月份是否已存在记录
            FinanceRecord existing = financeDAO.findByMonth(month);
            if (existing != null) {
                // 更新
                existing.setRent(rent);
                existing.setWaterBill(water);
                existing.setElectricBill(electric);
                existing.setOtherExpense(other);
                existing.setRemark(remark);
                financeDAO.update(existing);
                JOptionPane.showMessageDialog(this, "财务记录已更新！");
            } else {
                // 新增
                FinanceRecord record = new FinanceRecord(
                        financeDAO.generateNewId(), month, rent, water, electric, other, remark);
                financeDAO.add(record);
                JOptionPane.showMessageDialog(this, "财务记录添加成功！");
            }

            refreshTable(financeDAO.getAll());
            updateProfitDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入正确的金额！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String month = txtMonth.getText().trim();
        if (month.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FinanceRecord record = financeDAO.findByMonth(month);
        if (record == null) {
            JOptionPane.showMessageDialog(this, "记录不存在！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除 " + month + " 的财务记录吗？",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            financeDAO.delete(record.getId());
            refreshTable(financeDAO.getAll());
            clearForm();
            JOptionPane.showMessageDialog(this, "删除成功！");
        }
    }
}
