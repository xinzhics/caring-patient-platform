package com.caring.sass.ai.face.service.impl;


import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.CozeConfig;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.config.FaceCoreConfig;
import com.caring.sass.ai.dto.CozeToken;
import com.caring.sass.ai.dto.FaceMergeResultDTO;
import com.caring.sass.ai.dto.FaceV3DeteactDTO;
import com.caring.sass.ai.dto.UserSubscriptionTemplate;
import com.caring.sass.ai.entity.face.*;
import com.caring.sass.ai.face.dao.MegviiFusionDiagramMapper;
import com.caring.sass.ai.face.dao.MegviiFusionDiagramResultMapper;
import com.caring.sass.ai.face.dao.MegviiTemplateDiagramMapper;
import com.caring.sass.ai.face.dao.MegviiTemplateDiagramTypeMapper;
import com.caring.sass.ai.face.service.MegviiFusionDiagramService;
import com.caring.sass.ai.face.service.MergeImageFreeFrequencyService;
import com.caring.sass.ai.utils.FaceApi;
import com.caring.sass.ai.utils.ImageThumbnails;
import com.caring.sass.ai.utils.VolcengineApi2;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.entity.File;
import com.caring.sass.oauth.api.MiniappInfoApi;
import com.caring.sass.user.entity.MiniappInfo;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.MiniAppApi;
import com.caring.sass.wx.WechatOrdersApi;
import com.caring.sass.wx.dto.config.WechatOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 融合图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Slf4j
@Service

public class MegviiFusionDiagramServiceImpl extends SuperServiceImpl<MegviiFusionDiagramMapper, MegviiFusionDiagram> implements MegviiFusionDiagramService {

    @Autowired
    FaceApi faceApi;

    @Autowired
    MegviiTemplateDiagramMapper megviiTemplateDiagramMapper;

    @Autowired
    MegviiTemplateDiagramTypeMapper megviiTemplateDiagramTypeMapper;

    @Autowired
    MegviiFusionDiagramResultMapper fusionDiagramResultMapper;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    CozeConfig cozeConfig;

    @Autowired
    FaceCoreConfig faceCoreConfig;

    @Autowired
    FaceConfig faceConfig;

    @Autowired
    MiniappInfoApi miniappInfoApi;

    @Autowired
    MiniAppApi miniAppApi;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    MergeImageFreeFrequencyService mergeImageFreeFrequencyService;

    @Autowired
    VolcengineApi2 volcengineApi;

    private static ExecutorService executor;

    /**
     * 订阅模版
     */
    private static String MINI_APP_USER_SUBSCRIPTION = "mini_app_user_subscription";

    NamedThreadFactory threadFactory = new NamedThreadFactory("saas-coze-face-", false);

    public MegviiFusionDiagramServiceImpl() {
        executor = new ThreadPoolExecutor(1, 3, 0L,
                TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(500),
                threadFactory);
    }

    /**
     * 微信支付订单购买 图片融合机会。
     * 用户首次 限免分类 免费，后续收费。
     * 其他分类 固定收费。
     * @param userId
     * @param typeIds
     * @return
     */
    @Override
    public Integer calculatePrice(Long userId, List<Long> typeIds) {
        int totalCost = 0;
        if (CollUtil.isEmpty(typeIds)) {
            throw new BizException("请选择分类");
        } else {
            // 查询选择的分类。
            // 分类都是免费设置，则计算最低价格和。
            // 分类中存在收费设置， 则取收费的价格作为总价
            List<MegviiTemplateDiagramType> templateDiagramTypes = megviiTemplateDiagramTypeMapper.selectBatchIds(typeIds);
            for (MegviiTemplateDiagramType diagramType : templateDiagramTypes) {
                // 不是限免分类，直接计费
                if (diagramType.getFree().equals(0)) {
                    totalCost += diagramType.getCost();
                } else {
                    // 如果是限免分类。检查用户是否已经使用过限免分类。
                    Integer count = baseMapper.selectCount(Wraps.<MegviiFusionDiagram>lbQ()
                            .eq(MegviiFusionDiagram::getUserId, userId)
                            .like(MegviiFusionDiagram::getTemplateDiagramTypeIds, diagramType.getId().toString()));
                    if (count != null && count > 0) {
                        // 如果用户使用过限免分类，那么此限免分类不在对他免费。
                        totalCost += diagramType.getCost();
                    }
                }
            }
        }
        return totalCost;
    }

