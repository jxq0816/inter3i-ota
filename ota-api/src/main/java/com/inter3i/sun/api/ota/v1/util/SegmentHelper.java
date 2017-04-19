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

import com.inter3i.sun.api.ota.v1.config.SegmentFieldsConfig;
import com.inter3i.sun.api.ota.v1.job.schedule.SegmenteJob;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SegmentHelper {

    /**
     * tokenresult
     * +-[]
     * responseHeader
     * +-{"QTime":15,"QTimeUs":10576,"status":0}
     */
    public static final String SEG_RESPONSE_HEADER_KEY = "responseHeader";
    public static final String SEG_RESPONSE_BODY_KEY = "tokenresult";


    /**
     * @param fieldval
     * @return
     * @throws JSONException
     */
    public static String getTokenFieldVal(Object fieldval) throws JSONException {
        String reslut = null;
        if (fieldval instanceof JSONObject) {
            JSONObject jsonData = (JSONObject) fieldval;
            if (!jsonData.isNull(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD)) {
                reslut = jsonData.getString(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD); //新增数据时
            }
            //            else if (isset($fieldval[0])) {
            //                $t = $fieldval[0]; //更新数据时从solr查出
            //            }
            else {
                reslut = "";
            }
        } else if (fieldval instanceof String) {
            reslut = (String) fieldval;
        } else if (fieldval instanceof JSONArray) {
            JSONArray jsonData = (JSONArray) fieldval;
            if (jsonData.length() > 0) {
                reslut = (String) jsonData.get(0);
            } else {
                reslut = "";
            }
        }
        return reslut;
    }

    public static String getTokenFieldValByPathExp(JSONObject josn, String tokenFiledExp) throws JSONException {
        String[] result = tokenFiledExp.split(SegmentFieldsConfig.FIELD_PATH_SEP);
        if (!ValidateUtils.isNullOrEmpt(josn, result[0])) {
            return getTokenFieldValByPathExpLoop(result, 1, josn.get(result[0]));
        }
        return "";
    }

    private static String getTokenFieldValByPathExpLoop(final String[] tokenFiledExp, final int pathIdx, final Object grabDoc) throws JSONException {
        if (pathIdx < tokenFiledExp.length) {
            if (!ValidateUtils.isNullOrEmpt((JSONObject) grabDoc, tokenFiledExp[pathIdx])) {
                return getTokenFieldValByPathExpLoop(tokenFiledExp, pathIdx + 1, ((JSONObject) grabDoc).get(tokenFiledExp[pathIdx]));
            } else {
                return "";
            }
        } else {
            return grabDoc == null ? "" : getTokenFieldVal(grabDoc);
        }
    }


    public static JSONArray setTerms(final String fieldName, final JSONObject doc, JSONObject analysisResult) throws JSONException {
        if (doc.isNull(fieldName)) {
            throw new RuntimeException("setTerms by fieldName[" + fieldName + "] excption. field for current doc is null.");
        }

        JSONArray terms = null;
        if (analysisResult.isNull(SegmentFieldsConfig.ANALYSIS_TERMS_KEY)) {
            terms = new JSONArray();
        } else {
            terms = analysisResult.getJSONArray(SegmentFieldsConfig.ANALYSIS_TERMS_KEY);
        }

        Object fieldValue = doc.get(fieldName);
        if (JsonUtils.isSimpleType(fieldValue)) {
            JSONObject newValue = new JSONObject();
            newValue.put(SegmentFieldsConfig.CONTENT_NAME_FOR_SEG_FIELD, fieldValue);
            newValue.put(SegmentFieldsConfig.TERMS_NAME_FOR_SEG_FIELD, terms);
            doc.put(fieldName, newValue);
        } else if (fieldValue instanceof JSONObject) {
            ((JSONObject) fieldValue).put(SegmentFieldsConfig.TERMS_NAME_FOR_SEG_FIELD, terms);
        } else if (fieldValue instanceof JSONArray) {
            throw new RuntimeException("setTerms for dco by current data struct not supported!");
        }
        return terms;
    }

    public static void setTerms4Text(final String fieldName, final JSONObject doc, JSONObject analysisResult) throws JSONException {
        JSONArray terms = setTerms(fieldName, doc, analysisResult);
        //similar生成MD5
        StringBuilder termsStr = new StringBuilder();
        for (int termIdx = 0; termIdx < terms.length(); termIdx++) {
            termsStr.append(terms.get(termIdx));
        }
        //给当前微博 ($weibo) 增加属性:similar 表示当前微博的散列值:及所有分词的term的MD5值
        JSONArray similar = new JSONArray();
        //String md5String = md5(termsStr);
        //doc.put("similar", md5String);
    }

    /**
     * 将分词结果中对应的结果设置到目标文档上面去<BR>
     * 该方法将分词结果中的textFiledName字段对应的值，设置到目标文档中去；如果是设置全文字段，则需要提供分词<BR>
     * 结果中的纯文本分词的结果在分词结果中对应的字段名称:coverageFieldName. 该字段默认为“text”<BR>
     * 将会把“text”字段中的所有term设置到doc中的textFiledName字段的值的"terms"的key中.
     *
     * @param textFiledName
     * @param doc
     * @param analysisResult
     * @param coverageFieldName
     * @throws JSONException
     */
    public static void setTerms4CommonField(final String textFiledName, final JSONObject doc, JSONObject analysisResult, final String coverageFieldName) throws JSONException {
        if (ValidateUtils.isNullOrEmpt(coverageFieldName)) {
            if (!ValidateUtils.isNullOrEmpt(analysisResult, textFiledName)) {
                Object targetFieldValue = analysisResult.get(textFiledName);
                doc.put(textFiledName, targetFieldValue);
            }
        } else {
            //设置 terms 变量   analysisResult.text
            if (!ValidateUtils.isNullOrEmpt(analysisResult, coverageFieldName)) {
                Object targetFieldValue = analysisResult.get(coverageFieldName);
                JSONObject fieldValue = doc.getJSONObject(textFiledName);
                fieldValue.put(SegmentFieldsConfig.TERMS_NAME_FOR_SEG_FIELD, targetFieldValue);
            }
        }
    }

    public static void setTerms4CommonFieldLoop(final String textFiledName, String[] fieldPaths, int pathIdx, final JSONObject doc, JSONObject analysisResult, final String coverageFieldName) throws JSONException {
        if (pathIdx >= (fieldPaths.length - 1)) {
            //最后一层
            setTerms4CommonField(textFiledName, doc, analysisResult, coverageFieldName);
        } else {
            //TODO if this key in doc is null?
            JSONObject childDoc = doc.getJSONObject(fieldPaths[pathIdx]);
            setTerms4CommonFieldLoop(fieldPaths[pathIdx + 1], fieldPaths, pathIdx + 1, childDoc, analysisResult, coverageFieldName);
        }
    }

    public static void setTerms4TextEmotionField(final String textFiledName, final JSONObject doc, JSONObject analysisResult) throws JSONException {
        if (textFiledName.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            int curPathIdx = 0;
            String[] paths = textFiledName.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            JSONObject childDoc = doc.getJSONObject(paths[0]);
            setTerms4CommonFieldLoop(paths[1], paths, curPathIdx + 1, childDoc, analysisResult, null);
        } else {
            setTerms4CommonField(textFiledName, doc, analysisResult, null);
        }
    }

    public static void setTerms4CommonFieldWrap(final String textFiledName, final JSONObject doc, JSONObject analysisResult) throws JSONException {
        if (textFiledName.contains(SegmentFieldsConfig.FIELD_PATH_SEP_CHAR)) {
            int curPathIdx = 0;
            String[] paths = textFiledName.split(SegmentFieldsConfig.FIELD_PATH_SEP);
            JSONObject childDoc = doc.getJSONObject(paths[0]);
            setTerms4CommonFieldLoop(paths[1], paths, curPathIdx + 1, childDoc, analysisResult, SegmentFieldsConfig.ANALYSIS_TERMS_KEY);
        } else {
            setTerms4CommonField(textFiledName, doc, analysisResult, SegmentFieldsConfig.ANALYSIS_TERMS_KEY);
        }
    }
}
