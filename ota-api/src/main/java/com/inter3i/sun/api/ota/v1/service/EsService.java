package com.inter3i.sun.api.ota.v1.service;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHitField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 刘晓磊 on 2016/11/21.
 * Es查询接口
 */
public interface EsService {

    TransportClient getClient();
    /**
     * 通过条件查询Es
     * @param esdata body传入的参数
     * @param featureId 特征分类用"逗号"分隔
     * @return BoolQueryBuilder 返回需要执行的语句
     */
    BoolQueryBuilder searchBuilder (String esdata, String featureId,String customerId);

    /**
     *通过字段名称和字段值进行等于匹配
     * @param fieldname 字段名称
     * @param value  字段值
     */
    BoolQueryBuilder eqRelationShip(String fieldname,String value);

    /**
     *范围查询
     * @param fieldname 字段名称
     * @param gte  大于等于
     * @param lt    小于
     */
    BoolQueryBuilder rangeRelationShip(String fieldname,String gte,String lt);

    /**
     *包含查询
     * @param fieldname 字段名称
     * @param jsonArrayin  json数据
     */
    BoolQueryBuilder inRelationShip(String fieldname,JSONArray jsonArrayin);

    /**
     *不包含查询
     * @param fieldname 字段名称
     * @param jsonArrayin  json数据
     */
    BoolQueryBuilder ninRelationShip(String fieldname,JSONArray jsonArrayin);

    /**
     *  新增Es数据
     * @param client  Es客户端
     * @param index   索引
     * @param type     类型
     * @param id        数据id
     * @param data      数据
     */
    void save(TransportClient client, String index, String type,String id, String data);


    /**
     * 创建索引
     * @param client Es客户端
     * @param index 索引
     * @param type 类型
     */
    void createEsIndex(TransportClient client,String index,String type);


    /**
     *
     * @param client Es客户端
     * @param index  索引
     * @return 是否成功
     */
    boolean deleteEsIndex(TransportClient client,String index);

    /**
     *此接口还需要调用mogdb
     * @param fieldname 字段名称
     * @param characteristics  特征分类id
     */
    BoolQueryBuilder inCharacteristics(String fieldname, String[] characteristics);

    /**
     *
     * @param mapOut 查询后的结果
     * @param outInfoArray  需要输出的词
     * @param highlightedArray 需要高亮的词
     * @return outInfo 返回输出信息
     */
    JSONObject outputInformation(Map<String,SearchHitField> mapOut, JSONArray outInfoArray, JSONArray highlightedArray);

    /**
     *
     * @param mapOut 查询后的结果
     * @param field  字段
     * @param highlightedArray 需要高亮的词
     * @return String 返回对内容做高亮后的内容
     */
    String outputHighligh(Map<String,SearchHitField> mapOut,JSONArray highlightedArray,String field);

    /**
     *
     * @param mapOut 查询后的结果
     * @param field  字段
     * @return content 返回对应的原文
     */
    StringBuilder getContent(Map<String,SearchHitField> mapOut,String field);
    /**
     *
     * @param mapOut 查询后的结果
     * @param highlightName  高亮字段
     * @param  highlightValue 高亮值数组
     * @return map 返回需要高亮的开始、结束下标并排序
     */
    Map<Integer,Integer> getSubscript (Map<String,SearchHitField> mapOut,JSONArray highlightValue,String highlightName);

    /**
     *
     * @param mapOut 查询后的结果
     * @param  highlightedArray 高亮值数组
     * @return map 返回对应的原文
     */
    Map<Integer,Integer> getHighlightcount(Map<String, SearchHitField> mapOut, JSONArray highlightedArray);
}