    /**
     * 用户创建一个微信支付订单。
     *
     * @param userId
     * @param typeIds
     * @return
     */
    @Override
    public WechatOrders createdWechatOrder(Long userId, List<Long> typeIds) {

        String mergeImageGoodsName = faceConfig.getMerge_image_goods_name();
        // 计算总价
        int totalCost = calculatePrice(userId, typeIds);
        R<MiniappInfo> miniappInfoR = miniappInfoApi.selectByIdNoTenant(userId);
        MiniappInfo infoRData = miniappInfoR.getData();
        if (Objects.isNull(infoRData)) {
            throw new BizException("用户不存在，请删除小程序重新进入。");
        }
        String miniAppOpenId = infoRData.getMiniAppOpenId();
        String miniAppId = infoRData.getMiniAppId();
        WechatOrdersSaveDTO saveDTO = new WechatOrdersSaveDTO();
        saveDTO.setAmount(totalCost);
        saveDTO.setAmountCurrency("CNY");
        saveDTO.setBusinessType(MegviiFusionDiagram.class.getSimpleName());
        saveDTO.setDescription(mergeImageGoodsName);
        saveDTO.setOpenId(miniAppOpenId);
        saveDTO.setAppId(miniAppId);

        R<WechatOrders> wechatOrders = wechatOrdersApi.createWechatOrders(saveDTO);
        return wechatOrders.getData();
    }

    /**
     * 在redis记录小程序订阅的模版
     * @param userSubscriptionTemplate
     */
    @Override
    public void addTemplateSubscription(UserSubscriptionTemplate userSubscriptionTemplate) {

        redisTemplate.opsForHash()
                .put("mini_app_user_subscription", userSubscriptionTemplate.getUserId().toString(), userSubscriptionTemplate.getTemplateId());

    }

    /**
     * 检查用户是否有限免次数。
     * 检查用户选择分类是否不包含收费分类。
     * 当用户有限免次数， 分类都是限免时，此次订单免费。
     * 当用户没有限免次数， 或 分类中包含收费分类时， 校验订单是否支付。
     * @param userId
     * @param templateDiagramTypes
     * @param wechatOrderId
     * @return
     */
    public boolean canMergeImage(Long userId, List<MegviiTemplateDiagramType> templateDiagramTypes, Long wechatOrderId) {
        boolean canFree = true;
        // 检查用户选择的分类是否只有限免
        for (MegviiTemplateDiagramType diagramType : templateDiagramTypes) {
            if (diagramType.getFree() == 0) {
                canFree = false;
                break;
            } else {
                Integer count = baseMapper.selectCount(Wraps.<MegviiFusionDiagram>lbQ().eq(MegviiFusionDiagram::getUserId, userId)
                        .like(MegviiFusionDiagram::getTemplateDiagramTypeIds, diagramType.getId().toString()));
                if (count != null && count > 0) {
                    canFree = false;
                }
            }
        }
        // 检查用户是否需要付费
        // 可以免费
        if (canFree) {
            return true;
        } else if (wechatOrderId == null) {
            // 如果用户没有限免次数，并且 wechatOrderId 是 null  直接拒绝用户的请求
            return false;
        }
        // Check order success within 6 seconds.
        for (int attemptCount = 0; attemptCount < 6; attemptCount++) {
            R<Boolean> orderStatus = wechatOrdersApi.checkOrderSuccess(wechatOrderId);
            if (orderStatus.getData() != null && orderStatus.getData().equals(true)) {
                return true; // Order successful, allow image merging.
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread interrupted while waiting for order confirmation.", e);
                return false; // Interrupted, deny the request.
            }
        }

        // 如果用户没有免费次数，并且 wechatOrderId 不是 null  则判断订单是否支付成功，如果支付成功，则执行人脸融合操作，否则拒绝用户的请求
        return false;
    }

