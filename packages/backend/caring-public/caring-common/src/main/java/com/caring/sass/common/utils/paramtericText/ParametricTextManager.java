package com.caring.sass.common.utils.paramtericText;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParametricTextManager {

    private static final ThreadLocal<ParameterHolder> THREAD_LOCAL = new ThreadLocal();

    public void init() {
        long currentThreadId = Thread.currentThread().getId();
        if (THREAD_LOCAL.get() == null) {
            THREAD_LOCAL.set(new ParameterHolder(currentThreadId));
        }
    }

    public void addParameter(String key, Object value) {
        ParameterHolder ph = THREAD_LOCAL.get();
        if (ph == null) {
            this.init();
            ph = THREAD_LOCAL.get();
        }

        if (ph != null) {
            ph.put(key, value);
        }

    }

    private static final Pattern P = Pattern.compile("\\$\\{([^}]*)\\}");

    public String format(String text) {
        if (!Objects.isNull(text)) {
            Matcher m = P.matcher(text);
            ArrayList<String> variableNames = new ArrayList();

            while (m.find()) {
                variableNames.add(m.group(1));
            }

            Map<String, Object> params = new HashMap();
            Iterator var7 = variableNames.iterator();

            String toReplacedText;
            while (var7.hasNext()) {
                String name = (String) var7.next();
                if (name.startsWith("groovy::")) {
                    toReplacedText = name.substring(8);
                    toReplacedText = toReplacedText.replaceAll("\\\\\"", "\"");
                    String val = this.execGroovy(toReplacedText);
                    params.put(name, val);
                } else if (name.contains(".")) {
                    String[] array = name.split("\\.");
                    this.parseObject(params, name, array[0], array[1]);
                } else {
                    this.parseString(params, name);
                }
            }

            Object val;
            for (Matcher m2 = P.matcher(text); m2.find(); text = text.replace(m2.group(), val == null ? "" : val.toString())) {
                toReplacedText = m2.group(1);
                val = params.get(toReplacedText);
            }
//            remove();
        }
        return text;
    }

    String execGroovy(String groovyCode) {
        try {
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding);
            Object value = shell.evaluate(groovyCode);
            return value == null ? "[express error]" : value.toString();
        } catch (Exception var5) {
            return "[express error]";
        }
    }

    void parseString(Map<String, Object> params, String key) {
        if (!StringUtils.isEmpty(key)) {
            ParameterHolder ph = THREAD_LOCAL.get();
            if (null != ph) {
                Object obj = ph.get(key);
                if (obj != null) {
                    params.put(key, obj.toString());
                }
            }
        }
    }

    void parseObject(Map<String, Object> params, String key, String objName, String propertyName) {
        if (!StringUtils.isEmpty(objName) && !StringUtils.isEmpty(propertyName)) {
            ParameterHolder ph = THREAD_LOCAL.get();
            if (null == ph) {
                params.put(key, "[express error]");
            } else {
                Object obj = ph.get(objName);
                if (obj == null) {
                    params.put(key, "[express error]");
                } else {
                    try {
                        Object value = PropertyUtils.getProperty(obj, propertyName);
                        if (value == null) {
                            value = "[express error]";
                        }

                        params.put(key, value.toString());
                    } catch (Exception var8) {
                    }

                }
            }
        }
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
