本系统采用Java语言开发，基于Swing搭建可视化GUI操作界面，面向中小型仓储日常运营需求，核心覆盖商品、出入库两大基础管理模块。系统支持商品信息录入、修改、查询与库存余量实时统计，完整记录入库采购、出库销售数据，自动留存每笔业务单据便于核对。系统拓展水电成本核算功能，可录入每月电费、水费支出，结合商品进销差价自动核算月度、年度总利润，清晰区分仓储运营成本与经营收益。界面操作简洁直观，摒弃复杂命令行，普通管理员可快速上手，实现仓储物资规范化管控、经营收支一体化计算，有效减少人工记账误差，提升仓库管理与盈利核算效率。
具体操作如下
🏭 仓库管理系统 — 打开 & 运行教程
环境要求
JDK 8 或以上（脚本默认使用 Java 26，如果版本不对，看下面的修改方法）
Windows 系统（使用 .bat 脚本）
路径中最好不要有中文（防止编码问题）
快速开始（三步）
第一步：修改 Java 版本（如果需要）
打开 compile_updated.bat，找到第 11 行：


set JAVA_VERSION=26
把 26 改成你电脑上安装的 JDK 版本，比如 8、11、17、21 等：


set JAVA_VERSION=8
查看你的 Java 版本：打开命令行输入 java -version

第二步：编译
双击 compile_updated.bat

或者在命令行中执行：


compile_updated.bat
看到 COMPILATION SUCCESSFUL! 就说明编译成功了。

第三步：运行
双击 run_updated.bat

或者在命令行中执行：


run_updated.bat
系统界面就会弹出来啦！
手动编译运行（不用 .bat）

# 1. 创建输出目录
mkdir out

# 2. 按顺序编译
javac -encoding UTF-8 -d out src/model/*.java
javac -encoding UTF-8 -d out -cp out src/dao/*.java
javac -encoding UTF-8 -d out -cp out src/service/*.java src/ui/*.java src/App.java

# 3. 运行
java -cp out App
项目结构

warehouse-system/
├── compile_updated.bat   # 编译脚本
├── run_updated.bat       # 启动脚本
├── setup.bat             # 英文路径迁移脚本
├── src/
│   ├── App.java          # 主入口
│   ├── model/            # 数据模型（商品、库存、财务）
│   ├── dao/              # 数据访问（CSV 读写）
│   ├── service/          # 业务逻辑
│   └── ui/               # Swing 界面（5 个标签页）
└── data/                 # CSV 数据文件
功能模块
标签页	功能
📊 仪表盘	总览：库存量、库存价值、今日出入库、本月利润
📦 商品管理	商品的增删改查、搜索筛选
📥 入库管理	商品入库登记
📤 出库管理	商品出库登记（含库存校验）
💰 财务管理	月度固定支出（房租水电）管理 & 利润计算
你可以直接把上面整段复制到 README.md。需要我帮你把这个 README 文件直接写到项目里吗？