    @Transactional
    @Override
    public MegviiFusionDiagram cozeSave(MultipartFile file, String templateDiagramTypeIds, Long userId, Long wechatOrderId) {

        List<Long> ids = new ArrayList<>();
        // 将模板图ID列表按逗号分隔并转换为Long类型，存入ids列表中
        for (String string : templateDiagramTypeIds.split(",")) {
            ids.add(Long.valueOf(string));
        }
        List<MegviiTemplateDiagramType> templateDiagramTypes = megviiTemplateDiagramTypeMapper.selectBatchIds(ids);
        if (templateDiagramTypes.isEmpty()) {
            throw new BizException("模板图不存在");
        }

        // 将base64转成MultipartFile 上传到文件服务。
        String base64Image = ImageThumbnails.getImageBase64(file);
        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile(base64Image);
        // 获取检测到的人脸数量
        R<File> fileR = fileUploadApi.upload(1L, multipartFile);
        if (!fileR.getIsSuccess() || fileR.getData() == null) {
            throw new BizException(ExceptionCode.SYSTEM_BUSY.getMsg());
        }
        // 若包含收费的分类，则校验是否 有免费次数 或 是否支付订单
        boolean mergeImage = canMergeImage(userId, templateDiagramTypes, wechatOrderId);
        if (!mergeImage) {
            throw new BizException("请购买生成艺术头像服务");
        }
        File data = fileR.getData();
        String url = data.getUrl();
        // 创建融合图对象，并设置属性值
        MegviiFusionDiagram diagram = MegviiFusionDiagram.builder()
                .imageBase64(url)
                .templateDiagramTypeIds(templateDiagramTypeIds)
                .userId(userId)
                .build();
        if (Objects.nonNull(wechatOrderId)) {
            diagram.setOutTradeNo(wechatOrderId.toString());
        }
        diagram.setFinishImageTotal(0);
        diagram.setTaskStatus(0);
        // 将融合图对象插入数据库
        baseMapper.insert(diagram);
        // 异步执行人脸融合操作
        // 使用火山引擎进行人脸融合
        SaasGlobalThreadPool.execute(() -> volcengineMergeFace(diagram, false));
        // 返回融合图对象
        return diagram;
    }

