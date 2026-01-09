package com.caring.sass.common.utils.paramtericText;

import com.alibaba.fastjson.JSONObject;

public class ParameterHolder extends JSONObject {
    private static final long serialVersionUID = -6849764822133331280L;
    long threadId;

    public ParameterHolder(long threadId) {
        this.threadId = threadId;
    }
}
