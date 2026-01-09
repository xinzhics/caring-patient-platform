package com.caring.sass.nursing.constant;

/**
 * 标签绑定使用的redis消息队列key
 *
 */
public class TagBindRedisKey {

   public static String TENANT_ATTR_BIND =  "news:tenant-attr-bind";



   public static String getTagBindKey(String tenantCode, Long tagId) {
       return "tenant:" + tenantCode + "tag:" + tagId;
   }

}