    /**
     * 火山引擎合成图片
     * @param diagram
     */
    public void volcengineMergeFace(MegviiFusionDiagram diagram, boolean reStartTask) {
        try {
            // 获取模板图ID列表
            String templateDiagramTypeIds = diagram.getTemplateDiagramTypeIds();
            List<Long> ids = new ArrayList<>();
            // 将模板图ID列表按逗号分隔并转换为Long类型，存入ids列表中
            for (String string : templateDiagramTypeIds.split(",")) {
                ids.add(Long.valueOf(string));
            }

            // 根据模板图ID列表查询模板图信息
            List<MegviiTemplateDiagram> templateDiagrams = megviiTemplateDiagramMapper.selectList(Wraps.<MegviiTemplateDiagram>lbQ().
                    select(SuperEntity::getId, MegviiTemplateDiagram::getImageObsUrl, MegviiTemplateDiagram::getTemplateDiagramType)
                    .in(MegviiTemplateDiagram::getTemplateDiagramType, ids));
            // 融合结果列表
            List<MegviiFusionDiagramResult> results = new ArrayList<>();
            diagram.setTaskStatus(1);
            baseMapper.updateById(diagram);
            for (MegviiTemplateDiagram templateDiagram : templateDiagrams) {
                // 创建融合结果对象
                MegviiFusionDiagramResult fusionDiagramResult = new MegviiFusionDiagramResult();
                // 设置用户ID
                if (reStartTask) {
                    // 清除掉错误的生产结果。
                    int delete = fusionDiagramResultMapper.delete(Wraps.<MegviiFusionDiagramResult>lbQ()
                            .eq(MegviiFusionDiagramResult::getUserId, diagram.getUserId())
                            .eq(MegviiFusionDiagramResult::getFusionDiagramId, diagram.getId())
                            .eq(MegviiFusionDiagramResult::getFusionDiagramTemplateId, templateDiagram.getId())
                            .isNotNull(MegviiFusionDiagramResult::getErrorMessage));
                    if (delete == 0) {
                        // 验证是否已经生成过
                        Integer integer = fusionDiagramResultMapper.selectCount(Wraps.<MegviiFusionDiagramResult>lbQ()
                                .eq(MegviiFusionDiagramResult::getUserId, diagram.getUserId())
                                .eq(MegviiFusionDiagramResult::getFusionDiagramId, diagram.getId())
                                .eq(MegviiFusionDiagramResult::getFusionDiagramTemplateId, templateDiagram.getId())
                                .isNotNull(MegviiFusionDiagramResult::getErrorMessage));
                        if (integer != null && integer > 0) {
                            continue;
                        }
                    }
                }
                fusionDiagramResult.setUserId(diagram.getUserId());
                fusionDiagramResult.setFusionDiagramId(diagram.getId());
                fusionDiagramResult.setFusionDiagramTypeId(templateDiagram.getTemplateDiagramType());
                fusionDiagramResult.setFusionDiagramTemplateId(templateDiagram.getId());
                try {
                    // 调用人脸融合接口，传入模板图和融合图的base64编码，获取融合结果
                    String string = volcengineApi.mergeImage(diagram.getImageBase64(),
                            templateDiagram.getImageObsUrl(),
                            faceConfig.getSource_similarity(),
                            faceConfig.getGpen(),
                            faceConfig.getReqKey());
                    if (string == null) {
                        // 如果融合结果为空，则设置融合结果的错误信息
                        fusionDiagramResult.setErrorMessage("融合失败");
                    } else {
//                    System.out.println(string);
                        // 设置融合结果的错误信息和结果信息
                        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile("data:image/jpeg;base64," + string);
                        if (Objects.nonNull(multipartFile)) {
                            R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
                            if (uploaded.getIsSuccess()) {
                                File fileInfo = uploaded.getData();
                                fusionDiagramResult.setImageObsUrl(fileInfo.getUrl());
                            }
                        }
                        diagram.setFinishImageTotal(diagram.getFinishImageTotal() + 1);
                    }
                } catch (Exception e) {
                    // 捕获异常，设置融合结果的错误信息
                    fusionDiagramResult.setErrorMessage(e.getMessage());
                }
                // 将融合结果添加到融合结果列表中
                results.add(fusionDiagramResult);
            }
            // 如果融合结果列表不为空
            if (!results.isEmpty()) {
                // 批量插入融合结果到数据库中
                fusionDiagramResultMapper.insertBatchSomeColumn(results);
                diagram.setTaskStatus(2);
                baseMapper.updateById(diagram);
                sendNotice(diagram.getUserId(), diagram.getCreateTime());
            } else {
                Integer integer = fusionDiagramResultMapper.selectCount(Wraps.<MegviiFusionDiagramResult>lbQ()
                        .eq(MegviiFusionDiagramResult::getUserId, diagram.getUserId())
                        .eq(MegviiFusionDiagramResult::getFusionDiagramId, diagram.getId())
                        .isNull(MegviiFusionDiagramResult::getErrorMessage));
                if (integer != null && integer > 0) {
                    diagram.setTaskStatus(2);
                } else {
                    diagram.setTaskStatus(3);
                }
                baseMapper.updateById(diagram);
            }

        } catch (Exception e) {
            // 捕获异常，避免导致线程池中的其他任务无法执行
            log.error("error", e);
        }
    }



    public void cozeError(Long diagramId) {

        MegviiFusionDiagram diagram = baseMapper.selectById(diagramId);
        String templateDiagramTypeIds = diagram.getTemplateDiagramTypeIds();
        List<Long> ids = new ArrayList<>();
        // 将模板图ID列表按逗号分隔并转换为Long类型，存入ids列表中
        for (String string : templateDiagramTypeIds.split(",")) {
            ids.add(Long.valueOf(string));
        }
        List<MegviiTemplateDiagramType> templateDiagramTypes = megviiTemplateDiagramTypeMapper.selectBatchIds(ids);
    }

