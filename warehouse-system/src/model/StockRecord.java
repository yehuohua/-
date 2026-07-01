package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 出入库记录实体类
 * 记录每一次入库或出库的详细信息
 */
public class StockRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录类型枚举：IN=入库，OUT=出库 */
    public enum Type {
        IN("入库"), OUT("出库");

        private final String label;
        Type(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private String id;          // 记录编号（唯一标识）
    private String productId;   // 关联商品编号
    private String productName; // 商品名称（冗余字段，方便查询显示）
    private Type type;          // 记录类型：入库 / 出库
    private int quantity;       // 数量
    private LocalDate date;     // 操作日期
    private String operator;    // 经手人
    private String remark;      // 备注

    /**
     * 全参数构造方法
     */
    public StockRecord(String id, String productId, String productName, Type type,
                       int quantity, LocalDate date, String operator, String remark) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.date = date;
        this.operator = operator;
        this.remark = remark;
    }

    // ==================== Getter / Setter ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /**
     * 将记录转换为 CSV 格式字符串
     */
    public String toCSV() {
        return id + "," + escape(productId) + "," + escape(productName) + ","
                + type.name() + "," + quantity + "," + date.toString() + ","
                + escape(operator) + "," + escape(remark);
    }

    /**
     * 从 CSV 格式字符串解析记录对象
     */
    public static StockRecord fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        return new StockRecord(
                unescape(parts[0]),                        // id
                unescape(parts[1]),                        // productId
                unescape(parts[2]),                        // productName
                Type.valueOf(unescape(parts[3])),          // type
                Integer.parseInt(unescape(parts[4])),       // quantity
                LocalDate.parse(unescape(parts[5])),        // date
                unescape(parts[6]),                        // operator
                unescape(parts[7])                         // remark
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
        return type.getLabel() + " - " + productName + " x" + quantity + " (" + date + ")";
    }
}
