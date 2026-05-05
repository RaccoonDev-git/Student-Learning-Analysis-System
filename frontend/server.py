from flask import Flask, send_from_directory, request, jsonify, Response
import os
import requests

app = Flask(__name__, static_folder=".")

# 后端服务器地址
BACKEND_URL = "http://localhost:8082"


# 允许跨域请求
@app.after_request
def after_request(response):
    response.headers.add("Access-Control-Allow-Origin", "*")
    response.headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization")
    response.headers.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS")
    return response


# 处理静态文件
@app.route("/")
def index():
    return send_from_directory(".", "index.html")


@app.route("/<path:path>")
def static_files(path):
    # 检查请求的路径是否存在
    if os.path.exists(os.path.join(".", path)):
        return send_from_directory(".", path)
    # 如果请求的路径没有扩展名，检查是否存在对应的HTML文件
    elif "." not in path:
        html_path = path + ".html"
        if os.path.exists(os.path.join(".", html_path)):
            return send_from_directory(".", html_path)
    # 如果文件不存在，返回404
    return "File not found", 404


# 代理API请求到后端服务器
@app.route("/api/<path:path>", methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"])
def proxy_api(path):
    # 构造后端URL - 包含完整的/api前缀，与后端安全配置匹配
    backend_url = f"{BACKEND_URL}/api/{path}"

    # 添加日志输出
    print(f"\n[代理请求] 路径: {path}")
    print(f"[代理请求] 后端URL: {backend_url}")
    print(f"[代理请求] 方法: {request.method}")
    print(f"[代理请求] 数据: {request.get_data().decode('utf-8')}")
    print(f"[代理请求] 头信息: {dict(request.headers)}")

    # 获取请求方法和数据
    method = request.method
    headers = {key: value for (key, value) in request.headers if key != "Host"}
    data = request.get_data()

    try:
        # 转发请求到后端
        resp = requests.request(
            method=method,
            url=backend_url,
            headers=headers,
            data=data,
            params=request.args,
            allow_redirects=False,
        )

        # 添加响应日志
        print(f"[代理响应] 状态码: {resp.status_code}")
        print(f"[代理响应] 响应头: {dict(resp.headers)}")
        print(f"[代理响应] 响应内容: {resp.text}")

        # 返回后端响应
        excluded_headers = [
            "content-encoding",
            "content-length",
            "transfer-encoding",
            "connection",
        ]
        headers = [
            (name, value)
            for (name, value) in resp.raw.headers.items()
            if name.lower() not in excluded_headers
        ]

        response = Response(resp.content, resp.status_code, headers)
        return response
    except Exception as e:
        return jsonify({"error": "无法连接到后端服务器"}), 500


if __name__ == "__main__":
    from flask import Response

    app.run(host="localhost", port=3000, debug=True)
