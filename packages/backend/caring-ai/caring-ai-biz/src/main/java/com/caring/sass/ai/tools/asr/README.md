# 音频转文字 (ASR) 工具

此模块实现了阿里云通义千问音频转文字功能，包括两个核心工具：

## 功能

1. **音频转文字提交工具** - 用于提交音频文件进行语音识别
2. **音频转文字查询工具** - 用于查询已提交任务的状态和结果

## 配置

在配置文件中添加以下配置：

```yaml
caring:
  asr:
    api-key: your_dashscope_api_key_here
```

## 使用方法

### 提交音频转文字任务

使用 `AsrSubmitTool` 类的 `submitAudioToText` 方法提交音频文件进行转文字：

```java
String taskId = asrSubmitTool.submitAudioToText("https://example.com/audio.mp3");
```

### 查询转文字结果

使用 `AsrQueryTool` 类的 `queryAudioToTextResult` 方法查询任务状态：

```java
String result = asrQueryTool.queryAudioToTextResult("task_id_returned_from_submit");
```

## 依赖

- Gson - 用于JSON序列化/反序列化
- OkHttp3 - 用于HTTP请求
- LangChain4j - 用于工具注解

## 模型

使用 `qwen3-asr-flash-filetrans` 模型进行音频转文字处理，支持中文等多种语言。