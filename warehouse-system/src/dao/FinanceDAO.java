package dao;

import model.FinanceRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 财务记录数据访问对象
 * 负责财务数据（房租、水电等）的持久化存储和增删改查
 */
public class FinanceDAO {

    private List<FinanceRecord> recordList;
    private final String filePath;

    public FinanceDAO(String filePath) {
        this.filePath = filePath;
        this.recordList = new ArrayList<>();
        loadFromFile();
    }

    // ==================== CRUD 操作 ====================

    /**
     * 新增财务记录
     */
    public boolean add(FinanceRecord record) {
        // 同一月份只能有一条记录
        if (findByMonth(record.getMonth()) != null) {
            return false;
        }
        recordList.add(record);
        saveToFile();
        return true;
    }

    /**
     * 根据编号查找
     */
    public FinanceRecord findById(String id) {
        for (FinanceRecord r : recordList) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * 根据月份查找
     */
    public FinanceRecord findByMonth(String month) {
        for (FinanceRecord r : recordList) {
            if (r.getMonth().equals(month)) {
                return r;
            }
        }
        return null;
    }

    /**
     * 更新财务记录
     */
    public boolean update(FinanceRecord updatedRecord) {
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
     * 删除财务记录
     */
    public boolean delete(String id) {
        boolean removed = recordList.removeIf(r -> r.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * 获取所有财务记录（按月份倒序）
     */
    public List<FinanceRecord> getAll() {
        List<FinanceRecord> sorted = new ArrayList<>(recordList);
        sorted.sort(Comparator.comparing(FinanceRecord::getMonth).reversed());
        return sorted;
    }

    // ==================== 查询 / 统计 ====================

    /**
     * 按月份模糊搜索
     */
    public List<FinanceRecord> searchByMonth(String keyword) {
        return recordList.stream()
                .filter(r -> r.getMonth().contains(keyword) || r.getRemark().contains(keyword))
                .sorted(Comparator.comparing(FinanceRecord::getMonth).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取指定年份的所有财务记录
     */
    public List<FinanceRecord> getByYear(String year) {
        return recordList.stream()
                .filter(r -> r.getMonth().startsWith(year))
                .sorted(Comparator.comparing(FinanceRecord::getMonth))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有年份列表
     */
    public List<String> getAllYears() {
        return recordList.stream()
                .map(r -> r.getMonth().substring(0, 4))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 生成新编号
     */
    public String generateNewId() {
        int maxNum = 0;
        for (FinanceRecord r : recordList) {
            String id = r.getId();
            if (id.startsWith("F") && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "F" + String.format("%04d", maxNum + 1);
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
                        recordList.add(FinanceRecord.fromCSV(line));
                    } catch (Exception e) {
                        System.err.println("解析财务数据出错: " + line + " -> " + e.getMessage());
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
            for (FinanceRecord r : recordList) {
                writer.write(r.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存数据文件失败: " + e.getMessage());
        }
    }
}
