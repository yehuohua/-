package service;

import dao.ProductDAO;
import dao.StockRecordDAO;
import model.Product;
import model.StockRecord;

import java.time.LocalDate;

/**
 * 库存业务逻辑层
 * 处理入库、出库等核心业务，确保库存数据一致性
 * 入库时自动增加商品库存，出库时自动扣减库存并检查是否充足
 */
public class StockService {

    private final ProductDAO productDAO;
    private final StockRecordDAO recordDAO;

    public StockService(ProductDAO productDAO, StockRecordDAO recordDAO) {
        this.productDAO = productDAO;
        this.recordDAO = recordDAO;
    }

    /**
     * 执行入库操作
     * 1. 保存入库记录
     * 2. 自动更新商品库存（增加）
     *
     * @param record 入库记录
     * @return 操作结果消息
     */
    public String doStockIn(StockRecord record) {
        Product product = productDAO.findById(record.getProductId());
        if (product == null) {
            return "错误：商品不存在！";
        }
        if (record.getQuantity() <= 0) {
            return "错误：入库数量必须大于0！";
        }

        // 保存记录
        recordDAO.add(record);

        // 更新商品库存
        product.setStock(product.getStock() + record.getQuantity());
        productDAO.update(product);

        return "入库成功！" + product.getName() + " 当前库存: " + product.getStock();
    }

    /**
     * 执行出库操作
     * 1. 检查库存是否充足
     * 2. 保存出库记录
     * 3. 自动扣减商品库存
     *
     * @param record 出库记录
     * @return 操作结果消息
     */
    public String doStockOut(StockRecord record) {
        Product product = productDAO.findById(record.getProductId());
        if (product == null) {
            return "错误：商品不存在！";
        }
        if (record.getQuantity() <= 0) {
            return "错误：出库数量必须大于0！";
        }
        if (product.getStock() < record.getQuantity()) {
            return "错误：库存不足！当前库存: " + product.getStock() + "，需要: " + record.getQuantity();
        }

        // 保存记录
        recordDAO.add(record);

        // 更新商品库存
        product.setStock(product.getStock() - record.getQuantity());
        productDAO.update(product);

        return "出库成功！" + product.getName() + " 当前库存: " + product.getStock();
    }

    /**
     * 计算库存总价值（按进价计算）
     * @return 库存总价值
     */
    public double getTotalInventoryValue() {
        double total = 0;
        for (Product p : productDAO.getAll()) {
            total += p.getStock() * p.getPurchasePrice();
        }
        return total;
    }

    /**
     * 获取今日入库总量
     */
    public int getTodayTotalIn() {
        return recordDAO.getDailyTotalIn(LocalDate.now());
    }

    /**
     * 获取今日出库总量
     */
    public int getTodayTotalOut() {
        return recordDAO.getDailyTotalOut(LocalDate.now());
    }

    /**
     * 获取今日入库记录数
     */
    public long getTodayInCount() {
        return recordDAO.filterByType(StockRecord.Type.IN).stream()
                .filter(r -> r.getDate().equals(LocalDate.now()))
                .count();
    }

    /**
     * 获取今日出库记录数
     */
    public long getTodayOutCount() {
        return recordDAO.filterByType(StockRecord.Type.OUT).stream()
                .filter(r -> r.getDate().equals(LocalDate.now()))
                .count();
    }
}
