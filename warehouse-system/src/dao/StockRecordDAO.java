package dao;

import model.StockRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出入库记录数据访问对象
 * 负责出入库记录的持久化存储（CSV文件）和内存中的增删改查
 */
public class StockRecordDAO {

    private List<StockRecord> recordList;  // 内存中的记录列表
    private final String filePath;         // 数据文件路径

    public StockRecordDAO(String filePath) {
        this.filePath = filePath;
        this.recordList = new ArrayList<>();
        loadFromFile();
    }

    // ==================== CRUD 操作 ====================

    /**
     * 新增出入库记录
     */
    public void add(StockRecord record) {
        recordList.add(record);
        saveToFile();
    }

    /**
     * 根据编号查找记录
     */
    public StockRecord findById(String id) {
        for (StockRecord r : recordList) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * 更新记录
     */
    public boolean update(StockRecord updatedRecord) {
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getId().equals(updatedRecord.getId())) {
                recordList.set(i, updatedRecord);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    /**
     * 删除记录
     */
    public boolean delete(String id) {
        boolean removed = recordList.removeIf(r -> r.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * 获取所有记录（按日期倒序）
     */
    public List<StockRecord> getAll() {
        List<StockRecord> sorted = new ArrayList<>(recordList);
        sorted.sort(Comparator.comparing(StockRecord::getDate).reversed());
        return sorted;
    }

    // ==================== 查询 / 筛选 / 统计 ====================

    /**
     * 按商品名称或编号模糊搜索
     */
    public List<StockRecord> search(String keyword) {
        String lower = keyword.toLowerCase();
        return recordList.stream()
                .filter(r -> r.getProductName().toLowerCase().contains(lower)
                        || r.getProductId().toLowerCase().contains(lower)
                        || r.getOperator().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(StockRecord::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 按日期范围筛选
     * @param startDate 开始日期（含）
     * @param endDate 结束日期（含）
     * @return 该日期范围内的记录
     */
    public List<StockRecord> filterByDateRange(LocalDate startDate, LocalDate endDate) {
        return recordList.stream()
                .filter(r -> !r.getDate().isBefore(startDate) && !r.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(StockRecord::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 按记录类型筛选
     * @param type 记录类型（IN/OUT），null表示全部
     */
    public List<StockRecord> filterByType(StockRecord.Type type) {
        if (type == null) return getAll();
        return recordList.stream()
                .filter(r -> r.getType() == type)
                .sorted(Comparator.comparing(StockRecord::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 统计指定日期某商品的入库总数
     */
    public int getTotalInByDate(String productId, LocalDate date) {
        return recordList.stream()
                .filter(r -> r.getType() == StockRecord.Type.IN
                        && r.getProductId().equals(productId)
                        && r.getDate().equals(date))
                .mapToInt(StockRecord::getQuantity)
                .sum();
    }

    /**
     * 统计指定日期某商品的出库总数
     */
    public int getTotalOutByDate(String productId, LocalDate date) {
        return recordList.stream()
                .filter(r -> r.getType() == StockRecord.Type.OUT
                        && r.getProductId().equals(productId)
                        && r.getDate().equals(date))
                .mapToInt(StockRecord::getQuantity)
                .sum();
    }

    /**
     * 统计指定日期的总入库量
     */
    public int getDailyTotalIn(LocalDate date) {
        return recordList.stream()
                .filter(r -> r.getType() == StockRecord.Type.IN && r.getDate().equals(date))
                .mapToInt(StockRecord::getQuantity)
                .sum();
    }

    /**
     * 统计指定日期的总出库量
     */
    public int getDailyTotalOut(LocalDate date) {
        return recordList.stream()
                .filter(r -> r.getType() == StockRecord.Type.OUT && r.getDate().equals(date))
                .mapToInt(StockRecord::getQuantity)
                .sum();
    }

    /**
     * 获取指定月份的所有记录
     */
    public List<StockRecord> getByMonth(String yearMonth) {
        return recordList.stream()
                .filter(r -> r.getDate().toString().startsWith(yearMonth))
                .sorted(Comparator.comparing(StockRecord::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取记录总数
     */
    public int getCount() {
        return recordList.size();
    }

    /**
     * 生成新的记录编号
     */
    public String generateNewId() {
        int maxNum = 0;
        for (StockRecord r : recordList) {
            String id = r.getId();
            if (id.startsWith("R") && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "R" + String.format("%06d", maxNum + 1);
    }

    // ==================== 文件读写 ====================

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("无法创建数据文件: " + e.getMessage());
            }
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        recordList.add(StockRecord.fromCSV(line));
                    } catch (Exception e) {
                        System.err.println("解析记录出错: " + line + " -> " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("读取数据文件失败: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            for (StockRecord r : recordList) {
                writer.write(r.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存数据文件失败: " + e.getMessage());
        }
    }
}
