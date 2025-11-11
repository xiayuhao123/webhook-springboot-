# Webhook API 文档

## 基本信息
- **Base URL**：`http://localhost:8080/bridge`
- **版本**：v1（通过路径 `/api/webhook` 体现）
- **鉴权**：Webhook 入站暂不校验（可加签名/Token 扩展）；下游 Qwen3 需 `QWEN_API_KEY`。

## 1. POST `/api/webhook/messages`
接收通讯软件推送的用户消息，实时转发到 Qwen3，并返回回复。

### 请求体
```json
{
  "messageId": "msg-20241111-001",
  "conversationId": "conv-7788",
  "channel": "private_chat",
  "text": "我无法登录系统，应该怎么办？",
  "receivedAt": "2025-11-11T13:25:10+08:00",
  "sender": {
    "id": "user-001",
    "displayName": "张三",
    "email": "zhangsan@example.com"
  },
  "attachments": [
    {
      "type": "screenshot",
      "url": "https://example.com/snap.png",
      "name": "login-error.png"
    }
  ],
  "metadata": {
    "tenant": "alpha",
    "clientVersion": "5.2.1"
  }
}
```

### 响应
```json
{
  "status": "DELIVERED",
  "relayId": "8b77dc26-a45c-4d99-a3ff-f4dbdd6ad2d8",
  "aiReply": "您好，建议先通过“忘记密码”重置，如仍失败请提供报错截图。",
  "respondedAt": "2025-11-11T13:25:11.145Z",
  "notes": "Message relayed to Qwen successfully."
}
```

### 校验规则
| 字段 | 规则 |
| --- | --- |
| `messageId`/`conversationId`/`channel`/`text` | 必填，非空字符串 |
| `sender.id`、`sender.displayName` | 必填 |
| `sender.email` | 可选，如提供需满足 Email 格式 |
| `attachments[].type/url` | 如出现附件，type 与 url 必填 |

### 典型错误
| HTTP 状态 | 返回示例 | 说明 |
| --- | --- | --- |
| 400 | `{ "error": "Validation error", "message": "text: text is required" }` | 字段校验失败 |
| 502 | `{ "error": "AI provider error", "message": "Qwen did not return any choices." }` | AI 响应异常 |
| 500 | `{ "error": "Internal error", "message": "..." }` | 服务器内部错误 |

## 2. GET `/api/webhook/ping`
健康检查，返回 `{ "status": "ok" }`，可用于上游在启动/定时检测时判断服务是否在线。

## 3. GET `/actuator/health`
来自 Spring Boot Actuator；生产部署可继续暴露 `info`、`metrics` 等端点。

## 本地模拟
1. 设置环境变量（Windows PowerShell 示例）：
   ```powershell
   $env:QWEN_API_KEY="sk-xxxx"
   ```
2. 启动应用：
   ```powershell
   mvn spring-boot:run
   ```
3. 发送测试消息：
   ```bash
   curl -X POST http://localhost:8080/bridge/api/webhook/messages \
     -H "Content-Type: application/json" \
     -d @sample-message.json
   ```
   `sample-message.json` 可参考上述请求体，或在 README 指引下自行构造。

## Webhook -> Qwen3 的调用说明
- 请求路径：`${ai.qwen.base-url}${ai.qwen.chat-path}`（默认 `https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions`）
- 请求头：`Authorization: Bearer <QWEN_API_KEY>`，`Content-Type: application/json`
- 请求体（示例）：
```json
{
  "model": "qwen-plus",
  "messages": [
    { "role": "system", "content": "You are the AI brain..." },
    { "role": "user", "content": "Conversation ID: conv-7788\n..." }
  ],
  "temperature": 0.7,
  "top_p": 0.8
}
```
- 响应解析：取 `choices[0].message.content` 作为 `aiReply`，若 `error` 字段存在则抛出 `AiProviderException`。

> 若 Qwen3 未来升级接口或需要多轮上下文，可扩展 `messages` 构造逻辑或引入会话存储模块。
