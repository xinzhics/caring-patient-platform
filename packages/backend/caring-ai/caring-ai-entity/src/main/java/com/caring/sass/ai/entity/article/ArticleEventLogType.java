package com.caring.sass.ai.entity.article;

public enum ArticleEventLogType {


    /**
     * 用户登录
     */
    LOGIN("LOGIN", "用户登录", 0),

    /**
     * AI文案生成器
     */
    AI_CREATE_ARTICLE("AI_CREATE_ARTICLE", "AI文案生成器", 34),


    /**
     * 选择科普文本
     */
    SELECT_SCIENCE_TEXT("SELECT_SCIENCE_TEXT", "选择科普文本", 35),

    /**
     * 科普原创 提要求
     */
    SCIENCE_ORIGINAL_REQUIREMENT("SCIENCE_ORIGINAL_REQUIREMENT", "科普原创 提要求", 36),

    /**
     * 科普原创 重写大纲
     */
    SCIENCE_ORIGINAL_REWRITE_OUTLINE("SCIENCE_ORIGINAL_REWRITE_OUTLINE", "科普原创 重写大纲", 37),

    /**
     * 科普原创 写正文
     */
    SCIENCE_ORIGINAL_WRITE_CONTENT("SCIENCE_ORIGINAL_WRITE_CONTENT", "科普原创 写正文", 38),
    /**
     * 科普原创 重写正文
     */
    SCIENCE_ORIGINAL_REWRITE_CONTENT("SCIENCE_ORIGINAL_REWRITE_CONTENT", "科普原创 重写正文", 39),
    /**
     * 科普原创 写标题
     */
    SCIENCE_ORIGINAL_WRITE_TITLE("SCIENCE_ORIGINAL_WRITE_TITLE", "科普原创 写标题", 40),
    /**
     * 科普原创 重写标题
     */
    SCIENCE_ORIGINAL_REWRITE_TITLE("SCIENCE_ORIGINAL_REWRITE_TITLE", "科普原创 重写标题", 41),

    /**
     * 科普原创 保存到作品集
     */
    SCIENCE_ORIGINAL_SAVE_PORTFOLIO("SCIENCE_ORIGINAL_SAVE_PORTFOLIO", "科普原创 保存到作品集", 42),

    /**
     * 科普原创 设置标题
     */
    SCIENCE_ORIGINAL_SET_TITLE("SCIENCE_ORIGINAL_SET_TITLE", "科普原创 设置标题", 43),

    /**
     * 科普原创 复制文本
     */
    SCIENCE_ORIGINAL_COPY_TEXT("SCIENCE_ORIGINAL_COPY_TEXT", "科普原创 复制文本", 44),

    /**
     * 科普选择 仿写
     */
    SCIENCE_SELECT_COPY_TEXT("SCIENCE_SELECT_COPY_TEXT", "科普选择 仿写", 46),

    /**
     * 科普仿写 提交设置
     */
    SCIENCE_COPY_TEXT_SUBMIT_SETTING("SCIENCE_COPY_TEXT_SUBMIT_SETTING", "科普仿写 提交设置", 47),

    /**
     * 科普仿写重写文案
     */
    SCIENCE_COPY_TEXT_REWRITE_TEXT("SCIENCE_COPY_TEXT_REWRITE_TEXT", "科普仿写 重写文案", 48),

    /**
     * 科普仿写保存到作品集
     */
    SCIENCE_COPY_TEXT_SAVE_PORTFOLIO("SCIENCE_COPY_TEXT_SAVE_PORTFOLIO", "科普仿写 保存到作品集", 49),

    /**
     * 科普仿写复制文本
     */
    SCIENCE_COPY_TEXT_COPY_TEXT("SCIENCE_COPY_TEXT_COPY_TEXT", "科普仿写 复制文本", 50),


    /**
     * 选择 媒体文案
     */
    SELECT_MEDIA_TEXT("SELECT_MEDIA_TEXT", "选择媒体文案", 52),

    /**
     * 媒体原创文案提交设置
     */
    MEDIA_ORIGINAL_SUBMIT_SETTING("MEDIA_ORIGINAL_SUBMIT_SETTING", "媒体原创文案提交设置", 53),

