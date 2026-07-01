package dao;

import model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品数据访问对象
 * 负责商品数据的持久化存储（CSV文件）和内存中的增删改查操作
 */
public class ProductDAO {

    private List<Product> productList;  // 内存中的商品列表
    private final String filePath;      // 数据文件路径

    /**
     * 构造方法：指定数据文件路径并加载数据
     * @param filePath CSV 数据文件路径
     */
    public ProductDAO(String filePath) {
        this.filePath = filePath;
        this.productList = new ArrayList<>();
        loadFromFile();
    }

    // ==================== CRUD 操作 ====================

    /**
     * 新增商品
     * @param product 要添加的商品对象
     * @return true 添加成功，false 编号已存在
     */
    public boolean add(Product product) {
        // 检查编号是否重复
        if (findById(product.getId()) != null) {
            return false;
        }
        productList.add(product);
        saveToFile();
        return true;
    }

    /**
     * 根据编号查找商品
     * @param id 商品编号
     * @return 找到的商品对象，未找到返回 null
     */
    public Product findById(String id) {
        for (Product p : productList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 更新商品信息
     * @param updatedProduct 更新后的商品对象
     * @return true 更新成功，false 商品不存在
     */
    public boolean update(Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(updatedProduct.getId())) {
                productList.set(i, updatedProduct);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    /**
     * 删除商品
     * @param id 要删除的商品编号
     * @return true 删除成功，false 商品不存在
     */
    public boolean delete(String id) {
        boolean removed = productList.removeIf(p -> p.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * 获取所有商品列表
     * @return 商品列表（副本）
     */
    public List<Product> getAll() {
        return new ArrayList<>(productList);
    }

    // ==================== 查询 / 搜索 / 排序 / 筛选 ====================

    /**
     * 按商品名称模糊搜索
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     */
    public List<Product> searchByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return productList.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 按商品编号模糊搜索
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     */
    public List<Product> searchById(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return productList.stream()
                .filter(p -> p.getId().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 按类别筛选商品
     * @param category 类别名称
     * @return 该类别的商品列表
     */
    public List<Product> filterByCategory(String category) {
        if (category == null || category.isEmpty() || category.equals("全部")) {
            return getAll();
        }
        return productList.stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * 按库存数量排序（默认升序）
     * @param ascending true 升序，false 降序
     * @return 排序后的商品列表
     */
    public List<Product> sortByStock(boolean ascending) {
        return productList.stream()
                .sorted(ascending ? Comparator.comparingInt(Product::getStock)
                                  : Comparator.comparingInt(Product::getStock).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 按商品名称排序
     * @param ascending true 升序，false 降序
     * @return 排序后的商品列表
     */
    public List<Product> sortByName(boolean ascending) {
        return productList.stream()
                .sorted(ascending ? Comparator.comparing(Product::getName)
                                  : Comparator.comparing(Product::getName).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取所有商品类别（去重）
     * @return 类别列表
     */
    public List<String> getAllCategories() {
        return productList.stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取商品总数
     * @return 商品数量
     */
    public int getCount() {
        return productList.size();
    }

    /**
     * 生成新的商品编号
     * @return 自动生成的编号（格式：P + 序号）
     */
    public String generateNewId() {
        int maxNum = 0;
        for (Product p : productList) {
            String id = p.getId();
            if (id.startsWith("P") && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "P" + String.format("%04d", maxNum + 1);
    }

    // ==================== 文件读写 ====================

    /**
     * 从 CSV 文件加载商品数据
     */
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            // 文件不存在则创建空文件
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
                        productList.add(Product.fromCSV(line));
                    } catch (Exception e) {
                        System.err.println("解析商品数据出错: " + line + " -> " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("读取数据文件失败: " + e.getMessage());
        }
    }

    /**
     * 将商品列表保存到 CSV 文件
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            for (Product p : productList) {
                writer.write(p.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存数据文件失败: " + e.getMessage());
        }
    }
}