    /**
     * 服务重启后。恢复线程池中的任务
     */
    @Override
    public void reStartMergeImage() {
        // 服务重启时，执行的，先获取锁。防止多个实例都执行此操作
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("MEGVII_FUSION_DIAGRAM_START_MERGE_IMAGE", "1", 1, TimeUnit.HOURS);
        if (ifAbsent == null || !ifAbsent) {
            return;
        }
        try {
            log.info(" ai server start success , init MergeImage task");
            List<MegviiFusionDiagram> fusionDiagrams = baseMapper.selectList(Wraps.<MegviiFusionDiagram>lbQ()
                    .in(MegviiFusionDiagram::getTaskStatus, 0, 1)
                    .orderByAsc(SuperEntity::getCreateTime));
            for (MegviiFusionDiagram diagram : fusionDiagrams) {
                SaasGlobalThreadPool.execute(() -> volcengineMergeFace(diagram, true));
            }
        } catch (Exception e) {
            // 捕获异常，避免导致线程池中的其他任务无法执行
            log.error("error", e);
        }
    }

    /**
     * 重启某一个任务
     *
     * @param id
     */
    @Override
    public void reStartMergeImage(Long id, boolean resetImageSize) {
        MegviiFusionDiagram diagram = baseMapper.selectById(id);
        if (resetImageSize) {
            // 只处理一下。
            String imageBase64 = diagram.getImageBase64();
            String dir = System.getProperty("java.io.tmpdir");
            String path = dir + "/saas/reStartMergeImage/";
            java.io.File file = null;
            try {
                String qrCodeLocalPath = FileUtils.downLoadFromUrl(imageBase64, UUID.randomUUID().toString().replace("-", ""), path);
                // 将base64转成MultipartFile 上传到文件服务。
                file = new java.io.File(qrCodeLocalPath);
                MockMultipartFile multipartFile1 = FileUtils.fileToFileItem(file);
                String base64Image = ImageThumbnails.getImageBase64(multipartFile1);
                MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile(base64Image);
                R<File> upload = fileUploadApi.upload(2L, multipartFile);
                File data = upload.getData();
                String url = data.getUrl();
                diagram.setImageBase64(url);
                baseMapper.updateById(diagram);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (file != null) {
                    file.delete();
                }
            }
        }
        SaasGlobalThreadPool.execute(() -> volcengineMergeFace(diagram, true));
    }

