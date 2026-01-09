export const Constants = {
    upfileUrl: process.env.NODE_ENV === 'development' ? "./upfile" : './upfile',
    Required: 1,
    Gender: {
        Male: 0,
        Female: 1
    },
    /**
     * @description: Attr属性值
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
        SingleLineText: 'SingleLineText',                          // 单行文字
        MultiLineText: 'MultiLineText',                            // 多行文字
        Radio: 'Radio',                                            // Radio单选
        CheckBox: 'CheckBox',                                      // Check多选
        DropdownSelect: 'DropdownSelect',                          // Select下拉
        MultiLevelDropdownSelect: 'MultiLevelDropdownSelect',      // 多级下拉
        SingleImageUpload: 'SingleImageUpload',                    // 图片单选
        MultiImageUpload: 'MultiImageUpload',                      // 图片多选
        Number: 'Number',                                          // 数字
        UploadFile: 'UploadFile',                                  // 上传文件
        Time: 'Time',                                              // 时间
        Date: 'Date',                                              // 日期
        DateTime: 'DateTime',                                      // 日期时间
        Pagesplit: 'Pagesplit',                                    // 分页
        Sketch: 'Sketch',                                          // 素描
        Website: 'Website',                                        // 网址
        Geography: 'Geography',                                    // 地理为止
        FullName: 'FullName',                                      // 姓名
        MobileNumber: 'MobileNumber',                              // 手机
        PhoneNumber: 'PhoneNumber',                                // 电话
        Email: 'Email',                                            // 邮箱
        Avatar : 'Avatar',                                         // 头像
        Address: 'Address',                                        // 地址
        Compose: 'Compose',                                        // 组合，自定义
        Desc: 'Desc',                                               // 描述
        Page: 'Page',                                               // 分页
        SplitLine: 'SplitLine'                                     // 分割线
    },

    /**
     * CustomFormField的精准类型
     */
    FieldExactType: {
        Height: 'Height', 				                           // 身高
        Weight: 'Weight',                                          // 体重
        BMI: 'BMI',                                                // BMI
        SCR: 'SCR', 	                                           // 血肌酐
        GFR: 'GFR',                                                // 肾小球过滤
        CCR: 'CCR',                                                // 肌酐清除率
        CourseOfDisease: 'CourseOfDisease',                        // 病程，肾病分期
        Age: 'Age',                                                // 年龄
        Gender: 'Gender',                                          // 性别
        Birthday: 'Birthday',                                      // 出生年月日
        Name: 'Name',                                              // 姓名
        Mobile: 'Mobile',                                          // 手机
        Email: 'Email',                                            // 邮箱
        Phone: 'Phone',                                            // 座机电话
        Address: 'Address',                                        // 地址
        Diagnose: 'Diagnose',                                      // 诊断类型
        Avatar: 'Avatar'                                           // 头像
    },

    Tag: {
        AttrType: {
            Single: 2,
            Multi: 3,
            DropDown: 6,
            Number: 8,
            Time: 10,
            Date: 11
        },
        AttrTypeText: {
            2: '单选',
            3: '多选',
            6: '下拉框',
            8: '数字',
            10: '时间',
            11: '日期'
        }
    },
    SMS: {
        Type: {
            Login: 'Login'
        }
    },
    Cms: {
        ChannelType: {
            Article: 'Article',  // 图片文章
            Banner: 'Banner',     // banner
            Reminder:'Reminder',
            LearningPlan:'LearningPlan'
        }
    },
    authUrl: {
        '001': '/ucenter',                                          //个人中心
        '002': '/user/med/list',                                    //我的药箱
        '003': '/user/patient/edit',                                //健康档案
        '004': '/user/patient/examineDataList',                     //检验数据
        '005': '/user/healthy/list',                                //健康日志
        '006': '/user/nurs/list',                                   //随访计划
        '007': '/jiance/list',                                      //监测数据
        '008': '/cms',                                              //cms
        '009': '/user/medCalendar',                                 //服药日历
        '010': '/model/edit',                                       //护理计划
        '011': '/cms/learnPlan',                                    //学习计划
        '012': '/user/clockIn',                                      //打卡
        '013': '/user/patient/edit', //注册信息
        '014': '/entryGroup', //入组协议页面
        '015': '/cms/ly', //cms页面
        '016': '/doctorLogin', //医生首次登录校验页
        '017': '/doctorIndex', //医生非首次登录页
        '018': '/recipes/recipesMain', //营养食谱
        '019': '/jiance/add', //添加监测数据
        '020': '/user/patient/examineDataList', //检验数据
        '021': '/recipes/recipesMain', //食谱
        '022': '/user/patient/show', //保单
        '023': '/doctorMine', //医生---我的
        '024': '/doctorIndex', //医生---我的患者
        '025': '/healthLecture/list', //医生---讲座
        '000': '/messageStatusUrl',
    },

};