    /**
     * 媒体原创文案重写
     */
    MEDIA_ORIGINAL_REWRITE_TEXT("MEDIA_ORIGINAL_REWRITE_TEXT", "媒体原创文案重写", 54),

    /**
     * 媒体原创文案保存到作品集
     */
    MEDIA_ORIGINAL_SAVE_PORTFOLIO("MEDIA_ORIGINAL_SAVE_PORTFOLIO", "媒体原创文案保存到作品集", 55),

    /**
     * 媒体原创文案复制文本
     */
    MEDIA_ORIGINAL_COPY_TEXT("MEDIA_ORIGINAL_COPY_TEXT", "媒体原创文案复制文本", 56),

    /**
     * 媒体仿写文案提交
     */
    MEDIA_COPY_TEXT_SUBMIT("MEDIA_COPY_TEXT_SUBMIT", "媒体仿写文案提交",57),

    /**
     * 媒体仿写文案重写
     */
    MEDIA_COPY_TEXT_REWRITE_TEXT("MEDIA_COPY_TEXT_REWRITE_TEXT", "媒体仿写文案重写", 58),

    /**
     * 媒体仿写文案保存到作品集
     */
    MEDIA_COPY_TEXT_SAVE_PORTFOLIO("MEDIA_COPY_TEXT_SAVE_PORTFOLIO", "媒体仿写文案保存到作品集", 59),

    /**
     * 媒体仿写文案复制文本
     */
    MEDIA_COPY_TEXT_COPY_TEXT("MEDIA_COPY_TEXT_COPY_TEXT", "媒体仿写文案复制文本", 60),


    /**
     * 选择口播文案
     */
    SELECT_VOICE_TEXT("SELECT_VOICE_TEXT", "选择口播文案", 61),

    /**
     * 口播 原创文案提交设置
     */
    VOICE_ORIGINAL_SUBMIT_SETTING("VOICE_ORIGINAL_SUBMIT_SETTING", "口播 原创文案提交设置", 62),

    /**
     * 口播原创文案重写
     */
    VOICE_ORIGINAL_REWRITE_TEXT("VOICE_ORIGINAL_REWRITE_TEXT", "口播原创文案重写", 63),

    /**
     * 口播 原创保存到作品集
     */
    VOICE_ORIGINAL_SAVE_PORTFOLIO("VOICE_ORIGINAL_SAVE_PORTFOLIO", "口播 原创文案保存到作品集", 64),

    /**
     * 口播 原创复制文本
     */
    VOICE_ORIGINAL_COPY_TEXT("VOICE_ORIGINAL_COPY_TEXT", "口播 原创复制文本", 65),

    /**
     * 口播仿写提交设置
     */
    VOICE_COPY_TEXT_SUBMIT_SETTING("VOICE_COPY_TEXT_SUBMIT_SETTING", "口播仿写提交设置", 66),

    /**
     * 口播 仿写 重写文案
     */
    VOICE_COPY_TEXT_REWRITE_TEXT("VOICE_COPY_TEXT_REWRITE_TEXT", "口播 仿写文案重写", 67),

    /**
     * 口播仿写保存到作品集
     */
    VOICE_COPY_TEXT_SAVE_PORTFOLIO("VOICE_COPY_TEXT_SAVE_PORTFOLIO", "口播仿写保存到作品集", 68),

    /**
     * 口播仿写复制文本
     */
    VOICE_COPY_TEXT_COPY_TEXT("VOICE_COPY_TEXT_COPY_TEXT", "口播仿写复制文本", 69),


    /**
     * 形象定制
     */
    HUMAN_VIDEO_CREATION("HUMAN_VIDEO_CREATION", "形象定制", 71),

    /**
     * 形象授权须知
     */
    HUMAN_VIDEO_AUTH_NOTICE("HUMAN_VIDEO_AUTH_NOTICE", "形象授权须知", 72),
    /**
     * 形象名称输入
     */
    HUMAN_VIDEO_NAME_INPUT("HUMAN_VIDEO_NAME_INPUT", "形象名称输入", 73),

    /**
     * 形象上传图片
     */
    HUMAN_VIDEO_UPLOAD_IMAGE("HUMAN_VIDEO_UPLOAD_IMAGE", "形象上传图片", 74),
    /**
     * 形象上传视频
     */
    HUMAN_VIDEO_UPLOAD_VIDEO("HUMAN_VIDEO_UPLOAD_VIDEO", "形象上传视频", 75),

