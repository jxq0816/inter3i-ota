/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/05/05
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.controller;

import com.inter3i.sun.api.ota.v1.config.ImportDataConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.job.SegmentAdapterTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class SegmentController {


    private static MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig();

    public static void main(String[] args) {
        String cacheName = "cache02";
        JSONObject jsonDocStr= null;
        String encoding = "utf-8";
        String filePath = "E:\\file\\mongo.txt";
        File file = new File(filePath);
        StringBuilder builder = new StringBuilder();
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = null;//考虑到编码格式
            try {
                read = new InputStreamReader(new FileInputStream(file), encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader bufferedReader = new BufferedReader(read);

            String lineTxt;


            try {
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    builder.append(lineTxt);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            JSONObject requestData = new JSONObject(builder.toString());
            jsonDocStr=requestData.getJSONObject("jsonDoc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SegmentAdapterTest segmentAdapter = new SegmentAdapterTest(serverConfig, cacheName, ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getDataCollectionBy(cacheName));
        JSONObject jsonObject=segmentAdapter.doJsonSegmentFromNLP(jsonDocStr);
        try {
            JSONArray datas=jsonObject.getJSONArray("datas");
            System.out.print(datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
