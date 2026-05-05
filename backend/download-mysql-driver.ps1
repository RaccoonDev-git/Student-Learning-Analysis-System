# 下载MySQL驱动JAR文件
$driverUrl = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
$driverPath = "./mysql-connector-j-8.0.33.jar"

Write-Host "正在下载MySQL驱动..."
Invoke-WebRequest -Uri $driverUrl -OutFile $driverPath

if (Test-Path $driverPath) {
    Write-Host "✅ MySQL驱动下载成功!"
    Write-Host "文件位置: $driverPath"
    
    # 运行JdbcDirectTest类
    Write-Host "\n正在运行数据库连接测试...\n"
    java -cp "$driverPath;target/classes" com.example.studentanalysissystem.test.JdbcDirectTest
} else {
    Write-Host "❌ MySQL驱动下载失败!"
}