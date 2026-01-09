package com.caring.sass.msgs.utils.im.api;

import io.swagger.client.model.RegisterUsers;

public abstract interface IMUserAPI
{
  public abstract Object createNewIMUserSingle(RegisterUsers paramRegisterUsers);

  public abstract Object createNewIMUserBatch(Object paramObject);

  public abstract Object getIMUserByUserName(String paramString);

  public abstract Object getIMUsersBatch(Long paramLong, String paramString);

  public abstract Object deleteIMUserByUserName(String paramString);

  public abstract Object deleteIMUserBatch(Long paramLong, String paramString);

  public abstract Object modifyIMUserPasswordWithAdminToken(String paramString, Object paramObject);

  public abstract Object modifyIMUserNickNameWithAdminToken(String paramString, Object paramObject);

  public abstract Object addFriendSingle(String paramString1, String paramString2);

  public abstract Object deleteFriendSingle(String paramString1, String paramString2);

  public abstract Object getFriends(String paramString);

  public abstract Object getBlackList(String paramString);

  public abstract Object addToBlackList(String paramString, Object paramObject);

  public abstract Object removeFromBlackList(String paramString1, String paramString2);

  public abstract Object getIMUserStatus(String paramString);

  public abstract Object getOfflineMsgCount(String paramString);

  public abstract Object getSpecifiedOfflineMsgStatus(String paramString1, String paramString2);

  public abstract Object deactivateIMUser(String paramString);

  public abstract Object activateIMUser(String paramString);

  public abstract Object disconnectIMUser(String paramString);

  public abstract Object getIMUserAllChatGroups(String paramString);

  public abstract Object getIMUserAllChatRooms(String paramString);
}

/* Location:           D:\gitProject\saas\caring-im\caring-im.jar
 * Qualified Name:     BOOT-INF.classes.lano.im.utils.im.api.IMUserAPI
 * JD-Core Version:    0.6.0
 */