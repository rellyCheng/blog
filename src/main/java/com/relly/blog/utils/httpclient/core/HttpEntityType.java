package com.relly.blog.utils.httpclient.core;

/**
 *
 * HttpEntity 类型
 * @author Thunder
 * @date 2018/7/25 16:14
 * @param  * @param null
 * @return
 */
public enum HttpEntityType {

    ENTITY_STRING(0,"StringEntity"),

    //ENTITY_FILE(1,"FileEntity"),

    ENTITY_BYTES(2,"ByteArrayEntity"),

   // ENTITY_INPUT_STREAM(3,"ENTITY_INPUT_STREAM"),

    //ENTITY_SERIALIZABLE(4,"SerializableEntity"),

   // ENTITY_MULTIPART(5,"MultipartEntity"),

    ENTITY_FORM(6,"UrlEncodedFormEntity");

    private int code;
    private String name;

    private HttpEntityType(int code, String name){
        this.code = code;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getCode() {
        return code;
    }
}
