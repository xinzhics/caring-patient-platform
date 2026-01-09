package com.caring.sass.msgs.utils.im.api;

public abstract interface ChatGroupAPI
{
  public abstract Object getChatGroups(Long paramLong, String paramString);

  public abstract Object getChatGroupDetails(String[] paramArrayOfString);

  public abstract Object createChatGroup(Object paramObject);

  public abstract Object modifyChatGroup(String paramString, Object paramObject);

  public abstract Object deleteChatGroup(String paramString);

  public abstract Object getChatGroupUsers(String paramString);

  public abstract Object addSingleUserToChatGroup(String paramString1, String paramString2);

  public abstract Object addBatchUsersToChatGroup(String paramString, Object paramObject);

  public abstract Object removeSingleUserFromChatGroup(String paramString1, String paramString2);

  public abstract Object removeBatchUsersFromChatGroup(String paramString, String[] paramArrayOfString);

  public abstract Object transferChatGroupOwner(String paramString, Object paramObject);

  public abstract Object getChatGroupBlockUsers(String paramString);

  public abstract Object addSingleBlockUserToChatGroup(String paramString1, String paramString2);

  public abstract Object addBatchBlockUsersToChatGroup(String paramString, Object paramObject);

  public abstract Object removeSingleBlockUserFromChatGroup(String paramString1, String paramString2);

  public abstract Object removeBatchBlockUsersFromChatGroup(String paramString, String[] paramArrayOfString);
}

/* Location:           D:\gitProject\saas\caring-im\caring-im.jar
 * Qualified Name:     BOOT-INF.classes.lano.im.utils.im.api.ChatGroupAPI
 * JD-Core Version:    0.6.0
 */