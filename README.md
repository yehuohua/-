# 🏭 仓库管理系统

一个基于 Java Swing 的桌面仓库管理系统，支持商品管理、出入库登记、财务统计等功能。数据使用 CSV 文件持久化存储。

## 环境要求

- **JDK 8 或以上**（脚本默认使用 Java 26，如版本不同请修改 `compile_updated.bat` 第 11 行）
- **Windows 系统**（使用 `.bat` 脚本启动）
- 项目路径建议使用纯英文，避免编码问题

## 快速开始

### 第一步：修改 Java 版本（如果需要）

打开 `compile_updated.bat`，找到第 11 行：

```bat
set JAVA_VERSION=26
```

把 `26` 改成你电脑上安装的 JDK 版本，比如 `8`、`11`、`17`、`21` 等：

```bat
set JAVA_VERSION=8
```

> 查看你的 Java 版本：打开命令行输入 `java -version`

### 第二步：编译

双击 **`compile_updated.bat`**

或者在命令行中执行：

```bash
compile_updated.bat
```

看到 `COMPILATION SUCCESSFUL!` 就说明编译成功了。

### 第三步：运行

双击 **`run_updated.bat`**

或者在命令行中执行：

```bash
run_updated.bat
```

系统界面就会弹出来啦！

---

## 中文路径问题？

如果你的项目路径包含中文，双击 **`setup.bat`**，输入一个纯英文的目标路径（如 `C:\projects\warehouse`），脚本会自动把所有文件复制过去。

---

## 手动编译运行（不用 .bat）

```bash
# 1. 创建输出目录
mkdir out

# 2. 按顺序编译
javac -encoding UTF-8 -d out src/model/*.java
javac -encoding UTF-8 -d out -cp out src/dao/*.java
javac -encoding UTF-8 -d out -cp out src/service/*.java src/ui/*.java src/App.java

# 3. 运行
java -cp out App
```

---

## 项目结构

```
warehouse-system/
├── compile_updated.bat   # 编译脚本
├── run_updated.bat       # 启动脚本
├── setup.bat             # 英文路径迁移脚本
├── src/
│   ├── App.java          # 主入口
│   ├── model/            # 数据模型（商品、库存记录、财务记录）
│   ├── dao/              # 数据访问层（CSV 读写）
│   ├── service/          # 业务逻辑层
│   └── ui/               # Swing 界面
└── data/                 # CSV 数据文件
```

## 功能模块

| 标签页 | 功能 |
|--------|------|
| 📊 仪表盘 | 总览：库存量、库存价值、今日出入库、本月利润 |
| 📦 商品管理 | 商品的增删改查、按名称/编号搜索、按类别筛选 |
| 📥 入库管理 | 商品入库登记，自动更新库存 |
| 📤 出库管理 | 商品出库登记，库存不足时提示 |
| 💰 财务管理 | 月度固定支出（房租水电）管理与利润计算 |

## 技术栈

- **语言**：Java
- **UI 框架**：Swing
- **数据存储**：CSV 文件
- **编码**：UTF-8
