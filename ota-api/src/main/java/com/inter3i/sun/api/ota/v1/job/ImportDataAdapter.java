/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/05/03
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.job;

import com.inter3i.sun.api.ota.v1.config.IMongoDocConverter;
import com.inter3i.sun.api.ota.v1.config.ImportDataConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.net.HttpUtils;
import com.inter3i.sun.api.ota.v1.util.MongoUtils;
import com.inter3i.sun.api.ota.v1.util.ValidateUtils;
import com.inter3i.sun.persistence.dataimport.CommonData;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ImportDataAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataAdapter.class);
    //    public static final ImportLock IMPORT_LOCK = new ImportLock();
    public static final Map<String, ImportLock> cacheLocks = new HashMap<String, ImportLock>(4);

    public static ImportLock getLockBy(final String cacheName) {
        if (cacheLocks.containsKey(cacheName)) {
            return cacheLocks.get(cacheName);
        }
        synchronized (cacheLocks) {
            if (!cacheLocks.containsKey(cacheName)) {
                cacheLocks.put(cacheName, new ImportLock());
            }
            return cacheLocks.get(cacheName);
        }
    }


    private final String cacheName;
    private final MongoCollection dbDataCollection;
    private final MongoCollection dbSupplyCollection;
    private final MongoDBServerConfig serverConfig;
    private boolean isForJob;

    public ImportDataAdapter(final String cacheName, final MongoCollection dbDataCollection, final MongoCollection dbSupplyCollection, final MongoDBServerConfig serverConfig) {
        this(cacheName, dbDataCollection, dbSupplyCollection, serverConfig, true);
    }

    public ImportDataAdapter(final String cacheName, final MongoCollection dbDataCollection, final MongoCollection dbSupplyCollection, final MongoDBServerConfig serverConfig, boolean isJob) {
        this.cacheName = cacheName;
        this.isForJob = isJob;
        if (dbDataCollection == null) {
            String errorMsg = "create ImportDataAdapter excption:dbDataCollection is null!";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        if (dbSupplyCollection == null) {
            String errorMsg = "create ImportDataAdapter excption:dbSupplyCollection is null!";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        if (serverConfig == null) {
            String errorMsg = "create ImportDataAdapter excption:serverConfig is null!";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        this.dbDataCollection = dbDataCollection;
        this.dbSupplyCollection = dbSupplyCollection;
        this.serverConfig = serverConfig;
    }

    /**
     *
     */
    public void importDoc2Solr() {
        if (isForJob && !getLockBy(this.cacheName).tryRun()) {
            logger.info("Job:[importDoc2Solr] for[" + cacheName + "] is running,give up current job.");
            return;
        }

        int handleDocNum = 0;
        long startTime = System.currentTimeMillis();
        Document taskData = null;
        Document[] batchDoc = null;

        int batchApdNum = 0;

        int maxDocNum = serverConfig.getDocNumPerImport() * serverConfig.getImportNumPerFlush();
        Object[] docIds = new Object[maxDocNum];
        long[] importSolrTimes = new long[maxDocNum];
        int[] allStatus = new int[maxDocNum];

        //批次、批量信息统计
        BathStatistic statistic = null;
        boolean isHandleDone = false;

        try {
            while (!isHandleDone) {

                //第一次 或者从 游标超时异常冲开始恢复
                statistic = new BathStatistic();
                try {
//            dbCollection = RepositoryFactory.getMongoClient(ImportDataConfig.DB_NAME, ImportDataConfig.TABLE_NAME_COMMON, serverConfig.getMongoDBAdds(), serverConfig.getMongoDBPort());
//            if (dbSupplyCollection == null) {
//                dbSupplyCollection = RepositoryFactory.getMongoClient(ImportDataConfig.DB_NAME, ImportDataConfig.TABLE_NAME_SUPPLY_DATA, serverConfig.getMongoDBAdds(), serverConfig.getMongoDBPort());
//            }

                    //查询出所有的没有入库以及分词成功的文章
                    Bson filter1 = Filters.eq("importStatus", CommonData.IMPORTSTATUS_NO_IMPORT); //没有入库
                    Bson filter2 = Filters.eq("segmentedStatus", CommonData.SEGMENTE_SATUS_SUCCESS);//分词成功
                    Bson conds = Filters.and(filter1,filter2);
                    //没有分词
                    FindIterable iterable = dbDataCollection.find(conds);

                    Iterator<Document> iterator = iterable.iterator();

                    Arrays.fill(allStatus, CommonData.IMPORTSTATUS_IMPORT_SUCCESS);

                    int batchNum = 0;
                    batchDoc = new Document[serverConfig.getDocNumPerImport()];

                    //针对大批次里面的小批次里面的文档，在整个大批次里面的偏移
                    int offSet = 0;
                    while (iterator.hasNext()) {
                        taskData = iterator.next();

                        batchDoc[batchNum] = taskData;
                        //小批次里面的文档的需要从0开始
                        batchNum++;
                        //总处理的文档个数
                        handleDocNum++;


                        if (batchNum >= serverConfig.getDocNumPerImport()) {

                            //累计的批次数
                            statistic.batchSequence++;
                            //批量入库
                            //20170428 入库时间 by jiangxingqi
                            importData2SolrDerect(batchDoc, dbDataCollection, offSet, docIds,importSolrTimes, allStatus, statistic);
                            Arrays.fill(batchDoc, null);

                            //满一个小批次，将小批次里面的文档编号置零
                            batchNum = 0;
                            //一个大批次里面的小批次号，在一个大批次里面，当小的批次数达到 BATCH_COMMIT_NUM 个后，会被置成0
                            batchApdNum++;
                            offSet += batchDoc.length;

                            //判断处理的批次数是否达到最大批次 达到最大批次则调用solr进行flush to desk 操作
                            if (batchApdNum >= serverConfig.getImportNumPerFlush()) {
                                //20170428 创建时间 by jiangxingqi
                                handleFlushAndUpadateStatus(docIds,importSolrTimes,allStatus, dbDataCollection);

                                // reset the docIds and the status
                                resetParam(docIds,importSolrTimes,allStatus);
                                //满一个大批次，将大批次里面的小批次编号置零
                                batchApdNum = 0;
                                offSet = 0;
                            }
                        }
                    }

                    if (batchDoc[0] != null) {//10001条时候
                        //累计的批次数
                        statistic.batchSequence++;
                        //20170428 入库时间 by jiangxingqi
                        importData2SolrDerect(batchDoc, dbDataCollection, offSet, docIds,importSolrTimes, allStatus, statistic);
                    }

                    if (docIds[0] != null) { //当不够10000时候，1000个文档时候，import10次，但是没有修改状态
                        //20170428 入库时间 by jiangxingqi
                        handleFlushAndUpadateStatus(docIds, importSolrTimes,allStatus, dbDataCollection);
                        // reset the docIds and the status and importSolrTimes
                        resetParam(docIds,importSolrTimes,allStatus);
                    }
                    //执行 更新文章的 docId/father_guid/retweeted_guid等原创信息
                    //handleNedSupplyDocs();

                    //增加处理完成标签
                    isHandleDone = true;

                } catch (Exception e) {
                    logger.error("Job:[importDoc2Solr] for[" + cacheName + "] run exception. ErrorMsg:[" + e.getMessage() + "] handleDocNum:[" + handleDocNum + "].", e);
                    try {
                        //TODO 与奥异常时候将已经处理的数据刷新
                        if (docIds[0] != null) {
                            //20170428 入库时间 by jiangxingqi
                            handleFlushAndUpadateStatus(docIds,importSolrTimes,allStatus, dbDataCollection);
                            // reset the docIds and the status
                            resetParam(docIds,importSolrTimes,allStatus);
                            //执行 更新文章的 docId/father_guid/retweeted_guid等原创信息
                            //handleNedSupplyDocs();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    String errorMsg = e.getMessage();
                    if (errorMsg.contains("Query failed with error code -5 and error message") && errorMsg.contains("Cursor") && errorMsg.contains("not found")) {
                        logger.warn("Job:[importDoc2Solr] for[" + cacheName + "] cursor timeout! will continue...");
                    } else {
                        throw new RuntimeException("Job:[importDoc2Solr] for[" + cacheName + "] excption:[" + e.getMessage() + "].", e);
                    }
                } finally {
                    long endTime = System.currentTimeMillis();
                    logger.debug("=====================================importDoc=============================================");
                    logger.info("Job:[importDoc2Solr] for[" + cacheName + "] run complete. handleDocNum:[" + handleDocNum + "]. spend:[" + (endTime - startTime) + "] ms.");
                    logger.debug("=====================================importDoc=============================================");
                }
            }
        } finally {
            if (isForJob) {
                getLockBy(this.cacheName).runComplete();
            }
        }
    }

    public static class ImportLock {
        private final Object InnerLock = new Object();
        private volatile boolean isRunning = false;

        public boolean isIsRunning() {
            return isRunning;
        }

        public boolean tryRun() {
            //正在运行
            if (isRunning) {
                return false;
            }

            synchronized (InnerLock) {
                if (isRunning) {
                    return false;
                }

                // 没有在运行入库任务
                isRunning = true;
                return true;
            }
        }

        public void runComplete() {
            synchronized (InnerLock) {
                if (!isRunning) {
                    throw new RuntimeException("runComplete exception, running flag is false");
                }
                isRunning = false;
            }
        }
    }

    /**
     *
     * @param taskDatas
     * @param dbCollection
     * @param offset
     * @param docIds
     * @param importSolrTimes 入库时间 by jiangxingqi
     * @param allStatus
     * @param statistic
     * @throws JSONException
     */
    private void importData2SolrDerect(final Document[] taskDatas, final MongoCollection dbCollection, int offset, final Object[] docIds,final long[] importSolrTimes, final int[] allStatus, final BathStatistic statistic) throws JSONException {
        int handleNum = 0;
        long startTime = System.currentTimeMillis();

        //数据提交范围
        int curBatchNum = ((statistic.flushSequence * serverConfig.getImportNumPerFlush()) + statistic.batchSequence);
        int start = ((curBatchNum - 1) * serverConfig.getDocNumPerImport()) + 1;
        int end = start + serverConfig.getDocNumPerImport() - 1;

        String segResultStr = null;
        try {
            logger.info("Job:[ImportDataJob] --+-importData2SolrDerect by batch, handle the :[" + start + " TO " + end + "] doc to solr, batchNum:[" + serverConfig.getDocNumPerImport() + "] ");
            JSONArray reqDatas = new JSONArray();
            String taskDataStr = null;
            JSONObject reqDoTmp = null;
            Document doc = null;

            int[] status = new int[taskDatas.length];
            //默认成功
            Arrays.fill(status, 1);

            //int curBathNum = 0;
            for (int t = 0; t < taskDatas.length; t++) {
                doc = taskDatas[t];

                if (ValidateUtils.isNullOrEmpt(doc)) {
                    break;
                }
                //curBathNum++;
                handleNum++;

                Document taskData = (Document) doc.get("jsonDoc");
                taskDataStr = taskData.toJson();
                reqDoTmp = new JSONObject(taskDataStr);
                reqDoTmp.put("id4cahe", t);
                reqDatas.put(t, reqDoTmp);
            }

            segResultStr = sendBatchDoc(reqDatas, status);
            logger.info("Job:[ImportDataJob] --+-importData2SolrDerect by batch, handle the :[" + start + " TO " + end + "] doc to solr success! batchNum:[" + serverConfig.getDocNumPerImport() + "] ActuallyNum:[" + handleNum + "].");
            handleRespData(segResultStr, status, start, end);

            //根据偏移量 设置 文档状态
            //20170428 入库时间 by jiangxingqi
            setStatusByoffset(taskDatas,docIds,importSolrTimes,status,allStatus, dbCollection, offset);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Job:[ImportDataJob] --+-importData2SolrDerect by batch, handle the :[" + start + " TO " + end + "] doc to solr exception! batchNum:[" + serverConfig.getDocNumPerImport() + "] ErrorMsg:[" + e.getMessage() + "].", e);
            throw new RuntimeException("importData2SolrDerect by batch, handle the :[" + start + " TO " + end + "] doc to solr exception:[" + e.getMessage() + "].", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Job:[ImportDataJob] --+-importData2SolrDerect by batch, handle the :[" + start + " TO " + end + "] doc to solr complete! batchNum:[" + serverConfig.getDocNumPerImport() + "] spend:[" + (endTime - startTime) + "] ms.");
        }
    }

    private String sendBatchDoc(final JSONArray reqDatas, int[] status) throws JSONException {
        //发送solr入库请求
        String url=this.serverConfig.getDataImportUrl(this.cacheName);
        String segResult = HttpUtils.executePost(reqDatas.toString(), "utf8",url , 3000 * 1000, HttpUtils.CONTENT_TYPE_TEXT_XML);
        return segResult;
    }

    private void handleRespData(String respData, int[] errorDocs, int start, int end) throws JSONException {
        logger.info("Job:[ImportDataJob] --+-handleRespData for the :[" + start + " TO " + end + "] doc...");
        JSONObject jobj;
        jobj = new JSONObject(respData);

        // 提取统计信息
        /*if (!jobj.isNull("timeStatisticInfo")) {
            JSONObject timeStatisticInfos = jobj.getJSONObject("timeStatisticInfo");
            timeStatisticInfos.put("HttpInvkTimeAtCache", (endTime - startTime));
            TimeStatistic.statisticTime(timeStatisticInfos);
        }*/

        if (jobj.isNull("error") && !jobj.isNull("result") && jobj.get("result") instanceof Boolean
                && ((Boolean) jobj.get("result"))) {
            // 向solr中提交数据成功
            logger.info("Job:[ImportDataJob] --+-handleRespData for the :[" + start + " TO " + end + "] doc --+- import all batch doc success!");

            // 获取提交失败的所有的文档的id
            JSONArray errorDocIds = jobj.getJSONArray("errorCacheIds");

            if (null != errorDocIds && errorDocIds.length() > 0) {
                for (int i = 0; i < errorDocIds.length(); i++) {
                    errorDocs[errorDocIds.getInt(i)] = CommonData.IMPORTSTATUS_IMPORT_FAILED;
                    logger.error("Job:[ImportDataJob] --+-handleRespData for the :[" + start + " TO " + end + "] doc --+- the:[" + (start + errorDocIds.getInt(i)) + "] doc import failed!");
                }
            }

            //获取需要处理的 补充文章
            //handleNedSupplyDocs(jobj);
        } else {
            //提交数据失败 php中处理可能有异常
            logger.info("Job:[ImportDataJob] --+-handleRespData for the :[" + start + " TO " + end + "] doc --+- import batch doc has error! ErrorMsg:[" + jobj.get("error") + "].");
            // 获取提交失败的所有的文档的id
            JSONArray errorDocIds = jobj.getJSONArray("errorCacheIds");
            if (null != errorDocIds && errorDocIds.length() > 0) {
                for (int i = 0; i < errorDocIds.length(); i++) {
                    errorDocs[errorDocIds.getInt(i)] = CommonData.IMPORTSTATUS_IMPORT_FAILED;
                    logger.error("Job:[ImportDataJob] --+-handleRespData for the :[" + start + " TO " + end + "] doc --+- the:[" + (start + errorDocIds.getInt(i)) + "] doc import failed!");
                }
            }

            //获取需要处理的 补充文章
            //handleNedSupplyDocs(jobj);
        }
    }

    private void handleNedSupplyDocs(final JSONObject result) throws JSONException {
        if (ValidateUtils.isNullOrEmpt(result, "nedSupplyDocs")) {
            return;
        }

        JSONObject nedSupplyInfo = result.getJSONObject("nedSupplyDocs");
        if (!ValidateUtils.isNullOrEmpt(nedSupplyInfo, "docs")) {
            JSONArray newDocs = nedSupplyInfo.getJSONArray("docs");
            MongoUtils.addData(newDocs, dbSupplyCollection, new IMongoDocConverter() {
                @Override
                public Document converBean2Doc(Object commonData) {
                    JSONObject jsonObject = (JSONObject) commonData;
                    Document mogoDbBean = new Document();
                    mogoDbBean.put("doc", jsonObject.toString());
                    mogoDbBean.put("supplyStatsu", ImportDataConfig.SUPPLY_STATUS_NOT);
                    return mogoDbBean;
                }
            });
        }
    }

    /**
     *
     * @param docs
     * @param docIds
     * @param importSolrTimes 获取入库时间
     * @param status
     * @param allStatus
     * @param dbCollection
     * @param offset
     */
    private void setStatusByoffset(final Document[] docs,final Object[] docIds,final long[] importSolrTimes, int[] status, int[] allStatus, final MongoCollection dbCollection, int offset) {
        Document doc = null;
        for (int t = 0; t < docs.length; t++) {
            doc = docs[t];
            if (ValidateUtils.isNullOrEmpt(doc)) {
                break;
            }
            //doc.put("importStatus", status[t]);
            docIds[offset + t] = doc.get(MongoUtils.PRIM_KEY_ID);

            if(doc.get("importSolrTime")!=null){
                long importSolrTime=(long) doc.get("importSolrTime");
                importSolrTimes[offset + t] = importSolrTime;
            }
            allStatus[offset + t] = status[t];
        }
    }

    /**
     *
     * @param docIds
     * @param importSolrTimes  入库时间 by jiangxingqi
     * @param allStatus
     * @param dbCollection
     * @throws JSONException
     */
    private void handleFlushAndUpadateStatus(Object[] docIds, long[] importSolrTimes, int[] allStatus, MongoCollection dbCollection) throws JSONException {
        //1.flush solr to desk
        flushSolrData();

        //2.update all document status
        updateStatusById(docIds,importSolrTimes,allStatus,dbCollection);
    }


    private void handleNedSupplyDocs() throws JSONException {
        //
        String url = this.serverConfig.getSupplyIdUrl(this.cacheName);
        int supplyNum = sendNedSupplyDoc(url);

        if (supplyNum > 0) {
            //提交更新
            flushSolrData();
        }
    }

    private int sendNedSupplyDoc(String url) throws JSONException {
        Document supplyInfo = null;
        JSONArray batchSupplyDocs = new JSONArray();

        Bson fileter1 = Filters.eq("supplyStatsu", ImportDataConfig.SUPPLY_STATUS_NOT); //没有supply的
        Bson conds = Filters.and(fileter1);
        //查询出来所有的文章
        FindIterable iterable = dbSupplyCollection.find(conds);

        Iterator<Document> iterator = iterable.iterator();
        String temp = null;
        JSONObject supplyDoc = null;
        int batchNum = 0;
        Object[] ids = new Object[200];

        int hanleNum = 0;
        while (iterator.hasNext()) {
            supplyInfo = iterator.next();
            temp = (String) supplyInfo.get("doc");
            supplyDoc = new JSONObject(temp);
            batchSupplyDocs.put(supplyDoc);
            ids[batchNum] = supplyInfo.get("_id");

            batchNum++;
            hanleNum++;

            if (batchNum >= 200) {
                hanleBatchSupplyDoc(batchSupplyDocs, ids, url);

                //清空当前批量缓存
                batchSupplyDocs = new JSONArray();
                Arrays.fill(ids, null);
                batchNum = 0;
            }
        }

        //处理 不满 batchNum 的文档
        if (batchSupplyDocs.length() > 0) {
            hanleBatchSupplyDoc(batchSupplyDocs, ids, url);
        }

        logger.debug("=====================================sendNedSupplyDoc=============================================");
        logger.info("Job:[ImportDataJob] --+-sendNedSupplyDoc complete. hanleNum:[" + hanleNum + "].");
        logger.debug("=====================================sendNedSupplyDoc=============================================");
        return hanleNum;
    }

    private void hanleBatchSupplyDoc(final JSONArray batchSupplyDocs, final Object[] ids, final String url) throws JSONException {
        //发送solr入库请求
        JSONObject reqData = new JSONObject();
        reqData.put("isCommit", false);
        reqData.put("ispartialdata", true);
        reqData.put("docs", batchSupplyDocs);
        String segResult = HttpUtils.executePost(reqData.toString(), "utf8", url, 300 * 1000, HttpUtils.CONTENT_TYPE_TEXT_XML);
        JSONObject jobj = new JSONObject(segResult);
        if (jobj.isNull("error") && !jobj.isNull("result") && jobj.get("result") instanceof Boolean
                && ((Boolean) jobj.get("result"))) {
            // 向solr中提交数据成功
            logger.info("Job:[ImportDataJob] --+-handleNedSupplyDocs success!");

            Boolean[] errorDocs = new Boolean[batchSupplyDocs.length()];
            Arrays.fill(errorDocs, true);
            //处理错误的文档
            if (!ValidateUtils.isNullOrEmpt(jobj, "ErrorDocs")) {
                JSONArray errorInfos = jobj.getJSONArray("ErrorDocs");
                int idx = -1;
                String errorMsg = null;
                String docStr = null;
                Document document = null;
                for (int i = 0; i < errorInfos.length(); i++) {
                    errorMsg = ((JSONObject) errorInfos.get(i)).getString("errorMsg");
                    docStr = ((JSONObject) errorInfos.get(i)).getJSONObject("doc").toString();
                    logger.error("Job:[ImportDataJob] --+-handleNedSupplyDocs--+-hanleSupplyErrorDoc--+-doc supply error! " +
                            "ErrorMsg:[" + errorMsg + "] doc:[" + docStr + "].");

                    //将补充失败的文章进行跟新
                    //获取文章的下标
                    idx = ((JSONObject) errorInfos.get(i)).getInt("id");
                    if (idx < 0) {
                        throw new RuntimeException("hanleSupplyErrorDoc excption:[DocIdx from php is invalidate]. Idx:[" + idx + "].");
                    }

                    //updata status to db
                    document = new Document();
                    document.put(MongoUtils.PRIM_KEY_ID, ids[idx]);
                    document.put("supplyStatsu", ImportDataConfig.SUPPLY_STATUS_FAILED);
                    document.put("ErrorMsg", errorMsg);
                    MongoUtils.updateById(dbSupplyCollection, document);

                    errorDocs[idx] = false;
                }
            }

            //将补充成功的文档进行删除/修改状态
            Document document = null;
            for (int i = 0; i < errorDocs.length; i++) {
                if (!errorDocs[i]) {
                    //错误的文档已经修改过状态了，跳过
                    continue;
                }

                document = new Document();
                document.put(MongoUtils.PRIM_KEY_ID, ids[i]);
                document.put("supplyStatsu", ImportDataConfig.SUPPLY_STATUS_SUCCESS);
                MongoUtils.updateById(dbSupplyCollection, document);
            }
        } else {
            //提交数据失败 php中处理可能有异常
            logger.info("Job:[ImportDataJob] --+-handleNedSupplyDocs error! ErrorMsg:[" + jobj.get("error") + "].");
            throw new RuntimeException("handleNedSupplyDocs error! ErrorMsg:[" + jobj.get("error") + "].");
        }
    }


    private void flushSolrData() throws JSONException {
        String flushDeskURL = this.serverConfig.getFlushURL(this.cacheName);
        String segResult = HttpUtils.executeGet("utf8", flushDeskURL, 300 * 1000);
        if (null == segResult || segResult.length() == 0) {
            throw new RuntimeException(
                    "flushSolrData excption! commit solr for update error,response data is null!");
        }
        JSONObject jobj = new JSONObject(segResult);
        if (jobj.isNull("error") && !jobj.isNull("result") && jobj.get("result") instanceof Boolean
                && ((Boolean) jobj.get("result"))) {
            logger.info("Job:[ImportDataJob] --+- ###### flush solr data to desk seccess! ###### ");
        } else {
            logger.error("Job:[ImportDataJob] --+- ###### flush solr data to desk failed! ###### ,ErrorMsg:[" + jobj.toString() + "].");
            throw new RuntimeException("flush solr data to desk failed,ErrorMsg:[" + jobj.toString() + "].");
        }
    }

    /**
     *
     * @param docIds
     * @param importSolrTimes 20170428 入库时间 by jiangxingqi
     * @param allStatus
     * @param dbCollection
     */
    private void updateStatusById(Object[] docIds, long[] importSolrTimes,int[] allStatus,MongoCollection dbCollection) {
        Object id;
        for (int t = 0; t < docIds.length; t++) {
            id = docIds[t];
            if (ValidateUtils.isNullOrEmpt(id)) {
                break;
            }
            long importSolrTime=importSolrTimes[t];
            long updateSolrTime=new Date().getTime();
            if(importSolrTime==0) {
                importSolrTime = updateSolrTime;
                MongoUtils.updateStatusAndCreateTimeById(dbCollection, id, allStatus[t],importSolrTime,updateSolrTime);
            }else{
                MongoUtils.updateStatusAndUpdateTimeById(dbCollection, id, allStatus[t],updateSolrTime);
            }
        }
    }

    /**
     * 重置参数列表
     * @param docIds
     * @param importSolrTimes
     * @param allStatus
     */
    private void resetParam(Object[] docIds, long[] importSolrTimes,int[] allStatus){
        Arrays.fill(docIds, null);
        Arrays.fill(importSolrTimes, 0);//入库时间重置 by jiangxingqi 20170503
        Arrays.fill(allStatus, CommonData.IMPORTSTATUS_IMPORT_SUCCESS);
    }
    private static class BathStatistic {
        int flushSequence = 0;
        int batchSequence = 0;
    }

}
