package com.caring.sass.msgs.utils.im.api;

public abstract interface ChatRoomAPI
{
  public abstract Object createChatRoom(Object paramObject);

  public abstract Object modifyChatRoom(String paramString, Object paramObject);

  public abstract Object deleteChatRoom(String paramString);

  public abstract Object getAllChatRooms();

  public abstract Object getChatRoomDetail(String paramString);

  public abstract Object addSingleUserToChatRoom(String paramString1, String paramString2);

  public abstract Object addBatchUsersToChatRoom(String paramString, Object paramObject);

  public abstract Object removeSingleUserFromChatRoom(String paramString1, String paramString2);

  public abstract Object removeBatchUsersFromChatRoom(String paramString, String[] paramArrayOfString);
}

/* Location:           D:\gitProject\saas\caring-im\caring-im.jar
 * Qualified Name:     BOOT-INF.classes.lano.im.utils.im.api.ChatRoomAPI
 * JD-Core Version:    0.6.0
 */