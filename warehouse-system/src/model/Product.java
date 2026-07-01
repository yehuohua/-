package model;

import java.io.Serializable;

/**
 * 商品实体类
 * 用于描述仓库中的商品信息，包括基本信息、库存数量和价格
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;          // 商品编号（唯一标识）
    private String name;        // 商品名称
    private String category;    // 商品类别（如：电子产品、食品、日用品等）
    private String unit;        // 计量单位（如：个、箱、斤、台）
    private int stock;          // 当前库存数量
    private double purchasePrice; // 进货单价
    private double sellingPrice;  // 销售单价

    /**
     * 全参数构造方法
     */
    public Product(String id, String name, String category, String unit,
                   int stock, double purchasePrice, double sellingPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.stock = stock;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
    }

    // ==================== Getter / Setter ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    /**
     * 将商品对象转换为 CSV 格式字符串
     * 用于保存到文件
     */
    public String toCSV() {
        return id + "," + escape(name) + "," + escape(category) + ","
                + escape(unit) + "," + stock + "," + purchasePrice + "," + sellingPrice;
    }

    /**
     * 从 CSV 格式字符串解析出商品对象
     * @param csvLine CSV 格式的一行数据
     * @return 解析后的 Product 对象
     */
    public static Product fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        return new Product(
                unescape(parts[0]),   // id
                unescape(parts[1]),   // name
                unescape(parts[2]),   // category
                unescape(parts[3]),   // unit
                Integer.parseInt(unescape(parts[4])),  // stock
                Double.parseDouble(unescape(parts[5])), // purchasePrice
                Double.parseDouble(unescape(parts[6]))  // sellingPrice
        );
    }

    /**
     * 对包含逗号的字段进行转义（用双引号包裹）
     */
    private static String escape(String field) {
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * 取消转义，还原原始字段值
     */
    private static String unescape(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }

    @Override
    public String toString() {
        return name + "（" + id + "）";
    }
}
