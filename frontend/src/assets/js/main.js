// API基础URL从全局变量获取
const API_URL = window.API_URL || "/api";

// 打开模态框
function openModal(title, formData = null) {
  document.getElementById("modal-title").textContent = title;
  const form = document.getElementById("modal-form");
  form.reset();

  if (formData) {
    Object.keys(formData).forEach((key) => {
      const field = document.getElementById(key);
      if (field) {
        field.value = formData[key];
      }
    });
    document.getElementById("form-id").value = formData.id;
  }

  document.getElementById("modal").style.display = "block";
}

// 关闭模态框
function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// 通用API请求函数
async function apiRequest(endpoint, method = "GET", data = null, options = {}) {
  const url = `${API_URL}${endpoint}`;
  const requestOptions = {
    method,
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  };

  // 如果用户已登录，添加JWT令牌到请求头
  const user = JSON.parse(localStorage.getItem("user"));
  if (user && user.token && !requestOptions.headers["Authorization"]) {
    requestOptions.headers["Authorization"] = `Bearer ${user.token}`;
  }

  if (data && requestOptions.headers["Content-Type"] === "application/json") {
    requestOptions.body = JSON.stringify(data);
  } else if (data) {
    requestOptions.body = data;
  }

  try {
    const response = await fetch(url, requestOptions);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error("API请求失败:", error);
    showNotification("请求失败: " + error.message, "error");
    throw error;
  }
}

// 显示通知
function showNotification(message, type = "info") {
  // 创建通知元素
  const notification = document.createElement("div");
  notification.className = `notification ${type}`;
  notification.textContent = message;

  // 添加样式
  const style = document.createElement("style");
  style.textContent = `
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            color: white;
            font-weight: 500;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            z-index: 1000;
            transform: translateX(100%);
            transition: transform 0.3s ease;
        }
        .notification.show {
            transform: translateX(0);
        }
        .notification.info {
            background-color: #3498db;
        }
        .notification.success {
            background-color: #2ecc71;
        }
        .notification.error {
            background-color: #e74c3c;
        }
        .notification.warning {
            background-color: #f39c12;
        }
    `;
  document.head.appendChild(style);

  // 添加到页面并显示
  document.body.appendChild(notification);
  setTimeout(() => {
    notification.classList.add("show");
  }, 100);

  // 3秒后移除
  setTimeout(() => {
    notification.classList.remove("show");
    setTimeout(() => {
      document.body.removeChild(notification);
      document.head.removeChild(style);
    }, 300);
  }, 3000);
}

// 格式化日期
function formatDate(dateString) {
  const options = { year: "numeric", month: "2-digit", day: "2-digit" };
  return new Date(dateString).toLocaleDateString("zh-CN", options);
}

// 格式化数字
function formatNumber(num) {
  return num.toLocaleString("zh-CN");
}

// 数字动画函数
function animateNumber(element, start, end, duration = 1000) {
  const range = end - start;
  const startTime = performance.now();

  function updateNumber(currentTime) {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);

    // 使用缓动函数使动画更自然
    const easeOutQuart = 1 - Math.pow(1 - progress, 4);
    const currentValue = Math.floor(start + range * easeOutQuart);

    element.textContent = currentValue + "%";

    if (progress < 1) {
      requestAnimationFrame(updateNumber);
    }
  }

  requestAnimationFrame(updateNumber);
}

// 初始化数字动画
function initNumberAnimations() {
  // 获取所有数据卡片中的百分比元素
  const percentageElements = document.querySelectorAll(
    ".data-card .percentage"
  );

  // 为每个元素添加动画
  percentageElements.forEach((element, index) => {
    // 获取当前显示的数值
    const currentValue = parseInt(element.textContent);

    // 重置为0
    element.textContent = "0%";

    // 添加延迟，使动画依次进行
    setTimeout(() => {
      animateNumber(element, 0, currentValue, 1500);
    }, index * 200);
  });
}

// 初始化仪表盘
function initDashboard() {
  console.log("初始化仪表盘...");
  // 这里可以添加仪表盘初始化逻辑
}

// 初始化学生管理
function initStudentManagement() {
  console.log("初始化学生管理...");
  // 这里可以添加学生管理初始化逻辑
}

// 初始化成绩分析
function initGradeAnalysis() {
  console.log("初始化成绩分析...");
  // 这里可以添加成绩分析初始化逻辑
}

// 初始化预览区域
function initPreviewSection() {
  console.log("初始化预览区域...");

  // 获取预览区域元素
  const previewSection = document.querySelector(".preview-section");
  if (!previewSection) {
    console.log("预览区域未找到，跳过初始化");
    return;
  }

  // 添加入场动画类
  previewSection.style.opacity = "0";
  previewSection.style.transform = "translateY(30px)";
  previewSection.style.transition = "opacity 0.8s ease, transform 0.8s ease";

  // 延迟显示预览区域
  setTimeout(() => {
    previewSection.style.opacity = "1";
    previewSection.style.transform = "translateY(0)";
  }, 500);
}

// 页面加载完成后初始化
document.addEventListener("DOMContentLoaded", function () {
  console.log("页面加载完成，初始化各模块...");
  initDashboard();
  initStudentManagement();
  initGradeAnalysis();

  // 初始化数字动画
  initNumberAnimations();

  // 初始化预览区域
  initPreviewSection();
});
