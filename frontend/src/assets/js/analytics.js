// 数据分析模块
let chartInstances = [];

// 初始化分析模块
function initAnalytics() {
  // 加载分析数据
  loadAnalyticsData();
}

// 将初始化函数绑定到window对象
window.initAnalytics = initAnalytics;

// 切换分析类型的事件监听
const analysisTypeElement = document.getElementById("analysis-type");
if (analysisTypeElement) {
  analysisTypeElement.addEventListener("change", loadAnalyticsData);
}

// 加载分析数据
async function loadAnalyticsData() {
  const analysisType = document.getElementById("analysis-type").value;
  const analyticsContainer = document.getElementById("analytics-container");
  analyticsContainer.innerHTML =
    '<div class="loading"><i class="fas fa-spinner fa-spin"></i> 加载分析数据中...</div>';

  // 清除现有图表
  chartInstances.forEach((chart) => chart.destroy());
  chartInstances = [];

  try {
    // 根据选择的分析类型加载不同的数据
    switch (analysisType) {
      case "score-distribution":
        await renderScoreDistribution();
        break;
      case "course-comparison":
        await renderCourseComparison();
        break;
      case "student-trend":
        await renderStudentTrend();
        break;
      case "correlation-analysis":
        await renderCorrelationAnalysis();
        break;
      default:
        await renderScoreDistribution();
    }
  } catch (error) {
    analyticsContainer.innerHTML =
      '<div class="error-message">分析数据加载失败，请重试</div>';
    console.error("Error loading analytics data:", error);
  }
}
// 辅助：分析API基础地址
const ANALYSIS_API_BASE =
  (window.API_URL || "/api") + "/analysis";

// 从分析后端获取成绩统计
async function fetchGradeStatistics(params = {}) {
  const url = new URL(ANALYSIS_API_BASE + "/grade-statistics");
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== "")
      url.searchParams.append(k, v);
  });

  const user = JSON.parse(localStorage.getItem("user"));
  const res = await fetch(url.toString(), {
    headers: {
      "Content-Type": "application/json",
      ...(user && user.token ? { Authorization: `Bearer ${user.token}` } : {}),
    },
  });
  if (!res.ok) throw new Error("加载成绩统计失败: " + res.status);
  return await res.json();
}