    /**
     * 使用coze进行人脸融合
     * @param templateDiagramTypes
     * @param diagram
     */
    public void cozeMergeFace( List<MegviiTemplateDiagramType> templateDiagramTypes, MegviiFusionDiagram diagram) {

        List<MegviiTemplateDiagramType> needMerge = new ArrayList<>();
        for (MegviiTemplateDiagramType diagramType : templateDiagramTypes) {
            Integer count = fusionDiagramResultMapper.selectCount(Wraps.<MegviiFusionDiagramResult>lbQ()
                    .eq(MegviiFusionDiagramResult::getFusionDiagramId, diagram.getId())
                    .eq(MegviiFusionDiagramResult::getFusionDiagramTypeId, diagramType.getId())
                    .eq(MegviiFusionDiagramResult::getUserId, diagram.getUserId()));
            if (count != null && count > 0) {

            } else {
                needMerge.add(diagramType);
            }
        }
        if (needMerge.isEmpty()) {
            diagram.setTaskStatus(2);
            baseMapper.updateById(diagram);
        }
        templateDiagramTypes = needMerge;

        LocalDate localDate = LocalDate.now(); // 天
        CozeToken cozeToken = faceCoreConfig.getTokenThenNoNull();
        String token = cozeToken.getToken();
        String botId = cozeToken.getBotId();
        // 获取 redis 中存储的 coze 的访问次数
        String dayKey = "coze_count" + localDate + ":" + token;
        LocalDateTime now = LocalDateTime.now();
        try {
            cozeConfig.dayCheck(dayKey);
        } catch (Exception e) {
            try {
                LocalDateTime dateTime = now.plusDays(1).withHour(0).withMinute(0).withSecond(10).withNano(0);
                // 将LocalDateTime转换为Instant（这里使用系统默认时区）
                Instant instant1 = now.atZone(ZoneId.systemDefault()).toInstant();
                Instant instant2 = dateTime.atZone(ZoneId.systemDefault()).toInstant();
                // 计算两个Instant对象之间的差异
                long secondsBetween = ChronoUnit.MILLIS.between(instant1, instant2);
                Thread.sleep(secondsBetween);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        LocalTime time = LocalTime.now();
        LocalTime minute = time.withNano(0).withSecond(0);
        String minuteKey = "coze_count" + minute.toString() + ":" + token;
        try {
            cozeConfig.minuteCheck(minuteKey);
        } catch (Exception e) {
            try {
                LocalDateTime dateTime = now.plusMinutes(1).withSecond(1).withNano(0);
                // 将LocalDateTime转换为Instant（这里使用系统默认时区）
                Instant instant1 = now.atZone(ZoneId.systemDefault()).toInstant();
                Instant instant2 = dateTime.atZone(ZoneId.systemDefault()).toInstant();
                // 计算两个Instant对象之间的差异
                long secondsBetween = ChronoUnit.MILLIS.between(instant1, instant2);
                Thread.sleep(secondsBetween);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        for (int i = templateDiagramTypes.size(); i > 0; i--) {
            MegviiTemplateDiagramType diagramType = templateDiagramTypes.get(templateDiagramTypes.size() - 1);
            templateDiagramTypes.remove(templateDiagramTypes.size() - 1);
            try {
                time = LocalTime.now();
                LocalTime second = time.withNano(0);
                String secondKey = "coze_count" + second.toString() + ":" + token;
                cozeConfig.secondCheck(secondKey, token);
            } catch (Exception e) {
                cozeError(diagram.getId());
                break;
            }
            String gender = diagramType.getGender();
            String query = "%s ," +
                    "使用我这个图片的链接，帮我生成融合图，条件如下：" +
                    "我选择性别 %s，我想要 %s 风格形象， 请帮我生成吧，并将所有生成照片都给我吧。";
            String genderStr = "";
            if ("Male".equals(gender)) {
                genderStr = "男";
            } else {
                genderStr = "女";
            }
            String queryResult = String.format(query, diagram.getImageBase64(), genderStr, diagramType.getName());
            System.out.println(queryResult);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            // 使用 okHttp3 流式 方式请求接口
            EventSource.Factory factory = EventSources.createFactory(client);
            Long userId = diagram.getUserId();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            json.put("bot_id", botId);
            json.put("user", userId.toString());
            json.put("query", queryResult);
            json.put("stream", true);
            String domain = cozeConfig.getDomain();
            RequestBody body = RequestBody.create(JSON, json.toJSONString());
            Request request = new Request.Builder()
                    .url(domain + CozeConfig.API)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Connection", "Keep-alive")
                    .addHeader("Accept", "*/*")
                    .addHeader("Authorization", token)
                    .post(body)
                    .build();
            Long diagramId = diagram.getId();

            MegviiFusionDiagramServiceImpl diagramService = SpringUtils.getBean(MegviiFusionDiagramServiceImpl.class);

            CozeBotSSEEventSourceListener sourceListener = new CozeBotSSEEventSourceListener(diagramService, userId, diagramId, diagramType.getId());
            factory.newEventSource(request, sourceListener);

        }

        diagram.setTaskStatus(1);
        baseMapper.updateById(diagram);

    }


    /**
     * 发送通知
     * @param userId
     * @param createTime
     */
    public void sendNotice(Long userId, LocalDateTime createTime) {
        Object object = redisTemplate.opsForHash().get(MINI_APP_USER_SUBSCRIPTION, userId.toString());
        if (Objects.nonNull(object)) {
            redisTemplate.opsForHash().delete(MINI_APP_USER_SUBSCRIPTION, userId.toString());
            String templateId = object.toString();
            R<MiniappInfo> miniappInfoR = miniappInfoApi.selectByIdNoTenant(userId);
            MiniappInfo infoRData = miniappInfoR.getData();
            if (infoRData != null) {
                WxMaSubscribeMessage wxMaSubscribeMessage = new WxMaSubscribeMessage();
                wxMaSubscribeMessage.setMiniprogramState(WxMaConstants.MiniProgramState.FORMAL);
                wxMaSubscribeMessage.setPage("/pages/AlbumList/AlbumList");
                wxMaSubscribeMessage.setTemplateId(templateId);
                wxMaSubscribeMessage.setToUser(infoRData.getMiniAppOpenId());
                List<WxMaSubscribeMessage.MsgData> datas = new ArrayList<>();

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                DateTimeFormatter finishFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
                datas.add(new WxMaSubscribeMessage.MsgData("thing20", "生成艺术头像"));
                datas.add(new WxMaSubscribeMessage.MsgData("phrase21", "已完成"));
                datas.add(new WxMaSubscribeMessage.MsgData("time9", createTime.format(formatter)));
                datas.add(new WxMaSubscribeMessage.MsgData("time3", now.format(finishFormatter)));
                wxMaSubscribeMessage.setData(datas);
                miniAppApi.sendMessage(infoRData.getMiniAppId(), wxMaSubscribeMessage);
            }
        }
    }

    /**
     * 更新结果
     * @param diagramId
     */
    public void cozeResultClosed(Long diagramId) {
        MegviiFusionDiagram fusionDiagram = new MegviiFusionDiagram();
        fusionDiagram.setId(diagramId);
        fusionDiagram.setTaskStatus(2);
        Integer count = fusionDiagramResultMapper.selectCount(Wraps.<MegviiFusionDiagramResult>lbQ()
                .eq(MegviiFusionDiagramResult::getFusionDiagramId, diagramId));
        fusionDiagram.setFinishImageTotal(count);
        fusionDiagram.setCreateImageTotal(count);
        baseMapper.updateById(fusionDiagram);

        MegviiFusionDiagram diagram = baseMapper.selectById(diagramId);
        sendNotice(diagram.getUserId(), diagram.getCreateTime());
    }


    /**
     * 保存结果
     * @param fileUrl
     * @param userId
     * @param diagramId
     */
    public void saveCozeResult(String fileUrl, Long userId, Long diagramId, Long diagramTypeId) {

        // 创建融合结果对象
        MegviiFusionDiagramResult fusionDiagramResult = new MegviiFusionDiagramResult();
        // 设置用户ID
        fusionDiagramResult.setUserId(userId);
        fusionDiagramResult.setFusionDiagramTypeId(diagramTypeId);
        fusionDiagramResult.setFusionDiagramId(diagramId);
        java.io.File file = null;
        try {
            file = FileUtils.downLoadArticleFromUrl(fileUrl, UUID.randomUUID() + ".png");
        } catch (IOException e) {
            e.printStackTrace();
            fusionDiagramResult.setErrorMessage("下载图片失败");
        }
        if (file != null) {
            MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
            R<File> fileR = fileUploadApi.upload(2L, multipartFile);
            file.delete();
            if (fileR.getIsSuccess()) {
                File data = fileR.getData();
                String url = data.getUrl();
                fusionDiagramResult.setImageObsUrl(url);
            }
        }

        fusionDiagramResultMapper.insert(fusionDiagramResult);

    }


    /**
     * 旷视人脸融合
     * @param file
     * @param templateDiagramTypeIds
     * @param userId
     * @return
     */
    @Override
    public MegviiFusionDiagram save(MultipartFile file, String templateDiagramTypeIds, Long userId) {

        // 压缩图片，并转换为base64编码
        String base64Image = ImageThumbnails.getImageBase64(file);

        // 调用人脸检测接口，获取检测结果
        FaceV3DeteactDTO detect = faceApi.detect(base64Image);
        if (detect == null) {
            throw new BizException("人脸检测失败");
        }
        // 获取检测到的人脸数量
        int faceNum = detect.getFace_num();

        // 如果人脸数量为0或者faces列表为空，则抛出异常
        if (faceNum == 0 || detect.getFaces() == null || detect.getFaces().isEmpty()) {
            throw new BizException("未检测到人脸");
        }

        // 获取错误信息
        String errorMessage = detect.getError_message();

        // 如果错误信息不为空，则抛出异常
        if (StrUtil.isNotEmpty(errorMessage)) {
            throw new BizException(errorMessage);
        }

        // 创建融合图对象，并设置属性值
        MegviiFusionDiagram diagram = MegviiFusionDiagram.builder()
                .imageBase64(base64Image)
                .templateDiagramTypeIds(templateDiagramTypeIds)
                .userId(userId)
                .build();
        List<Long> ids = new ArrayList<>();
        // 将模板图ID列表按逗号分隔并转换为Long类型，存入ids列表中
        for (String string : templateDiagramTypeIds.split(",")) {
            ids.add(Long.valueOf(string));
        }
        Integer selectCount = megviiTemplateDiagramMapper.selectCount(Wraps.<MegviiTemplateDiagram>lbQ().in(MegviiTemplateDiagram::getTemplateDiagramType, ids));
        diagram.setCreateImageTotal(selectCount);
        diagram.setFinishImageTotal(0);
        diagram.setTaskStatus(0);
        // 将融合图对象插入数据库
        baseMapper.insert(diagram);;

        // 异步执行人脸融合操作
        SaasGlobalThreadPool.execute(() -> mergeFace(diagram));

        // 返回融合图对象
        return diagram;
    }


    /**
     * 旷视人脸融合
     * @param diagram
     */
    public void mergeFace(MegviiFusionDiagram diagram) {
        // 获取模板图ID列表
        String templateDiagramTypeIds = diagram.getTemplateDiagramTypeIds();
        List<Long> ids = new ArrayList<>();
        // 将模板图ID列表按逗号分隔并转换为Long类型，存入ids列表中
        for (String string : templateDiagramTypeIds.split(",")) {
            ids.add(Long.valueOf(string));
        }

        // 获取融合图的base64编码
        String imageBase64 = diagram.getImageBase64();
        // 根据模板图ID列表查询模板图信息
        List<MegviiTemplateDiagram> templateDiagrams = megviiTemplateDiagramMapper.selectList(Wraps.<MegviiTemplateDiagram>lbQ().
                select(SuperEntity::getId, MegviiTemplateDiagram::getImageBase64, MegviiTemplateDiagram::getFaceRectangle)
                .in(MegviiTemplateDiagram::getTemplateDiagramType, ids));
        // 融合结果列表
        List<MegviiFusionDiagramResult> results = new ArrayList<>();
        diagram.setTaskStatus(1);
        baseMapper.updateById(diagram);
        // 遍历模板图信息列表
        templateDiagrams.forEach(templateDiagram -> {
            // 创建融合结果对象
            MegviiFusionDiagramResult fusionDiagramResult = new MegviiFusionDiagramResult();
            // 设置用户ID
            fusionDiagramResult.setUserId(diagram.getUserId());
            fusionDiagramResult.setFusionDiagramId(diagram.getId());
            try {
                // 调用人脸融合接口，传入模板图和融合图的base64编码，获取融合结果
                FaceMergeResultDTO mergedFace = faceApi.mergeFace(templateDiagram, imageBase64);
                if (mergedFace == null) {
                    // 如果融合结果为空，则设置融合结果的错误信息
                    fusionDiagramResult.setErrorMessage("融合失败");
                } else {
                    // 设置融合结果的错误信息和结果信息
                    if (mergedFace.getError_message() != null) {
                        fusionDiagramResult.setErrorMessage(mergedFace.getError_message());
                    } else {
                        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile("data:image/jpeg;base64," + mergedFace.getResult());
                        if (Objects.nonNull(multipartFile)) {
                            R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
                            if (uploaded.getIsSuccess()) {
                                File fileInfo = uploaded.getData();
                                fusionDiagramResult.setImageObsUrl(fileInfo.getUrl());
                            }
                        }
                        diagram.setFinishImageTotal(diagram.getFinishImageTotal() + 1);
                        baseMapper.updateById(diagram);
                    }
                }
            } catch (Exception e) {
                // 捕获异常，设置融合结果的错误信息
                fusionDiagramResult.setErrorMessage(e.getMessage());
            }
            // 将融合结果添加到融合结果列表中
            results.add(fusionDiagramResult);
        });
        diagram.setTaskStatus(2);
        baseMapper.updateById(diagram);
        // 如果融合结果列表不为空
        if (!results.isEmpty()) {
            // 批量插入融合结果到数据库中
            fusionDiagramResultMapper.insertBatchSomeColumn(results);
        }

    }


    @Override
    public void addFaceMergeCozeToken(String token, String botId) {
        faceCoreConfig.addToken(token, botId);
    }


    @Override
    public void removeFaceMergeCozeToken(String token) {
        faceCoreConfig.removeToken(token);
    }

    @Override
    public boolean removeById(Serializable id) {

        MegviiFusionDiagram diagram = new MegviiFusionDiagram();
        diagram.setDeleteStatus(CommonStatus.YES);
        diagram.setId(Long.parseLong(id.toString()));
        baseMapper.updateById(diagram);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            removeById(id);
        }
        return true;
    }
}
