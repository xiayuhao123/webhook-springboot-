# Webhook Bridge (Spring Boot + Qwen3)

一个最小可行的 Webhook 桥接服务：接收通讯软件推送的消息，调用 Qwen3 API 获取回复并回传结果。项目基于 Spring Boot 3.3 + Maven，支持直接在 IntelliJ IDEA 中导入运行。

## 功能特性
- `POST /api/webhook/messages`：对接任意通讯/协同系统的 Webhook。
- Prompt 构建器：自动汇总发送方、渠道、附件、元数据。
- Qwen3 Chat Completions 客户端：可配置模型、温度、超时、系统提示词。
- 全局异常处理 + Bean Validation，快速定位错误。
- Actuator 健康检查，方便部署监控。
- 文档齐全：需求说明、API 文档、README。

## 快速开始
```bash
mvn clean package
java -jar target/webhook-bridge-0.0.1-SNAPSHOT.jar
# 或直接：
mvn spring-boot:run
```

启动后默认监听 `http://localhost:8080/bridge`。

### 必要配置
| 环境变量 / `application.yml` | 说明 |
| --- | --- |
| `QWEN_API_KEY` / `ai.qwen.api-key` | Qwen3 访问凭证 |
| `ai.qwen.model` | 模型（默认 `qwen-plus`） |
| `ai.qwen.temperature` / `top-p` | 可根据业务调优 |

> 未设置 API Key 时仍可启动用于本地联调，但调用 Qwen 会因鉴权失败而报错。

### 调试示例
```bash
curl -X POST http://localhost:8080/bridge/api/webhook/messages \
  -H "Content-Type: application/json" \
  -d '{
        "messageId":"msg-1",
        "conversationId":"conv-1",
        "channel":"demo",
        "text":"Hello?",
        "sender":{"id":"u1","displayName":"Tester"}
      }'
```

## 目录结构
```
├── src/main/java/com/example/webhookbridge
│   ├── controller        # Webhook 接口 & 全局异常
│   ├── service           # 转发业务逻辑
│   ├── client            # Qwen3 HTTP 客户端
│   ├── config/support    # 配置与公共工具
│   └── model             # DTO
├── src/test/java/...     # Web 层单测
├── docs/requirements.md  # 需求文档
├── docs/api.md           # API 文档
└── pom.xml
```

## 文档 & 后续
- 需求背景与假设：`docs/requirements.md`
- 接口细节与示例：`docs/api.md`

如需进一步扩展（签名校验、多租户、消息持久化、异步回调等），可在现有分层基础上继续迭代。欢迎根据实际业务补充文档中的「待确认」部分。 
