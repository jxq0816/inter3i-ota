/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2017/01/16
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import com.inter3i.sun.api.ota.v1.util.NLPDataFormater;

import java.util.HashSet;
import java.util.Set;

/**
 * 该类主要配置了所有需要的分词字段，以及每个字段的分词方式
 */
public class SegmentFieldsConfig {

    public static final String[] NED_SEGMENTE_FIELDS_TEXT = {"text", "pg_text"};
    public static final Set<String> TEXT_FIELDNAME = new HashSet<String>(16);


    /**
     * 针对需要分段的字段进行特殊处理，将该文档中的该字段，在分词时候，同样设置成数组
     * <p>
     * 如: --+-pg_text<BR>
     * ***********+--[0]--+-"abc"
     * ***********+--[1]--+-"def"<BR>
     * <p>
     * 将该结构通过分词数据格式化工具{@link NLPDataFormater} 来将这种需要分词的字段格式话处理成以下结构:<BR>
     * <p>
     * --+-pg_text<BR>
     * ***********+--[0]--JsonObj
     * *********************+--content--+-"abc"
     * *********************+--terms  --+-JsonArray
     * **************************************+--[0]--+-"a"
     * **************************************+--[1]--+-"b"
     * **************************************+--[2]--+-"c"
     * ***********+--[1]--JsonObj
     * *********************+--content--+-"def"
     * *********************+--terms  --+-JsonArray
     * **************************************+--[0]--+-"d"
     * **************************************+--[1]--+-"e"
     * **************************************+--[2]--+-"f"
     */
    public static final String[] PARAGRAPH_FILEDS = {"pg_text"};
    public static final Set<String> PARAGRAPH_FILEDS_SETS = new HashSet<String>(2);

    static {
        for (String paragraphFiled : PARAGRAPH_FILEDS) {
            PARAGRAPH_FILEDS_SETS.add(paragraphFiled);
        }
        for (String textFiled : NED_SEGMENTE_FIELDS_TEXT) {
            TEXT_FIELDNAME.add(textFiled);
        }
    }


    public static final String NED_SEGMENTE_FIELDS_TEX_KEY = "key_text";
    public static final String NED_SEGMENTE_FIELDS_PG_TEX_KEY = "key_pg_text";

    public static final String NED_SEGMENTE_FIELDS_TEX_REPOST_START_KEY = "key_text_repost_start";
    public static final String NED_SEGMENTE_FIELDS_TEX_REPOST_END_KEY = "key_text_repost_end";

    public static final String[] NED_SEGMENTE_FIELDS = {"post_title", "description", "verified_reason", "user.description",
            "user.verified_reason", "retweeted_status.text", "retweeted_status.user.verified_reason", "status.text", "status.user.description",
            "status.user.verified_reason", "status.retweeted_status.text", "status.retweeted_status.user.description",
            "status.retweeted_status.user.verified_reason", "reply_comment.text", "reply_comment.user.description",
            "reply_comment.user.verified_reason"};

    public static final String FIELD_PATH_SEP = "\\.";
    public static final String FIELD_PATH_SEP_CHAR = ".";


    public static final String[] TEXT_FIELD_IN_SEG = {"NRN", "emoNRN", "district",
            "emoDistrict", "city", "emoCity", "province", "emoProvince", "country", "emoCountry", "business",
            "emoBusiness", "combinWord", "emoCombin", "organization", "emoOrganization", "emotion", "wb_topic",
            "emoTopic", "wb_topic_keyword", "emoTopicKeyword", "wb_topic_combinWord", "emoTopicCombinWord",
            "url", "host_domain", "account", "emoAccount"
    };

    public static final String[] REMOVED_FIELD = {"orig_emotion", "orig_business", "orig_emoBusiness"};

    /**
     * 将分词结果中的所有分词的token设置到需要分词字段的该 key下面</BR>
     * {
     * content:"xxx",
     * terms:[]
     * }
     */
    public static final String CONTENT_NAME_FOR_SEG_FIELD = "content";
    public static final String TERMS_NAME_FOR_SEG_FIELD = "terms";

    //分词结果中对用的terms的key值
    public static final String ANALYSIS_TERMS_KEY = "text";


    /**
     * content_type 字段的
     */
    public static final String CONTENT_TYPE_NAME_FOR_SEG_FIELD = "content_type";
    //当文章中有这些字段时候 该文章为评论(判断为评论的标准)
    public static final String[] COMMENT_FIELDS = {"retweeted_status", "retweeted_mid", "reply_father_floor", "reply_father_mid"};
    //原创
    public static final int CONTENT_TYPE_ORIGINAL = 0;
    //转发
    public static final int CONTENT_TYPE_TRANSPOND = 1;
    //评论
    public static final int CONTENT_TYPE_COMMENT = 2;
    //提问
    public static final int CONTENT_TYPE_QUESTION = 3;
    //回答
    public static final int CONTENT_TYPE_ANSWER = 4;
    //增量数据
    public static final int CONTENT_TYPE_APPEND = 5;

    /**
     * 楼号字段(作为主键生成的一项)
     */
    public static final String CONTENT_FILED_FLOOR = "floor";

    public static boolean isTextField(final String fieldName) {
        return SegmentFieldsConfig.TEXT_FIELDNAME.contains(fieldName);
    }

}
