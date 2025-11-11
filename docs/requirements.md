# Webhook Bridge 需求说明

## 项目背景
- **场景**：某协同/通讯软件需要通过 Webhook 将消息转发给智能体（Qwen3 API）。本项目负责提供可部署在本地/私有云的 Spring Boot 服务，完成消息接收、AI 调用和结果回传。
- **目标**：提供一套参考实现，帮助快速理解 Webhook 转发与大模型联动的整体架构，并能在 IDE（IntelliJ IDEA）中直接打开运行。

## 系统角色
1. **通讯软件（上游）**：向本服务推送消息，调用 `/api/webhook/messages`。
2. **Webhook Bridge（本项目）**：校验消息、格式化上下文、调用 AI、记录及返回回复。
3. **Qwen3 AI 服务（下游）**：根据传入的 Prompt 生成回复。

## 业务流程
1. 上游将用户消息、会话信息、附件等字段以 JSON 形式 POST 给 Webhook。
2. 服务层根据模板构造 Prompt，调用 Qwen3 Chat Completions API。
3. 解析 AI 返回，封装成标准响应同步返回给上游。
4. 若调用失败，统一返回结构化错误信息，便于上游重试或告警。

## 功能需求
| 分类 | 需求 |
| --- | --- |
| 接口 | 提供 `POST /api/webhook/messages`；提供健康检查 `GET /api/webhook/ping`。|
| 数据模型 | 支持 messageId、conversationId、sender、channel、text、attachments、metadata 等字段，字段校验必须严格。 |
| Prompt 构建 | 将发件人、渠道、附件、元数据汇总成结构化 Prompt，保持可读性。 |
| AI 调用 | 调用 Qwen3 Chat Completions（兼容 OpenAI 风格），支持 model、temperature、topP、自定义 system prompt。 |
| 错误处理 | 将校验错误、AI 调用异常统一包装为 JSON，包含时间戳、HTTP 状态、错误描述。 |
| 可配置性 | 通过 `application.yml` / 环境变量配置 AI 相关参数（API Key、模型、超时等）。 |
| 文档 | 提供需求文档（本文）与 API 文档，说明假设、缺失信息、测试方法。 |

## 非功能需求
- **可部署性**：Maven 项目，JDK 17，支持 IDEA 直接导入。
- **可观测性**：启用 Spring Boot Actuator（health/info）。
- **安全**：默认使用 Bearer Token 传递 Qwen API Key；未提供 API Key 时可用于本地 Mock，但会在日志中给出警告（运行期可再扩展）。
- **可维护性**：分层结构（Controller / Service / Client / Support），使用 Validation 与全局异常处理。

## 配置项
| 属性 | 默认值 | 说明 |
| --- | --- | --- |
| `ai.qwen.api-key` | `${QWEN_API_KEY:}` | 必填，建议用环境变量。 |
| `ai.qwen.base-url` | `https://dashscope.aliyuncs.com/compatible-mode/v1` | Qwen3 兼容 OpenAI 的基地址。 |
| `ai.qwen.chat-path` | `/chat/completions` | Chat 接口路径。 |
| `ai.qwen.model` | `qwen-plus` | 可改为 `qwen-max` 等。 |
| `ai.qwen.temperature` | `0.7` | 采样温度。 |
| `ai.qwen.top-p` | `0.8` | Nucleus Sampling。 |
| `ai.qwen.timeout` | `25s` | HTTP 连接/读取超时。 |
| `ai.qwen.system-prompt` | 见 `application.yml` | 可替换为企业自定义策略。 |

## 约束与假设
- 通讯软件调用方式可通过 `curl`/Postman/自动化测试模拟。
- AI 回复长度控制在 150 词以内，语言与用户输入保持一致。
- 若实际业务需要异步回调，可在当前实现基础上扩展消息队列。

## 待确认/空缺信息
- 上游通讯软件的安全校验方式（签名/白名单等）——目前未实现，需要后续补充。
- 实际需要落库的内容（审计、对话历史）——当前仅在内存中处理。
- 是否需要多租户/多模型配置——如需支持，可将 `ai.qwen` 配置扩展成租户级别。

> 如需补充上述字段，请在此文档中继续完善。当前版本聚焦最小可行功能（MVP）。 
