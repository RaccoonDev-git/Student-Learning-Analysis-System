# AI 明文密钥配置指引

## 修改位置
- 文件：`backend/src/main/resources/application.properties`
- 段落：`# DeepSeek配置 - 优化版本`
- 键名：`ai.middleware.models.deepseek.api-key`

## 操作步骤
1. 打开上述配置文件，找到 DeepSeek 配置段。
2. 在注释 `# 在部署前请在下方配置项中填写明文API密钥` 下，把 `ai.middleware.models.deepseek.api-key=` 的值设为实际密钥，例如：
   ```
   ai.middleware.models.deepseek.api-key=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```
3. 需要访问外部服务时，同时在 `ai.middleware.models.deepseek.endpoint=` 填写 DeepSeek 提供的 API 地址。
4. 保存文件并重启后端服务，使配置生效。

> 提示：仓库默认不包含生产密钥，请将真实值保存在受控环境，并考虑使用环境变量或密钥管理服务进一步提升安全性。

