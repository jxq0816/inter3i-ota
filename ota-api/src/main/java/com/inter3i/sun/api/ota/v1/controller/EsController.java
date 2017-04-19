package com.inter3i.sun.api.ota.v1.controller;

import com.inter3i.sun.api.ota.v1.service.EsService;
import com.inter3i.sun.api.ota.v1.service.ServiceFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by 刘晓磊 on 2016/11/21.
 * esWebSever控制器
 */
@RestController("/esquery")
class EsController {
    private final EsService esService;


    EsController() {
        super();
        this.esService = ServiceFactory.esService();
    }


    @SuppressWarnings("NestedMethodCall")
    @RequestMapping(produces = "text/plain;charset=UTF-8", method = RequestMethod.GET)
    String search(@RequestBody String esdatepos, String featureId, String customerId) {
        long startTime = System.currentTimeMillis();
        TransportClient client = this.esService.getClient();
        JSONObject jsonobj;
        try {
            jsonobj = new JSONObject(esdatepos);
            JSONArray filterSsort = jsonobj.getJSONArray("sort");
            JSONArray outInfo = jsonobj.getJSONArray("output");
            JSONArray highlighted = jsonobj.getJSONArray("HighlightedField");
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("lvyou9").setTypes("lvyou").setQuery(this.esService.searchBuilder(esdatepos,featureId,customerId)).setFrom(jsonobj.getInt("start_page")).setSize(jsonobj.getInt("page_size"));
            //拼接排序
            for (int j = 0; j < (filterSsort.length() - 1); j++){
                JSONObject jsonObject = filterSsort.getJSONObject(j);
                searchRequestBuilder.addSort(jsonObject.getString("orderbyname"),"asc".equals(jsonObject.getString("orderbytype"))?SortOrder.ASC:SortOrder.DESC);
            }
            //拼接返回信息
            for (int i = 0; i < (outInfo.length() - 1); i++){
                if("text".equals(outInfo.optString(i)) || "pg_text".equals(outInfo.optString(i))
                        || "description".equals(outInfo.optString(i)) || "post_title".equals(outInfo.optString(i))
                        || "apdComment".equals(outInfo.optString(i)) || "productDesc".equals(outInfo.optString(i))){
                    searchRequestBuilder.addField(outInfo.getString(i)+".content");
                    searchRequestBuilder.addField(outInfo.getString(i)+".terms.text");
                    searchRequestBuilder.addField(outInfo.getString(i)+".terms.start");
                    searchRequestBuilder.addField(outInfo.getString(i)+".terms.end");
                }else{
                    searchRequestBuilder.addField(outInfo.getString(i));
                }

            }
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            JSONObject outbject = new JSONObject();
            outbject.put("total",searchHits.getTotalHits());
            outbject.put("start_page",jsonobj.getInt("start_page"));
            outbject.put("page_size",jsonobj.getInt("page_size"));
            JSONArray outArray = new JSONArray();
            for (SearchHit hit : hits) {
                Map<String,SearchHitField> searchMap = hit.getFields();
                outArray.put(this.esService.outputInformation(searchMap,outInfo,highlighted));
                outbject.put("filter",outArray);
            }
                long endTime = System.currentTimeMillis();
            System.out.println("程序运行时间: " + (endTime - startTime) + "ms");
            return outbject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    void interData(String index, String type, String id, String data){
        TransportClient client = this.esService.getClient();
        this.esService.save(client,index,type,id,data);
    }
}
