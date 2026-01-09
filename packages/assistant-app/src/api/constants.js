export const Constants = {
  Required: 1,
  Gender: {
    Male: 0,
    Female: 1
  },
  /**
   * @description: Attr属性值
   * @author:
   * @date: 2019-12-27 19:16:53
   * @version: V1.0.0
   */
  CustomFormType: {
    NursingPlan: 1, // 护理计划
    HealthProfile: 2, // 健康档案
    BaseInfo: 4, // 基本信息
    ExamineData: 8, // 检验数据 （最开始只填一次）
    EcamineLog: 16, // 检验记录(最开始只填一次)
    HEALTH_LOG: 32 // 健康日志
  },

  CustomFormWidgetType: {
    // 单行文字
    SingleLineText: 'SingleLineText',
    // 多行文字
    MultiLineText: 'MultiLineText',
    // Radio单选
    Radio: 'Radio',
    // Check多选
    CheckBox: 'CheckBox',
    // Select下拉
    DropdownSelect: 'DropdownSelect',
    // 多级下拉
    MultiLevelDropdownSelect: 'MultiLevelDropdownSelect',
    // 图片单选
    SingleImageUpload: 'SingleImageUpload',
    // 图片多选
    MultiImageUpload: 'MultiImageUpload',
    // 数字
    Number: 'Number',
    // 上传文件
    UploadFile: 'UploadFile',
    // 时间
    Time: 'Time',
    // 日期
    Date: 'Date',
    // 日期时间
    DateTime: 'DateTime',
    // 分页
    Pagesplit: 'Pagesplit',
    // 素描
    Sketch: 'Sketch',
    // 网址
    Website: 'Website',
    // 地理为止
    Geography: 'Geography',
    // 姓名
    FullName: 'FullName',
    // 手机
    MobileNumber: 'MobileNumber',
    // 电话
    PhoneNumber: 'PhoneNumber',
    // 邮箱
    Email: 'Email',
    // 头像
    Avatar: 'Avatar',
    // 地址
    Address: 'Address',
    // 组合，自定义
    Compose: 'Compose',
    // 描述
    Desc: 'Desc',
    // 分页
    Page: 'Page',
    // 分割线
    SplitLine: 'SplitLine'
  },

  /**
   * CustomFormField的精准类型
   */
  FieldExactType: {
    // 身高
    Height: 'Height',
    // 体重
    Weight: 'Weight',
    // BMI
    BMI: 'BMI',
    // 血肌酐
    SCR: 'SCR',
    // 肾小球过滤
    GFR: 'GFR',
    // 肌酐清除率
    CCR: 'CCR',
    // 病程，肾病分期
    CourseOfDisease: 'CourseOfDisease',
    // 年龄
    Age: 'Age',
    // 性别
    Gender: 'Gender',
    // 出生年月日
    Birthday: 'Birthday',
    // 姓名
    Name: 'Name',
    // 手机
    Mobile: 'Mobile',
    // 邮箱
    Email: 'Email',
    // 座机电话
    Phone: 'Phone',
    // 地址
    Address: 'Address',
    // 诊断类型
    Diagnose: 'Diagnose',
    // 头像
    Avatar: 'Avatar',
    // 评分表单
    scoringSingleChoice: 'SCORING_SINGLE_CHOICE',
    // 校验日期
    CHECK_TIME: 'FormResultCheckTime'
  },
  Cms: {
    ChannelType: {
      // 图片文章
      Article: 'Article',
      // banner
      Banner: 'Banner',
      Reminder: 'Reminder',
      LearningPlan: 'LearningPlan'
    }
  }
}
