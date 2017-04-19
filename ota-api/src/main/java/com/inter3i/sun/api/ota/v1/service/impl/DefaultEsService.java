package com.inter3i.sun.api.ota.v1.service.impl;

import com.inter3i.sun.api.ota.v1.EsUtil;
import com.inter3i.sun.api.ota.v1.service.EsService;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHitField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.*;

/**
 * Created by 刘晓磊 on 2016/11/21.
 * Es查询接口实现类
 */
@SuppressWarnings("CallToPrintStackTrace")
public class DefaultEsService  implements EsService {


    public TransportClient getClient() {
            String host="192.168.0.20";
        //es访问端口
        int  port= 65000;
         String clusterName="elasticsearch_cluster_one";
        TransportClient client = null;
//        InputStream inputStream = Object.class.getResourceAsStream("/esClent.properties");
//        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") Properties prop = new Properties();
        try {
//            prop.load(inputStream);
//            System.out.println(prop.getProperty("clusterName").trim());
//            String clusterName1 = prop.getProperty("clusterName").trim();
            Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
            client = TransportClient.builder().settings(settings).build().
                    addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * 通过条件查询Es
     * @param esdata body传入的参数
     * @param featureId 特征分类用"逗号"分隔
     * @param customerId 用户唯一ID
     * @return BoolQueryBuilder 返回需要执行的语句
     */
    public BoolQueryBuilder searchBuilder(String esdata, String featureId, String customerId)  {
        JSONObject jsonobj;
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        String [] Characteristics= featureId.split(",");
        try {
            jsonobj = new JSONObject(esdata);

            JSONObject filterJson = jsonobj.getJSONObject("filter");
            if(filterJson.has("and")){
                JSONArray andJsonArray = filterJson.getJSONArray("and");
                for (int i = 0 ; i< andJsonArray.length(); i++){
                    JSONObject jsonObject = andJsonArray.getJSONObject(i);
                    if (jsonObject.has("eq")){
                        shouldpj.must(eqRelationShip(jsonObject.getString("fieldname"),jsonObject.getString("eq")));
                    }
                    if (jsonObject.has("range")){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("range");
                        String gte = jsonObject1.getString("gte");
                        String lte = jsonObject1.getString("lt");
                        shouldpj.must( rangeRelationShip(jsonObject.getString("fieldname"),gte,lte));
                    }
                    if (jsonObject.has("in")){
                        JSONArray jsonArray = jsonObject.getJSONArray("in");
                        shouldpj.must(inRelationShip(jsonObject.getString("fieldname"),jsonArray));
                    }
                    if (jsonObject.has("nin")){
                        JSONArray jsonArray = jsonObject.getJSONArray("nin");
//                        for (int j = 0 ; j < jsonArray.length() ; j++){
////                            System.out.println("nin "+jsonArray.optString(j));
//                        }
                        shouldpj.mustNot(ninRelationShip(jsonObject.getString("fieldname"), jsonArray));
                    }
                    if (!"".equals(customerId)){
                        shouldpj.must(eqRelationShip("customerId",customerId));
                    }
                    if (Characteristics.length > 1 ){
                        shouldpj.must(inCharacteristics("text.term.text",Characteristics));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  shouldpj;
    }

    /**
     *通过字段名称和字段值进行等于匹配
     * @param fieldname 字段名称
     * @param value  字段值
     * @return shouldpj
     */
    public BoolQueryBuilder eqRelationShip(String fieldname, String value) {
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        shouldpj.should(QueryBuilders.termQuery(fieldname,value));
        return  shouldpj;
    }

    /**
     *范围查询
     * @param fieldname 字段名称
     * @param gte  大于等于
     * @param lt    小于
     * @return shouldpj 返回范围拼接条件
     */
    public BoolQueryBuilder rangeRelationShip(String fieldname, String gte, String lt) {
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        shouldpj.should(QueryBuilders.rangeQuery(fieldname).from(gte).to(lt));
        return shouldpj;
    }

    /**
     *包含查询
     * @param fieldname 字段名称
     * @param jsonArrayin  json数据
     * @return shouldpj 返回in的拼接条件
     */
    public BoolQueryBuilder inRelationShip(String fieldname, JSONArray jsonArrayin) {
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        for (int j = 0 ; j < jsonArrayin.length() ; j++){
            shouldpj.should(QueryBuilders.termQuery(fieldname,jsonArrayin.optString(j)));
        }
        return  shouldpj;
    }

    /**
     *不包含查询
     * @param fieldname 字段名称
     * @param jsonArrayin  json数据
     * @return shouldpj 返回not in 的拼接条件
     */
    public BoolQueryBuilder ninRelationShip(String fieldname, JSONArray jsonArrayin) {
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        for (int j = 0 ; j < jsonArrayin.length() ; j++){
            shouldpj.should(QueryBuilders.termQuery(fieldname,jsonArrayin.optString(j)));
        }
        return  shouldpj;
    }

    /**
     *  新增Es数据
     * @param client  Es客户端
     * @param index   索引
     * @param type     类型
     * @param id        数据id
     * @param data      数据
     */
    public void save(TransportClient client, String index, String type, String id, String data) {
        client.prepareIndex(index, type,id)
                .setSource(data).execute().actionGet();
    }

    /**
     * 创建索引
     * @param client Es客户端
     * @param index 索引
     * @param type 类型
     */
    public void createEsIndex(TransportClient client, String index, String type) {
        client.prepareIndex(index, type)
                .execute().actionGet();
    }

    /**
     *
     * @param client Es客户端
     * @param index  索引
     * @return 是否成功
     */
    public boolean deleteEsIndex(TransportClient client, String index) {
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        if(inExistsResponse.isExists()){
            client.admin().indices().prepareDelete(index).execute().actionGet();
            return true;
        }else{
            return false;
        }
    }

    /**
     *此接口还需要调用mogdb
     * @param fieldname 字段名称
     * @param characteristics  特征分类id
     * @return shouldpj  返回特征分类的条件
     */
    public BoolQueryBuilder inCharacteristics(String fieldname, String[] characteristics) {
        BoolQueryBuilder shouldpj = new BoolQueryBuilder();
        for (String characteristic : characteristics) {
//            shouldpj.should(QueryBuilders.termQuery(fieldname,characteristics[j]));
        }
        return  shouldpj;
    }
    /**
     *
     * @param mapOut 查询后的结果
     * @param outInfoArray  需要输出的词
     * @param highlightedArray 需要高亮的词
     * @return outInfo 返回输出信息
     */
    public JSONObject outputInformation(Map<String,SearchHitField> mapOut, JSONArray outInfoArray, JSONArray highlightedArray){
        JSONObject outInfo = new JSONObject();
        for (int j = 0 ; j < outInfoArray.length()-1 ; j++){
            try {
                if ("text".equals(outInfoArray.optString(j)) || "pg_text".equals(outInfoArray.optString(j))
                        || "description".equals(outInfoArray.optString(j)) || "post_title".equals(outInfoArray.optString(j))
                        || "apdComment".equals(outInfoArray.optString(j)) || "productDesc".equals(outInfoArray.optString(j))){
                    outInfo.put(outInfoArray.optString(j),outputHighligh(mapOut,highlightedArray,outInfoArray.optString(j)));
                }else{
                    if (mapOut.containsKey(outInfoArray.optString(j))){
                        //outInfo.put(outInfoArray.optString(j), mapOut.get(outInfoArray.optString(j)).getValue());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return outInfo;
    }

    /**
     *
     * @param mapOut 查询后的结果
     * @param field  字段
     * @param highlightedArray 需要高亮的词
     * @return String 返回对内容做高亮后的内容
     */
    public String outputHighligh(Map<String,SearchHitField> mapOut,JSONArray highlightedArray,String field)  {
        EsUtil esUtil = new EsUtil();
        return esUtil.EsResultSort(getHighlightcount(mapOut,highlightedArray),getContent(mapOut,field));

    }

    /**
     *
     * @param mapOut 查询后的结果
     * @param  highlightedArray 高亮值数组
     * @return map 返回对应的原文
     */
    public Map<Integer,Integer> getHighlightcount(Map<String, SearchHitField> mapOut, JSONArray highlightedArray) {
        Map<Integer,Integer> map = null;
        try {
            for (int l = 0; l < highlightedArray.length(); l++) {
                JSONObject highlightedJson = highlightedArray.getJSONObject(l);
                String highlightName = highlightedJson.getString("Highlightedname");
                JSONArray highlightValue = highlightedJson.getJSONArray("value");
                map = getSubscript(mapOut,highlightValue,highlightName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     *
     * @param mapOut 查询后的结果
     * @param highlightName  高亮字段
     * @param  highlightValue 高亮值数组
     * @return map 返回需要高亮的开始、结束下标并排序
     */
    public Map<Integer,Integer> getSubscript (Map<String,SearchHitField> mapOut,JSONArray highlightValue,String highlightName){
        //定义map用于进行排序，key为开始下标,value为结束下标
        Map<Integer,Integer> map = new TreeMap<Integer, Integer>(
                new Comparator<Integer>() {
                    public int compare(Integer obj1, Integer obj2) {
                        return obj1.compareTo(obj2);
                    }
                }
        );
        //取出所有需要高亮值的开始和结束下标
        List<Object> text = mapOut.get(highlightName+".terms.text").getValues();
        List<Object> start = mapOut.get(highlightName+".terms.start").getValues();
        List<Object> end = mapOut.get(highlightName+".terms.end").getValues();
        for (int i = 0 ;i < highlightValue.length()-1 ; i++){
            for (int j = 0; j < text.size() ; j++){
                if (text.get(j).toString().equals(highlightValue.optString(i))){
                    map.put(Integer.valueOf(start.get(j).toString()),Integer.valueOf(end.get(j).toString()));
                }
            }
        }

        //取出所有需要高亮值的开始和结束下标
//        if (hitJson.has(highlightName)){
//            try {
//                JSONObject jsonText = hitJson.getJSONObject(highlightName);
//                JSONArray jsonArrayTerm = jsonText.getJSONArray("terms");
//                for (int i = 0 ;i < highlightValue.length() ; i++){
//                    for (int j = 0 ; j < jsonArrayTerm.length()-1 ; j++){
//                        JSONObject jsonTerm = jsonArrayTerm.getJSONObject(j);
//                        if (jsonTerm.getString("text").equals(highlightValue.optString(i))){
//                            map.put(jsonTerm.getInt("start"),jsonTerm.getInt("end"));
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
        return map;
    }
    /**
     *
     * @param mapOut 查询后的结果
     * @param field  字段
     * @return content 返回对应的原文
     */
    public StringBuilder getContent(Map<String,SearchHitField> mapOut,String field){
        SearchHitField searchHitField = mapOut.get("text.content");
        return new StringBuilder(searchHitField.getValue().toString());
    }
}