    /**
     * 形象提取音色
     */
    HUMAN_VIDEO_EXTRACT_VOICE("HUMAN_VIDEO_EXTRACT_VOICE", "形象提取音色", 76),

    /**
     * 形象开始训练
     */
    HUMAN_VIDEO_TRAINING("HUMAN_VIDEO_TRAINING", "形象开始训练", 77),


    /**
     * 音色定制
     */
    VOICE_CREATION("VOICE_CREATION", "音色定制", 79),

    /**
     * 音色名称输入
     */
    VOICE_NAME_INPUT("VOICE_NAME_INPUT", "音色名称输入", 80),

    /**
     * 上传音色文件
     */
    VOICE_UPLOAD_FILE("VOICE_UPLOAD_FILE", "上传音色文件", 81),

    /**
     * 音色开始克隆
     */
    VOICE_CLONE_START("VOICE_CLONE_START", "音色开始克隆", 82),


    /**
     * 数字人视频
     */
    HUMAN_VIDEO("HUMAN_VIDEO", "数字人视频", 84),
    /**
     * 数字人 选择形象
     */
    HUMAN_VIDEO_SELECT_IMAGE("HUMAN_VIDEO_SELECT_IMAGE", "数字人 选择形象", 85),
    /**
     * 数字人 选择音色
     */
    HUMAN_VIDEO_SELECT_VOICE("HUMAN_VIDEO_SELECT_VOICE", "数字人 选择音色", 86),
    /**
     * 数字人 创作文本
     */
    HUMAN_VIDEO_CREATE_TEXT("HUMAN_VIDEO_CREATE_TEXT", "数字人 创作文本", 87),
    /**
     * 数字人 开始生成
     */
    HUMAN_VIDEO_START_CREATE("HUMAN_VIDEO_START_CREATE", "数字人 开始生成", 88),
    /**
     * 数字人 下载
     */
    HUMAN_VIDEO_DOWNLOAD("HUMAN_VIDEO_DOWNLOAD", "数字人 下载", 89),
    /**
     * 数字人在线播放
     */
    HUMAN_VIDEO_ONLINE_PLAY("HUMAN_VIDEO_ONLINE_PLAY", "数字人在线播放", 90),


    /**
     * 播客 创建
     */
    BLOG_CREATE("BLOG_CREATE", "播客 创建", 92),
    /**
     * 播客 输入文本要求
     */
    BLOG_INPUT_TEXT_REQUIREMENT("BLOG_INPUT_TEXT_REQUIREMENT", "播客 输入文本要求", 93),

    /**
     * 播客设置声音
     */
    BLOG_SET_VOICE("BLOG_SET_VOICE", "播客 设置声音", 94),

    /**
     * 播客 预览生成内容
     */
    BLOG_PREVIEW_CREATE_CONTENT("BLOG_PREVIEW_CREATE_CONTENT", "播客 预览生成内容", 95),

    /**
     * 播客 放弃预览内容
     */
     BLOG_ABANDON_PREVIEW_CONTENT("BLOG_ABANDON_PREVIEW_CONTENT", "播客 放弃预览内容", 96),
    /**
     * 播客 编辑内容
     */
    BLOG_EDIT_CONTENT("BLOG_EDIT_CONTENT", "播客 编辑内容", 97),
    /**
     * 播客 增加对话
     */
    BLOG_ADD_DIALOG("BLOG_ADD_DIALOG", "播客 增加对话", 98),
    /**
     * 播客 删除对话
     */
    BLOG_DELETE_DIALOG("BLOG_DELETE_DIALOG", "播客 删除对话", 99),
    /**
     * 播客 提交音频内容
     */
    BLOG_SUBMIT_AUDIO_CONTENT("BLOG_SUBMIT_AUDIO_CONTENT", "播客 提交音频内容", 100),
    /**
     * 播客 下载音频
     */
    BLOG_DOWNLOAD_AUDIO("BLOG_DOWNLOAD_AUDIO", "播客 下载音频", 101),
    ;

    String code;
    String name;
    Integer rowIdx;


    ArticleEventLogType(String code, String name, Integer rowIdx) {
        this.code = code;
        this.name = name;
        this.rowIdx = rowIdx;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getRowIdx() {
        return rowIdx;
    }
}
