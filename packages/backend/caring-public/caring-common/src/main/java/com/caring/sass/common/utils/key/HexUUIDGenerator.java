package com.caring.sass.common.utils.key;

import java.io.Serializable;
import java.util.Properties;

public class HexUUIDGenerator extends AbstractUUIDGenerator {
    private String sep = "";

    public HexUUIDGenerator() {
    }

    protected String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    protected String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    public Serializable generate() {
        return (new StringBuffer(36)).append(this.format(this.getIP())).append(this.sep).append(this.format(this.getJVM())).append(this.sep).append(this.format(this.getHiTime())).append(this.sep).append(this.format(this.getLoTime())).append(this.sep).append(this.format(this.getCount())).toString();
    }

    public void configure(Properties params) {
        this.sep = params.getProperty("separator");
        this.sep = this.sep == null ? "" : this.sep;
    }


    public Object generate(int i) {
        return (new StringBuffer(i)).append(this.format(this.getIP())).append(this.sep).append(this.sep).append(this.format(this.getHiTime())).append(this.sep).append(this.format(this.getLoTime())).append(this.sep).toString();
    }
}
