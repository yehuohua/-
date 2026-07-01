package service;

import dao.FinanceDAO;
import dao.ProductDAO;
import dao.StockRecordDAO;
import model.FinanceRecord;
import model.Product;
import model.StockRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务业务逻辑层
 * 处理月度财务统计、利润计算等
 */
public class FinanceService {

    private final FinanceDAO financeDAO;
    private final ProductDAO productDAO;
    private final StockRecordDAO stockRecordDAO;

    public FinanceService(FinanceDAO financeDAO, ProductDAO productDAO, StockRecordDAO stockRecordDAO) {
        this.financeDAO = financeDAO;
        this.productDAO = productDAO;
        this.stockRecordDAO = stockRecordDAO;
    }

    /**
     * 计算当月毛利润
     * 公式：当月出库商品总额（按售价） - 当月入库商品总额（按进价） - 当月各项支出
     *
     * @return 毛利润
     */
    public double calculateMonthlyProfit() {
        String currentMonth = LocalDate.now().toString().substring(0, 7); // yyyy-MM
        return calculateProfitByMonth(currentMonth);
    }

    /**
     * 计算指定月份的毛利润
     */
    public double calculateProfitByMonth(String yearMonth) {
        // 当月出库总额（按售价）
        double totalSales = 0;
        List<StockRecord> monthRecords = stockRecordDAO.getByMonth(yearMonth);
        for (StockRecord r : monthRecords) {
            if (r.getType() == StockRecord.Type.OUT) {
                Product p = productDAO.findById(r.getProductId());
                if (p != null) {
                    totalSales += r.getQuantity() * p.getSellingPrice();
                }
            }
        }

        // 当月入库总额（按进价）
        double totalPurchase = 0;
        for (StockRecord r : monthRecords) {
            if (r.getType() == StockRecord.Type.IN) {
                Product p = productDAO.findById(r.getProductId());
                if (p != null) {
                    totalPurchase += r.getQuantity() * p.getPurchasePrice();
                }
            }
        }

        // 当月各项支出
        double totalExpense = 0;
        FinanceRecord finance = financeDAO.findByMonth(yearMonth);
        if (finance != null) {
            totalExpense = finance.getTotalExpense();
        }

        return totalSales - totalPurchase - totalExpense;
    }

    /**
     * 获取当前月份的财务记录
     * @return 财务记录，不存在返回 null
     */
    public FinanceRecord getCurrentMonthFinance() {
        String currentMonth = LocalDate.now().toString().substring(0, 7);
        return financeDAO.findByMonth(currentMonth);
    }

    /**
     * 获取当月总支出
     * @return 总支出金额
     */
    public double getCurrentMonthTotalExpense() {
        FinanceRecord record = getCurrentMonthFinance();
        return record != null ? record.getTotalExpense() : 0;
    }

    /**
     * 统计全年总支出
     * @param year 年份
     * @return 全年总支出
     */
    public double getYearlyTotalExpense(String year) {
        double total = 0;
        for (FinanceRecord r : financeDAO.getByYear(year)) {
            total += r.getTotalExpense();
        }
        return total;
    }
}
