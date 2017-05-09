/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/01/12
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.util;

import com.inter3i.sun.api.ota.v1.config.ImportDataConfig;
import com.inter3i.sun.api.ota.v1.config.SegmentFieldsConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NLPDataFormater {
    private static final Logger logger = LoggerFactory.getLogger(NLPDataFormater.class);

    /** 添加
     * @param josn
     * @param result
     * @throws JSONException
     * @throws CloneNotSupportedException
     */
    public static void addSegmentInfo4Text(Map<String, Object> filedTermsMap, String textFieldName, int valueIdx, JSONObject josn, JSONArray result, JSONArray dictPlan, String currentHost, int currentPort) throws JSONException, CloneNotSupportedException {
        JSONObject text = null;
        if (valueIdx >= 0) {
            text = (JSONObject) josn.getJSONArray(textFieldName).get(valueIdx);
        } else {
            //单值字段取值
            text = (JSONObject) josn.get(textFieldName);
        }

        if (ValidateUtils.isNullOrEmpt(text, "content")) {
            logger.warn("addSegmentInfo4Text faield:[content for text is null or empety]");
            return;
        }

        JSONObject nlpReq = new JSONObject();
        nlpReq.put("text_type", 1);

        nlpReq.put("cur_port", currentPort);
        nlpReq.put("cur_host", currentHost);

        //如果未设置sourceid,说明sourceid为非已知来源, 不送sourceid时solrNLP当作一般文章处理
        if (!ValidateUtils.isNullOrEmpt(josn, "sourceid")) {
            nlpReq.put("'sourceid'", josn.getString("sourceid"));
        }

        if (!josn.isNull("content_type")) {
            nlpReq.put("content_type", josn.getInt("content_type"));
        }

        nlpReq.put("dicttype", ImportDataConfig.TOKENIZE_DICTTYPE_ALL);
        nlpReq.put("dictionary_plan", dictPlan);

        String content = null;
        if (!josn.isNull(textFieldName)) {
            content = SegmentHelper.getTokenFieldVal(text);
        } else {
            throw new RuntimeException("addSegmentInfo4Text for current doc excption,field:[" + textFieldName + "] is null.");
        }
        nlpReq.put("content", content);

        // 非原创的文章 需要原创文章中的情感信息
        if ((!josn.isNull("content_type") && josn.getInt("content_type") != 0) && !ValidateUtils.isNullOrEmpt(josn, "analysis_status") && josn.getInt("analysis_status") == ImportDataConfig.ANALYSIS_STATUS_NORMAL) {
            nlpReq.put("dependorig", 1);

            //TODO 需要在这里设置原创的情感信息
            JSONObject originalInfo = new JSONObject();
            NLPDataFormater.setOrigEmotionData(josn, originalInfo);
            nlpReq.put("originfo", originalInfo);
        } else {
            // 原创
            nlpReq.put("dependorig", 0);
            nlpReq.put("originfo", "");
        }

        boolean repostStart = false;

        int fieldIdx = result.length();

        //单独处理转发
        if (josn.getInt("content_type") == SegmentFieldsConfig.CONTENT_TYPE_TRANSPOND && !nlpReq.isNull("content")) {
            repostStart = nlpReq.getString("content").contains("//@");
        }
        if (repostStart) {
            String originalContent = nlpReq.getString("content");
            int startPos = originalContent.indexOf("//@");
            nlpReq.put("content", originalContent.substring(0, startPos));
            if (filedTermsMap.containsKey(SegmentFieldsConfig.NED_SEGMENTE_FIELDS_TEX_REPOST_START_KEY)) {
                //text 字段 和 pg_text字段每个文章中能包其中一个
                throw new RuntimeException("addSegmentInfo4Text excetion,current repost key is exist!");
            }
            filedTermsMap.put(SegmentFieldsConfig.NED_SEGMENTE_FIELDS_TEX_REPOST_START_KEY, fieldIdx);
            result.put(nlpReq);

            JSONObject nlpReqClone = new JSONObject();
            JsonUtils.deepClone(nlpReq, nlpReqClone);
            String contentSufix = originalContent.substring(startPos);
            filedTermsMap.put(SegmentFieldsConfig.NED_SEGMENTE_FIELDS_TEX_REPOST_END_KEY, result.length());
            nlpReqClone.put("content", contentSufix);
            result.put(nlpReqClone);

            //获取到该字段的分词结果下标
            fieldIdx = -(fieldIdx + 1);
        } else {
            result.put(nlpReq);
        }

        if (valueIdx < 0) {
            //单值字段
            filedTermsMap.put(textFieldName, fieldIdx);
        } else {
            //
            if (!filedTermsMap.containsKey(textFieldName)) {
                filedTermsMap.put(textFieldName, new ArrayList(16));
            }
            ((List) filedTermsMap.get(textFieldName)).add(valueIdx, fieldIdx);
        }
    }

    public static void addSegmentInfo4Common(Map<String, Object> filedTermsMap, JSONObject josn, JSONArray result, String fullFieldName, String filedName, JSONArray dictPlan, String currentHost, int currentPort) throws JSONException {
        JSONObject filedJsonData = (JSONObject) josn.get(filedName);
        if (!ValidateUtils.isNullOrEmpt(filedJsonData, "content")) {
            JSONObject nlpReq = new JSONObject();
            nlpReq.put("text_type", 0); //非正文
            //如果未设置sourceid,说明sourceid为非已知来源, 不送sourceid时solrNLP当作一般文章处理
            if (!ValidateUtils.isNullOrEmpt(josn, "sourceid")) {
                nlpReq.put("'sourceid'", josn.getString("sourceid"));
            }

            nlpReq.put("dicttype", ImportDataConfig.TOKENIZE_DICTTYPE_NOEB);
            nlpReq.put("dictionary_plan", dictPlan);
            nlpReq.put("dependorig", 0);
            nlpReq.put("originfo", "");

            String contentStr = SegmentHelper.getTokenFieldValByPathExp(josn, filedName);
            nlpReq.put("content", contentStr);
            nlpReq.put("cur_port", currentPort);
            nlpReq.put("cur_host", currentHost);

            filedTermsMap.put(fullFieldName, result.length());
            result.put(nlpReq);
        } else {
            logger.warn("addSegmentInfo4Text faield:[content for " + fullFieldName + " is null or empety]");
        }
    }

    private static void addSegmentInfo4CommonLoop(Map<String, Object> filedTermsMap, JSONObject josn, JSONArray result, String filedName, String[] fieldNames, int fieldNameIdx, JSONArray dictPlan, String currentHost, int currentPort) throws JSONException {
        if (fieldNameIdx >= (fieldNames.length - 1)) {
            //最后一层
            addSegmentInfo4Common(filedTermsMap, josn, result, filedName, fieldNames[fieldNameIdx], dictPlan, currentHost, currentPort);
        } else {
            String curPath = fieldNames[fieldNameIdx];
            JSONObject childDoc = josn.getJSONObject(curPath);
            addSegmentInfo4CommonLoop(filedTermsMap, childDoc, result, filedName, fieldNames, fieldNameIdx + 1, dictPlan, currentHost, currentPort);
        }
    }

    public static void addSegmentInfo4CommonWrap(Map<String, Object> filedTermsMap, JSONObject josn, JSONArray result, String filedName, JSONArray dictPlan, String currentHost, int currentPort) throws JSONException {
        String[] fieldPaths = null;
        if (filedName.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            fieldPaths = filedName.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            String curPath = fieldPaths[0];
            JSONObject childDoc = josn.getJSONObject(curPath);
            addSegmentInfo4CommonLoop(filedTermsMap, childDoc, result, filedName, fieldPaths, 1, dictPlan, currentHost, currentPort);
        } else {
            addSegmentInfo4Common(filedTermsMap, josn, result, filedName, filedName, dictPlan, currentHost, currentPort);
        }
    }

    /**
     * 对评论或者回复等进行分词的时候，需要在评论或者恢复里面设置原创的情感信息
     *
     * @param curDoc
     * @param origInfo4Cur
     */
    public static void setOrigEmotionData(JSONObject curDoc, JSONObject origInfo4Cur) throws JSONException {
        if (ValidateUtils.isNullOrEmpt(curDoc, "orig_emotion")) {
            origInfo4Cur.put("emotion", new JSONArray());
        } else {
            origInfo4Cur.put("emotion", curDoc.getJSONArray("orig_emotion"));
        }

        //
        if (ValidateUtils.isNullOrEmpt(curDoc, "orig_business")) {
            origInfo4Cur.put("orig_business", new JSONArray());
        } else {
            origInfo4Cur.put("orig_business", curDoc.getJSONArray("orig_business"));
        }

        //
        if (ValidateUtils.isNullOrEmpt(curDoc, "orig_emoBusiness")) {
            origInfo4Cur.put("orig_emoBusiness", new JSONArray());
        } else {
            origInfo4Cur.put("orig_emoBusiness", curDoc.getJSONArray("orig_emoBusiness"));
        }
    }

    public static boolean isExistFieldWrap(final String tokenFiledExp, final JSONObject grabDoc) throws JSONException {
        if (tokenFiledExp.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            String[] result = tokenFiledExp.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            if (!ValidateUtils.isNullOrEmpt(grabDoc, result[0])) {
                return isExistFieldLoop(result, 1, grabDoc.getJSONObject(result[0]));
            } else {
                return false;
            }
        } else {
            return isExistField(tokenFiledExp, grabDoc);
        }
    }

    private static boolean isExistField(final String fieldName, final JSONObject grabDoc) throws JSONException {
        return !ValidateUtils.isNullOrEmpt(grabDoc, fieldName);
    }

    private static boolean isExistFieldLoop(final String[] tokenFiledExp, final int pathIdx, final JSONObject grabDoc) throws JSONException {
        if (pathIdx >= tokenFiledExp.length - 1) {
            //最后一级
            return isExistField(tokenFiledExp[pathIdx], grabDoc);
        } else {
            if (!ValidateUtils.isNullOrEmpt(grabDoc, tokenFiledExp[pathIdx])) {
                return isExistFieldLoop(tokenFiledExp, pathIdx + 1, grabDoc.getJSONObject(tokenFiledExp[pathIdx]));
            } else {
                return false;
            }
        }
    }


    public static final void converStruct4SegField(final String fieldName, final JSONObject grabDoc) throws JSONException {
        if (grabDoc.isNull(fieldName)) {
            return;
        }
        Object value = grabDoc.get(fieldName);
        JSONObject newValue = null;
        if (JsonUtils.isSimpleType(value)) {
            newValue = new JSONObject();
            newValue.put(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD, value);
            grabDoc.put(fieldName, newValue);
        } else if (value instanceof JSONArray) {
            if (((JSONArray) value).length() <= 0) {
                return;
            }

            if (SegmentFieldsConfig.PARAGRAPH_FILEDS_SETS.contains(fieldName) && ((JSONArray) value).length() > 0) {
                //处理 pg_text 等 需要分段的字段
                JSONArray newArrayValue = new JSONArray();
                String innerValue = null;
                for (int i = 0; i < ((JSONArray) value).length(); i++) {
                    innerValue = (String) ((JSONArray) value).get(i);
                    newValue = new JSONObject();
                    newValue.put(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD, innerValue);
                    newArrayValue.put(i, newValue);
                }
                grabDoc.put(fieldName, newArrayValue);
            } else {
                value = ((JSONArray) value).get(0);
                newValue = new JSONObject();
                newValue.put(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD, value);
                grabDoc.put(fieldName, newValue);
            }
        } else if (value instanceof JSONObject) {
            if (((JSONObject) value).isNull(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD)) {
                //content 为空
                grabDoc.remove(fieldName);
                throw new RuntimeException("converStruct4SegField exception: [content] for this field value is null.");
            } else {
                //content 已经存在 是否需要增加 terms 结构
                return;
            }
        }
        //newValue.put(SegmenteJob.TERMS_NAME_FOR_SEG_FIELD, new JSONArray());
    }

    public static final void converStruct4SegFieldLoop(final String fieldName, String[] fieldPaths, int pathIdx, final JSONObject grabDoc) throws JSONException {
        if (pathIdx >= (fieldPaths.length - 1)) {
            //左右一层
            converStruct4SegField(fieldPaths[pathIdx], grabDoc);
        } else {
            if (ValidateUtils.isNullOrEmpt(grabDoc, fieldPaths[pathIdx])) {
                return;
            } else {
                JSONObject childDoc = grabDoc.getJSONObject(fieldPaths[pathIdx]);
                converStruct4SegFieldLoop(fieldName, fieldPaths, pathIdx + 1, childDoc);
            }
        }
    }

    public static final void converStruct4SegFieldWrap(final String fieldName, final JSONObject grabDoc) throws JSONException {
        if (fieldName.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            String[] fieldPath = fieldName.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            if (ValidateUtils.isNullOrEmpt(grabDoc, fieldPath[0])) {
                return;
            }
            JSONObject childDoc = grabDoc.getJSONObject(fieldPath[0]);
            converStruct4SegFieldLoop(fieldName, fieldPath, 1, childDoc);
        } else {
            converStruct4SegField(fieldName, grabDoc);
        }
    }

    public static void supplementContentType(final JSONObject doc) throws JSONException {
        //
        if (ValidateUtils.isNullOrEmpt(doc, SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD)) {
            for (String fieldName : SegmentFieldsConfig.COMMENT_FIELDS) {
                if (!ValidateUtils.isNullOrEmpt(doc, fieldName)) {
                    //评论
                    doc.put(SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD, SegmentFieldsConfig.CONTENT_TYPE_COMMENT);
                    return;
                }
            }

            if (!ValidateUtils.isNullOrEmpt(doc, SegmentFieldsConfig.CONTENT_FILED_FLOOR)) {
                int floor = doc.getInt(SegmentFieldsConfig.CONTENT_FILED_FLOOR);
                if (floor == 0) {
                    //原创
                    doc.put(SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD, SegmentFieldsConfig.CONTENT_TYPE_ORIGINAL);
                } else if (floor == -1) {
                    //增量数据
                    doc.put(SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD, SegmentFieldsConfig.CONTENT_TYPE_APPEND);
                } else {
                    //评论数据
                    doc.put(SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD, SegmentFieldsConfig.CONTENT_TYPE_COMMENT);
                }
            } else {
                //原创
                doc.put(SegmentFieldsConfig.CONTENT_TYPE_NAME_FOR_SEG_FIELD, SegmentFieldsConfig.CONTENT_TYPE_ORIGINAL);
            }
        }
    }

    /**
     * 将需要分段的字段进行数据格式转化<BR>
     * <p>
     * 在进行分词时候，将该结构提交到PHP中，PHP中将该结构的数据(分词结果以及其他一些业务字段)按照分段后的格式进行直接赋值
     * <p>
     * doc
     * **+--pg_text
     * ********+--JSonObj
     * **************+--tokenFiled:
     * **********************+--content:ab
     * **********************+--terms:["a","b"]
     * **************+filed1:xxx
     * **************+filed2:xxx
     *
     * @param fieldNameExpr
     * @param grabDoc
     */
    public static void transferParagraphFieldBy(final String fieldNameExpr, JSONObject grabDoc) throws JSONException {
        if (fieldNameExpr.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            String[] fieldPath = fieldNameExpr.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            if (ValidateUtils.isNullOrEmpt(grabDoc, fieldPath[0])) {
                return;
            }
            JSONObject childDoc = grabDoc.getJSONObject(fieldPath[0]);
            transferParagraphFieldLoop(fieldNameExpr, fieldPath, 1, childDoc);
        } else {
            converStruct4ParagraphField(fieldNameExpr, grabDoc);
        }
    }


    private static void transferParagraphFieldLoop(final String fieldNameExpr, String[] fieldPaths, int pathIdx, JSONObject grabDoc) throws JSONException {
        if (pathIdx >= (fieldPaths.length - 1)) {
            //最后一层
            converStruct4ParagraphField(fieldPaths[pathIdx], grabDoc);
        } else {
            if (ValidateUtils.isNullOrEmpt(grabDoc, fieldPaths[pathIdx])) {
                return;
            } else {
                JSONObject childDoc = grabDoc.getJSONObject(fieldPaths[pathIdx]);
                converStruct4SegFieldLoop(fieldNameExpr, fieldPaths, pathIdx + 1, childDoc);
            }
        }
    }

    private static void converStruct4ParagraphField(final String fieldNameExpr, JSONObject grabDoc) throws JSONException {
        Object value = grabDoc.get(fieldNameExpr);
        if (!(value instanceof JSONArray)) {
            throw new RuntimeException("converStruct4ParagraphField excption,filed value for:[" + fieldNameExpr + "] is not an JsonArray!");
        }
        JSONArray arrayValue = (JSONArray) value;
        //
        JSONObject tokenOjb = null;
        for (int i = 0; i < arrayValue.length(); i++) {
            tokenOjb = (JSONObject) arrayValue.get(i);
            tokenOjb = wrapperDocOutter4ParagraphFiled(tokenOjb, fieldNameExpr);
            arrayValue.put(i, tokenOjb);
        }
    }

    private static JSONObject wrapperDocOutter4ParagraphFiled(final JSONObject graphValue, final String filedName) throws JSONException {
        JSONObject wrapper = new JSONObject();
        wrapper.put(filedName, graphValue);
        return wrapper;
    }
}
