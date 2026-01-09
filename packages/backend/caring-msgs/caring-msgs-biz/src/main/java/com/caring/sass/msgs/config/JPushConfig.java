 package com.caring.sass.msgs.config;

 public class JPushConfig
 {
   private String masterSecret;
   private String appkey;
   boolean apns = false;

   public JPushConfig(String jPushAppKey, String jPushMasterSecret) {
     this.appkey = jPushAppKey;
     this.masterSecret = jPushMasterSecret;
   }

   public String getMasterSecret() {
/* 22 */     return this.masterSecret;
   }

   public void setMasterSecret(String masterSecret) {
/* 26 */     this.masterSecret = masterSecret;
   }

   public String getAppkey() {
/* 30 */     return this.appkey;
   }

   public void setAppkey(String appkey) {
/* 34 */     this.appkey = appkey;
   }

   public boolean isApns() {
     return this.apns;
   }

   public void setApns(boolean apns) {
     this.apns = apns;
   }
 }
