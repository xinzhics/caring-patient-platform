package com.caring.sass.file.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.biz.FileBiz;
import com.caring.sass.file.dao.AttachmentMapper;
import com.caring.sass.file.domain.FileDO;
import com.caring.sass.file.domain.FileDeleteDO;
import com.caring.sass.file.dto.AttachmentDTO;
import com.caring.sass.file.dto.AttachmentResultDTO;
import com.caring.sass.file.dto.FilePageReqDTO;
import com.caring.sass.file.entity.Attachment;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.enumeration.DataType;
import com.caring.sass.file.properties.FileServerProperties;
import com.caring.sass.file.service.AttachmentService;
import com.caring.sass.file.strategy.FileStrategy;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 附件
 * </p>
 *
 * @author caring
 * @date 2019-06-24
 */
@Slf4j
@Service

public class AttachmentServiceImpl extends SuperServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {
    @Autowired
    private DatabaseProperties databaseProperties;
    @Resource
    private FileStrategy fileStrategy;
    @Autowired
    private FileServerProperties fileProperties;
    @Autowired
    private FileBiz fileBiz;

    @Override
    public IPage<Attachment> page(IPage<Attachment> page, FilePageReqDTO data) {
        Attachment attachment = BeanPlusUtil.toBean(data, Attachment.class);

        // ${ew.customSqlSegment} 语法一定要手动eq like 等 不能用lbQ!
        LbqWrapper<Attachment> wrapper = Wraps.<Attachment>lbQ()
                .like(Attachment::getSubmittedFileName, attachment.getSubmittedFileName())
                .like(Attachment::getBizType, attachment.getBizType())
                .like(Attachment::getBizId, attachment.getBizId())
                .eq(Attachment::getDataType, attachment.getDataType())
                .orderByDesc(Attachment::getId);
        return baseMapper.page(page, wrapper, new DataScope());
    }

    @Override
    public AttachmentDTO upload(MultipartFile multipartFile, String tenant, Long id, String bizType, String bizId, Boolean isSingle) {
        //根据业务类型来判断是否生成业务id
        if (StringUtils.isNotEmpty(bizType) && StringUtils.isEmpty(bizId)) {
            DatabaseProperties.Id idPro = databaseProperties.getId();
            bizId = IdUtil.getSnowflake(idPro.getWorkerId(), idPro.getDataCenterId()).nextIdStr();
        }
        File file = fileStrategy.upload(multipartFile);

        Attachment attachment = BeanPlusUtil.toBean(file, Attachment.class);

        attachment.setBizId(bizId);
        attachment.setBizType(bizType);
        setDate(attachment);

        if (isSingle) {
            super.remove(Wraps.<Attachment>lbQ().eq(Attachment::getBizId, bizId).eq(Attachment::getBizType, bizType));
        }

        if (id != null && id > 0) {
            //当前端传递了文件id时，修改这条记录
            attachment.setId(id);
            super.updateById(attachment);
        } else {
            super.save(attachment);
        }

        AttachmentDTO dto = BeanPlusUtil.toBean(attachment, AttachmentDTO.class);
        dto.setDownloadUrlByBizId(fileProperties.getDownByBizId(bizId));
        dto.setDownloadUrlById(fileProperties.getDownById(attachment.getId()));
        dto.setDownloadUrlByUrl(fileProperties.getDownByUrl(attachment.getUrl(), attachment.getSubmittedFileName()));
        return dto;
    }

    private void setDate(Attachment file) {
        LocalDateTime now = LocalDateTime.now();
        file.setCreateMonth(DateUtils.formatAsYearMonthEn(now));
        file.setCreateWeek(DateUtils.formatAsYearWeekEn(now));
        file.setCreateDay(DateUtils.formatAsDateEn(now));
    }

    @Override
    public boolean remove(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }

        List<Attachment> list = super.list(Wrappers.<Attachment>lambdaQuery().in(Attachment::getId, ids));
        if (list.isEmpty()) {
            return false;
        }
        boolean bool = super.removeByIds(ids);

        boolean boolDel = fileStrategy.delete(list.stream().map((fi) -> FileDeleteDO.builder()
                .relativePath(fi.getRelativePath())
                .fileName(fi.getFilename())
                .group(fi.getGroup())
                .path(fi.getPath())
                .file(false)
                .build())
                .collect(Collectors.toList()));
        return bool && boolDel;
    }

    @Override
    public boolean removeByBizIdAndBizType(String bizId, String bizType) {
        List<Attachment> list = super.list(
                Wraps.<Attachment>lbQ()
                        .eq(Attachment::getBizId, bizId)
                        .eq(Attachment::getBizType, bizType));
        if (list.isEmpty()) {
            return false;
        }
        return remove(list.stream().mapToLong(Attachment::getId).boxed().collect(Collectors.toList()));
    }

    @Override
    public List<AttachmentResultDTO> find(String[] bizTypes, String[] bizIds) {
        return baseMapper.find(bizTypes, bizIds);
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, Long[] ids) throws Exception {
        List<Attachment> list = (List<Attachment>) super.listByIds(Arrays.asList(ids));
        down(request, response, list);
    }

    @Override
    public void downloadByBiz(HttpServletRequest request, HttpServletResponse response, String[] bizTypes, String[] bizIds) throws Exception {
        List<Attachment> list = super.list(
                Wraps.<Attachment>lbQ()
                        .in(Attachment::getBizType, bizTypes)
                        .in(Attachment::getBizId, bizIds));

        down(request, response, list);
    }

    @Override
    public void downloadByUrl(HttpServletRequest request, HttpServletResponse response, String url, String filename) throws Exception {
        if (StringUtils.isEmpty(filename)) {
            filename = "未知文件名.txt";
        }
        List<Attachment> list = Arrays.asList(Attachment.builder()
                .url(url).submittedFileName(filename).size(0L).dataType(DataType.DOC).build());
        down(request, response, list);
    }

    private void down(HttpServletRequest request, HttpServletResponse response, List<Attachment> list) throws Exception {
        if (list.isEmpty()) {
            throw BizException.wrap("您下载的文件不存在");
        }
        List<FileDO> listDO = list.stream().map((file) ->
                FileDO.builder()
                        .url(file.getUrl())
                        .submittedFileName(file.getSubmittedFileName())
                        .size(file.getSize())
                        .dataType(file.getDataType())
                        .build())
                .collect(Collectors.toList());
        fileBiz.down(listDO, request, response);
    }


}