// 渲染成绩分布图表（使用后端分析结果）
async function renderScoreDistribution() {
  try {
    const analyticsContainer = document.getElementById("analytics-container");
    analyticsContainer.innerHTML = `
            <h3>成绩分布分析</h3>
            <div class="chart-container">
                <canvas id="scoreDistributionChart"></canvas>
            </div>
            <div class="chart-info">
                <p>该图表显示了所有学生成绩的分布情况，帮助了解整体学习状况。</p>
            </div>
        `;

    // 获取后端总体成绩统计
    const stats = await fetchGradeStatistics();

    const dist =
      stats && stats.scoreDistribution ? stats.scoreDistribution : {};
    const binLabels = ["90-100", "80-89", "70-79", "60-69", "0-59"];
    const binCounts = binLabels.map((label) => dist[label] || 0);

    // 创建图表
    const ctx = document
      .getElementById("scoreDistributionChart")
      .getContext("2d");
    const chart = new Chart(ctx, {
      type: "bar",
      data: {
        labels: binLabels,
        datasets: [
          {
            label: "学生数量",
            data: binCounts,
            backgroundColor: [
              "rgba(255, 99, 132, 0.7)",
              "rgba(255, 159, 64, 0.7)",
              "rgba(255, 205, 86, 0.7)",
              "rgba(75, 192, 192, 0.7)",
              "rgba(54, 162, 235, 0.7)",
            ],
            borderColor: [
              "rgb(255, 99, 132)",
              "rgb(255, 159, 64)",
              "rgb(255, 205, 86)",
              "rgb(75, 192, 192)",
              "rgb(54, 162, 235)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              precision: 0,
            },
          },
        },
        plugins: {
          title: {
            display: true,
            text: "成绩分布统计图",
            font: {
              size: 16,
            },
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                return `学生数量: ${context.raw}`;
              },
            },
          },
        },
      },
    });

    chartInstances.push(chart);
  } catch (error) {
    const analyticsContainer = document.getElementById("analytics-container");
    analyticsContainer.innerHTML =
      '<div class="error-message">成绩分布分析加载失败，请重试</div>';
    console.error("Error loading score distribution:", error);
  }
  // 渲染课程对比图表（使用后端分析API）
  async function renderCourseComparison() {
    const analyticsContainer = document.getElementById("analytics-container");
    analyticsContainer.innerHTML = `
        <h3>课程平均分对比</h3>
        <div class="chart-container">
            <canvas id="courseComparisonChart"></canvas>
        </div>
        <div class="chart-info">
            <p>该图表显示了各课程的平均分数对比，帮助了解不同课程的整体表现。</p>
        </div>
    `;

    try {
      // 获取课程数据
      const courses = await apiRequest("courses");

      if (courses && courses.length > 0) {
        // 为每门课程获取统计信息
        const courseAverages = [];
        const courseLabels = [];

        for (const course of courses.slice(0, 10)) {
          // 限制前10门课程
          try {
            const stats = await fetchGradeStatistics({ courseId: course.id });
            if (stats && stats.averageScore) {
              courseAverages.push(stats.averageScore);
              courseLabels.push(`${course.name} (${course.code})`);
            }
          } catch (e) {
            console.warn(`获取课程 ${course.name} 统计失败:`, e);
          }
        }

        if (courseAverages.length > 0) {
          // 创建图表
          const ctx = document
            .getElementById("courseComparisonChart")
            .getContext("2d");
          const chart = new Chart(ctx, {
            type: "bar",
            data: {
              labels: courseLabels,
              datasets: [
                {
                  label: "平均分",
                  data: courseAverages,
                  backgroundColor: "rgba(75, 192, 192, 0.7)",
                  borderColor: "rgb(75, 192, 192)",
                  borderWidth: 1,
                },
              ],
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              scales: {
                y: {
                  beginAtZero: true,
                  max: 100,
                  title: {
                    display: true,
                    text: "平均分",
                  },
                },
              },
              plugins: {
                title: {
                  display: true,
                  text: "课程平均分对比图",
                  font: {
                    size: 16,
                  },
                },
                tooltip: {
                  callbacks: {
                    label: function (context) {
                      return `平均分: ${context.raw.toFixed(2)}`;
                    },
                  },
                },
              },
            },
          });

          chartInstances.push(chart);
        } else {
          analyticsContainer.innerHTML =
            '<div class="error-message">没有找到课程统计数据</div>';
        }
      } else {
        analyticsContainer.innerHTML =
          '<div class="error-message">没有找到课程数据</div>';
      }
    } catch (error) {
      const analyticsContainer = document.getElementById("analytics-container");
      analyticsContainer.innerHTML =
        '<div class="error-message">课程对比分析加载失败，请重试</div>';
      console.error("Error loading course comparison:", error);
    }

    // 渲染学生趋势图表
    async function renderStudentTrend() {
      try {
        const analyticsContainer = document.getElementById(
          "analytics-container"
        );

        // 获取学生和成绩数据
        const students = await apiRequest("students");
        const scores = await apiRequest("scores");
        const courses = await apiRequest("courses");

        if (
          students &&
          students.length > 0 &&
          scores &&
          scores.length > 0 &&
          courses &&
          courses.length > 0
        ) {
          // 构建学生选择下拉框
          analyticsContainer.innerHTML = `
            <h3>学生成绩趋势分析</h3>
            <div class="form-group">
                <label for="student-select-analysis">选择学生:</label>
                <select id="student-select-analysis" required></select>
            </div>
            <div class="chart-container">
                <canvas id="studentTrendChart"></canvas>
            </div>
            <div class="chart-info">
                <p>该图表显示了选定学生的成绩变化趋势。</p>
            </div>
        `;

          // 填充学生选择下拉框
          const studentSelect = document.getElementById(
            "student-select-analysis"
          );
          students.forEach((student) => {
            const option = document.createElement("option");
            option.value = student.id;
            option.textContent = `${student.name} (${student.studentNumber})`;
            studentSelect.appendChild(option);
          });

          // 初始渲染第一个学生的趋势
          renderStudentChart(studentSelect.value);

          // 添加选择事件监听
          studentSelect.addEventListener("change", function () {
            renderStudentChart(this.value);
          });
        } else {
          analyticsContainer.innerHTML =
            '<div class="error-message">没有找到足够的学生、课程或成绩数据</div>';
        }
      } catch (error) {
        const analyticsContainer = document.getElementById(
          "analytics-container"
        );
        analyticsContainer.innerHTML =
          '<div class="error-message">学生趋势分析加载失败，请重试</div>';
        console.error("Error loading student trend analysis:", error);
      }
    }
    // 渲染特定学生的图表
    function renderStudentChart(studentId) {
      // 清除现有图表
      chartInstances.forEach((chart) => chart.destroy());
      chartInstances = [];

      // 获取相关数据
      const scores = window.scores || [];
      const courses = window.courses || [];

      // 筛选该学生的成绩
      const studentScores = scores.filter(
        (score) => score.studentId === parseInt(studentId)
      );

      if (studentScores.length === 0) {
        document
          .getElementById("studentTrendChart")
          .closest(".chart-container").innerHTML =
          '<div class="error-message">该学生没有成绩数据</div>';
        return;
      }

      // 按创建时间排序
      studentScores.sort(
        (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
      );

      // 准备图表数据
      const labels = studentScores.map((score, index) => {
        const course = courses.find((c) => c.id === score.courseId);
        return `${course ? course.courseName : `课程ID: ${score.courseId}`} (${
          index + 1
        })`;
      });

      const data = studentScores.map((score) => score.scoreValue);

      // 创建图表
      const ctx = document.getElementById("studentTrendChart").getContext("2d");
      const chart = new Chart(ctx, {
        type: "line",
        data: {
          labels: labels,
          datasets: [
            {
              label: "成绩",
              data: data,
              backgroundColor: "rgba(54, 162, 235, 0.2)",
              borderColor: "rgb(54, 162, 235)",
              borderWidth: 2,
              tension: 0.1,
              fill: false,
              pointBackgroundColor: "rgb(54, 162, 235)",
              pointRadius: 4,
              pointHoverRadius: 6,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              title: {
                display: true,
                text: "成绩",
              },
            },
            x: {
              title: {
                display: true,
                text: "课程及成绩记录",
              },
            },
          },
          plugins: {
            title: {
              display: true,
              text: "学生成绩趋势图",
              font: {
                size: 16,
              },
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  return `成绩: ${context.raw.toFixed(2)}`;
                },
              },
            },
          },
        },
      });

      chartInstances.push(chart);
    }

    // 渲染相关性分析图表（使用后端分析API）
    async function renderCorrelationAnalysis() {
      const analyticsContainer = document.getElementById("analytics-container");
      analyticsContainer.innerHTML = `
        <h3>成绩相关性分析</h3>
        <div class="chart-container">
            <canvas id="correlationChart"></canvas>
        </div>
        <div class="chart-info">
            <p>该图表显示了不同课程之间的成绩相关性，帮助了解课程间的关联程度。</p>
        </div>
    `;

      try {
        // 获取课程数据
        const courses = await apiRequest("courses");

        if (courses && courses.length >= 2) {
          // 选择前5门课程进行相关性分析
          const selectedCourses = courses.slice(0, 5);
          const correlationData = [];
          const courseNames = selectedCourses.map((course) => course.name);

          // 计算每对课程的相关性
          for (let i = 0; i < selectedCourses.length; i++) {
            for (let j = i + 1; j < selectedCourses.length; j++) {
              try {
                const response = await fetch(
                  `${ANALYSIS_API_BASE}/course-correlation?courseId1=${selectedCourses[i].id}&courseId2=${selectedCourses[j].id}`,
                  {
                    headers: {
                      "Content-Type": "application/json",
                      ...(localStorage.getItem("user")
                        ? {
                            Authorization: `Bearer ${
                              JSON.parse(localStorage.getItem("user")).token
                            }`,
                          }
                        : {}),
                    },
                  }
                );

                if (response.ok) {
                  const correlation = await response.json();
                  correlationData.push({
                    x: i,
                    y: j,
                    r: Math.abs(correlation.correlationCoefficient) * 10 + 5, // 气泡大小
                    v: correlation.correlationCoefficient,
                    course1Name: correlation.course1.name,
                    course2Name: correlation.course2.name,
                  });
                }
              } catch (e) {
                console.warn(
                  `获取课程相关性失败: ${selectedCourses[i].name} vs ${selectedCourses[j].name}`,
                  e
                );
              }
            }
          }

          if (correlationData.length > 0) {
            // 创建气泡图
            const ctx = document
              .getElementById("correlationChart")
              .getContext("2d");
            const chart = new Chart(ctx, {
              type: "bubble",
              data: {
                datasets: [
                  {
                    label: "课程相关性",
                    data: correlationData,
                    backgroundColor: correlationData.map((item) => {
                      const value = item.v;
                      if (value >= 0.7) return "rgba(40, 167, 69, 0.7)"; // 绿色
                      if (value >= 0.3) return "rgba(75, 192, 192, 0.7)"; // 青色
                      if (value >= -0.3) return "rgba(255, 205, 86, 0.7)"; // 黄色
                      if (value >= -0.7) return "rgba(255, 159, 64, 0.7)"; // 橙色
                      return "rgba(255, 99, 132, 0.7)"; // 红色
                    }),
                    borderColor: correlationData.map((item) => {
                      const value = item.v;
                      if (value >= 0.7) return "rgb(40, 167, 69)";
                      if (value >= 0.3) return "rgb(75, 192, 192)";
                      if (value >= -0.3) return "rgb(255, 205, 86)";
                      if (value >= -0.7) return "rgb(255, 159, 64)";
                      return "rgb(255, 99, 132)";
                    }),
                    borderWidth: 1,
                  },
                ],
              },
              options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  x: {
                    type: "category",
                    labels: courseNames,
                    title: {
                      display: true,
                      text: "课程",
                    },
                  },
                  y: {
                    type: "category",
                    labels: courseNames,
                    title: {
                      display: true,
                      text: "课程",
                    },
                  },
                },
                plugins: {
                  title: {
                    display: true,
                    text: "课程成绩相关性气泡图",
                    font: {
                      size: 16,
                    },
                  },
                  legend: {
                    display: false,
                  },
                  tooltip: {
                    callbacks: {
                      title: function (items) {
                        const item = items[0];
                        return `${item.raw.course1Name} vs ${item.raw.course2Name}`;
                      },
                      label: function (context) {
                        return `相关系数: ${context.raw.v.toFixed(2)}`;
                      },
                    },
                  },
                },
              },
            });

            chartInstances.push(chart);
          } else {
            analyticsContainer.innerHTML =
              '<div class="error-message">无法获取课程相关性数据</div>';
          }
        } else {
          analyticsContainer.innerHTML =
            '<div class="error-message">没有找到足够的课程进行相关性分析</div>';
        }
      } catch (error) {
        const analyticsContainer = document.getElementById(
          "analytics-container"
        );
        analyticsContainer.innerHTML =
          '<div class="error-message">相关性分析加载失败，请重试</div>';
        console.error("Error loading correlation analysis:", error);
      }
    }
  }
}
