package model;

import java.io.Serializable;

/**
 * 财务记录实体类
 * 用于记录每个月的固定支出（房租、水电等）和其他费用
 */
public class FinanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;          // 记录编号（唯一标识）
    private String month;       // 月份（格式：yyyy-MM，如 2026-01）
    private double rent;        // 房租
    private double waterBill;   // 水费
    private double electricBill; // 电费
    private double otherExpense; // 其他支出（如物业、网络、办公用品等）
    private String remark;      // 备注

    /**
     * 全参数构造方法
     */
    public FinanceRecord(String id, String month, double rent, double waterBill,
                         double electricBill, double otherExpense, String remark) {
        this.id = id;
        this.month = month;
        this.rent = rent;
        this.waterBill = waterBill;
        this.electricBill = electricBill;
        this.otherExpense = otherExpense;
        this.remark = remark;
    }

    // ==================== Getter / Setter ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public double getRent() { return rent; }
    public void setRent(double rent) { this.rent = rent; }

    public double getWaterBill() { return waterBill; }
    public void setWaterBill(double waterBill) { this.waterBill = waterBill; }

    public double getElectricBill() { return electricBill; }
    public void setElectricBill(double electricBill) { this.electricBill = electricBill; }

    public double getOtherExpense() { return otherExpense; }
    public void setOtherExpense(double otherExpense) { this.otherExpense = otherExpense; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /**
     * 计算本月所有支出的总和
     * @return 房租+水费+电费+其他支出
     */
    public double getTotalExpense() {
        return rent + waterBill + electricBill + otherExpense;
    }

    /**
     * 将财务记录转换为 CSV 格式字符串
     */
    public String toCSV() {
        return id + "," + escape(month) + "," + rent + "," + waterBill + ","
                + electricBill + "," + otherExpense + "," + escape(remark);
    }

    /**
     * 从 CSV 格式解析财务记录
     */
    public static FinanceRecord fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        return new FinanceRecord(
                unescape(parts[0]),                         // id
                unescape(parts[1]),                         // month
                Double.parseDouble(unescape(parts[2])),      // rent
                Double.parseDouble(unescape(parts[3])),      // waterBill
                Double.parseDouble(unescape(parts[4])),      // electricBill
                Double.parseDouble(unescape(parts[5])),      // otherExpense
                unescape(parts[6])                          // remark
        );
    }

    private static String escape(String field) {
        if (field == null) field = "";
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private static String unescape(String field) {
        if (field == null) return "";
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }

    @Override
    public String toString() {
        return month + " - 总支出: ¥" + String.format("%.2f", getTotalExpense());
    }
}
