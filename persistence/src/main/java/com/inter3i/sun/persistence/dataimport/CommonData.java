/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2016/12/12
 * Description:
 *
 */

package com.inter3i.sun.persistence.dataimport;

import com.inter3i.sun.persistence.Entity;
import lombok.Data;

import java.util.Map;

@Data
public class CommonData extends Entity {
    public static final int IMPORTSTATUS_NO_IMPORT = 0;
    public static final int IMPORTSTATUS_IMPORT_SUCCESS = 1;
    public static final int IMPORTSTATUS_IMPORT_FAILED = 2;
    public static final int IMPORTSTATUS_IMPORT_EXCEPTION = 3;

    public static final int SEGMENTE_SATUS_NO = 0;
    public static final int SEGMENTE_SATUS_SUCCESS = 1;
    public static final int SEGMENTE_SATUS_FAILED = 2;
    public static final int SEGMENTE_SATUS_EXCEPTION = 3;
    public static final int SEGMENTE_SATUS_OUT_SIZE = 4;

    private String jsonDocStr;

    private Map jsonDoc;

    private Map dataSmryInfo;

    private int importStatus;

    //是否已经分词
    private int segmentedStatus;

    private long cacheDataTime;

    private String segmentedEmg;
    private String importEmg;

   /* private int question_id;
    private int answer_id;
    private int answer_father_id;
    private int question_father_id;
    private int child_post_id;
    private int trample_count;
    private int floor;
    private int father_floor;
    private String quote_father_mid;
    private int reply_father_floor;
    private String reply_father_mid;
    private int paragraphid;
    private int read_count;
    private int recommended;
    private int toped;
    private String page_url;
    private String original_url;
    private String column;
    private String column1;
    private String post_title;
    private String id;
    private long created_at;
    private int created_year;
    private int created_month;
    private int created_day;
    private int created_hour;
    private int created_weekday;
    private String text;
    private String pg_text;
    private String originalText;
    private String combinWord;
    private String emoCombin;
    private String account;
    private String emoAccount;
    private String similar;
    private String source;
    private String favorited;
    private String truncated;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;

    private String[] thumbnail_pic;
    private String[] bmiddle_pic;
    private String[] original_pic;

    private String userid;
    private String userguid;
    private String retweeted_status;
    private String timeline_type;
    private String geo_type;
    private String geo_coordinates_x;
    private String geo_coordinates_y;
    private String annotations;
    private String screen_name;
    private int reposts_count;
    private int content_type;

    private String[] province_code;
    private String[] city_code;
    private String[] country_code;
    private String[] district_code;
    private String[] province;
    private String[] city;
    private String[] country;
    private String[] district;
    private String[] emoProvince;
    private String[] emoCity;
    private String[] emoCountry;
    private String[] emoDistrict;
    private String[] business;
    private String[] emoBusiness;

    private int comments_count;
    private int total_reposts_count;
    private int direct_reposts_count;
    private String[] NRN;
    private String[] emoNRN;
    private String sourceid;
    private String source_host;
    private String[] url;
    private String[] emoTopic;
    private String[] emoTopicKeyword;
    private String[] emoTopicCombinWord;
    private String[] organization;
    private String[] emoOrganization;
    private int register_time;
    private int verify;
    private String sex;
    private String[] emotion;
    private int reply_comment;
    private String statusid;
    private int analysis_status;
    private String guid;
    private String docguid;
    private String retweeted_guid;
    private String[] ancestor_text;
    private String[] ancestor_NRN;
    private String[] ancestor_emoNRN;
    private String[] ancestor_district;
    private String[] ancestor_emoDistrict;
    private String[] ancestor_city;
    private String[] ancestor_emoCity;
    private String[] ancestor_province;
    private String[] ancestor_emoProvince;
    private String[] ancestor_country;
    private String[] ancestor_emoCountry;
    private String[] ancestor_business;
    private String[] ancestor_emoBusiness;
    private String[] ancestor_combinWord;
    private String[] ancestor_emoCombin;
    private String[] ancestor_organization;
    private String[] ancestor_emoOrganization;
    private String[] ancestor_emotion;
    private String[] ancestor_wb_topic;
    private String[] ancestor_emoTopic;
    private String[] ancestor_wb_topic_keyword;
    private String[] ancestor_emoTopicKeyword;
    private String[] ancestor_wb_topic_combinWord;
    private String[] ancestor_emoTopicCombinWord;
    private String[] ancestor_url;
    private String[] ancestor_host_domain;
    private String[] ancestor_account;
    private String[] ancestor_emoAccount;
    private String[] ancestor_similar;


    private int followers_count;
    private int friends_count;
    private int repost_trend_cursor;
    private int total_reach_count;
    private String father_id;
    private String father_guid;
    private int direct_comments_count;
    private int praises_count;
    private int has_picture;
    private String[] host_domain;
    private String[] verified_reason;
    private String[] verified_type;
    private int level;
    private String[] description;
    private String mid;
    private String retweeted_mid;
    private String retweeted_userid;
    private String retweeted_description;
    private String retweeted_verified_reason;
    private String status_mid;
    private String[] wb_topic;
    private String[] wb_topic_keyword;
    private String[] wb_topic_combinWord;
    private String retweeted_text;
    private String retweeted_created_at;
    private String retweeted_verify;
    private String retweeted_screen_name;
    private String retweeted_source;
    private String retweeted_comments_count;
    private String retweeted_reposts_count;
    private String retweeted_followers_count;

    //特征分类
    private String feature_pclass;
    private String feature_class;
    private String feature_keyword;
    private String[] feature_field;
    private String feature_father_guid;

    private String productType;
    private String[] impress;
    private String[] commentTags;
    private double satisfaction;
    private double godRepPer;
    private double midRepPer;
    private double wosRepPer;
    private int godRepNum;
    private int midRepNum;
    private int wosRepNum;
    private int apdRepNum;
    private int showPicNum;
    private int cmtStarLevel;
    private int purchDate;

    private int isNewPro;
    private String proClassify;
    private String proPic[];
    private int proOriPrice;
    private int proCurPrice;
    private int proPriPrice;*/

                /*<field name="question_id" type="int" indexed="true" stored="true" /> <!--问题序列号-->

				<!--电商新增字段第二版:SKU(商品信息)-->
				<field name="" type="int" indexed="true" stored="true" /> <!--是否为新品-->
				<field name="" type="string" indexed="true" stored="true" /> <!--详细商品分类-->
				<field name="" type="string" indexed="true" stored="true" multiValued="true" /> <!--产品图片[]-->
				<field name="" type="int" indexed="true" stored="true" /> <!--原价-->
				<field name="" type="int" indexed="true" stored="true" /> <!--现价-->
				<field name="" type="int" indexed="true" stored="true" /> <!--促销价-->
				<field name="promotionInfos" type="string" indexed="true" stored="true" multiValued="true" /> <!--促销信息-->
				<field name="productFullName" type="string" indexed="true" stored="true" /> <!--商品全名-->
				<field name="productColor" type="string" indexed="true" stored="true" /> <!--商品颜色-->
				<field name="productSize" type="string" indexed="true" stored="true" /> <!--产品尺寸-->
				<field name="productDesc" type="text_smart" indexed="true" stored="true" multiValued="true" termVectors="true" termPositions="true" termOffsets="true"/> <!--产品描述-->
				<field name="productComb" type="string" indexed="true" stored="true" multiValued="true" /> <!--产品套餐/组合-->
				<field name="detailParam" type="string" indexed="true" stored="true" multiValued="true" /> <!--详细参数-->
				<field name="stockNum" type="int" indexed="true" stored="true" /> <!--库存-->
				<field name="salesNumMonth" type="int" indexed="true" stored="true" /> <!--月销售量-->
				<field name="compName" type="string" indexed="true" stored="true" /> <!--公司名称-->
				<field name="compAddress" type="string" indexed="true" stored="true" /> <!--公司地址-->
				<field name="phoneNum" type="string" indexed="true" stored="true" /> <!--公司电话-->
				<field name="operateTime" type="int" indexed="true" stored="true" /> <!--开店时长-->
				<field name="compURL" type="string" indexed="true" stored="true" /> <!--公司URL-->
				<field name="serviceProm" type="string" indexed="true" stored="true" multiValued="true" /> <!--服务承诺-->
				<field name="logisticsInfo" type="string" indexed="true" stored="true" multiValued="true" /> <!--物流-->
				<field name="payMethod" type="string" indexed="true" stored="true" multiValued="true" /> <!--支付方式-->
				<field name="compDesMatch" type="tdouble" indexed="true" stored="true" /> <!--与描述相符 对店的总体打分-->
				<field name="logisticsScore" type="tdouble" indexed="true" stored="true" /> <!--对店的物流打分-->
				<field name="serviceScore" type="tdouble" indexed="true" stored="true" /> <!--对店的服务打分-->


				<!--电商新增字段第二版:SKU(评论信息)-->
				<field name="serviceComment" type="text_smart" indexed="true" stored="true" multiValued="true" termVectors="true" termPositions="true" termOffsets="true"/> <!--对服务的评论-->
				<field name="apdComment" type="text_smart" indexed="true" stored="true" multiValued="true" termVectors="true" termPositions="true" termOffsets="true"/> <!--追评-->
				<field name="isFavorite" type="int" indexed="true" stored="true" /> <!--是否收藏-->
				<field name="isAttention" type="int" indexed="true" stored="true" /> <!--是否关注-->

				<!--电商数据增加的字段 end -->


				<!--简历新增字段-->
				<field name="dataClsfct" type="string" indexed="true" stored="true" /> <!--用于区分数据来源，例如电商数据可以通过抓取，也可以通过后台接口获取-->
				<field name="usersAge" type="int" indexed="true" stored="true" /> <!--年龄-->
				<field name="workTimeLong" type="tdouble" indexed="true" stored="true" /> <!--工作时长-->
				<field name="worksNum" type="int" indexed="true" stored="true" /> <!--换了几份工作-->
				<field name="eduBackGro" type="string" indexed="true" stored="true" /> <!--最高学历-->
				<field name="userPhoneNum" type="string" indexed="true" stored="true" /> <!--手机号码-->
				<field name="nowLocation" type="string" indexed="true" stored="true" /> <!--现居住的城市-->
				<field name="professionName" type="string" indexed="true" stored="true" /> <!--现在的行业与职能-->
				<field name="position" type="string" indexed="true" stored="true" /> <!--职位名称-->
				<field name="email" type="string" indexed="true" stored="true" /> <!--email地址-->
				<field name="isMarried" type="int" indexed="true" stored="true" /> <!--是否结婚-->
				<field name="nationality" type="string" indexed="true" stored="true" /> <!--国籍-->
				<field name="credentials" type="string" indexed="true" stored="true" multiValued="true" /> <!--证件信息-->
				<field name="politicalStatus" type="string" indexed="true" stored="true" /> <!--政治面貌-->
				<field name="foreignExper" type="int" indexed="true" stored="true" /> <!--是否有海外工作/学习经历-->
				<field name="workStatus" type="string" indexed="true" stored="true" /> <!--求职状态-->
				<field name="curPay" type="int" indexed="true" stored="true" /> <!--目前薪资-->
				<field name="homePhoneNum" type="string" indexed="true" stored="true" /> <!--家庭电话-->
				<field name="height" type="int" indexed="true" stored="true" /> <!--身高(CM)-->
				<field name="zipCode" type="string" indexed="true" stored="true" /> <!--邮编-->
				<field name="weiXinNum" type="string" indexed="true" stored="true" /> <!--微信号-->
				<field name="qqNum" type="string" indexed="true" stored="true" /> <!--QQ信号-->
				<field name="homeLocation" type="string" indexed="true" stored="true" /> <!--家庭住址-->
				<field name="expectWorkLct" type="string" indexed="true" stored="true" /> <!--期望工作地区-->
				<field name="expectYearPay" type="int" indexed="true" stored="true" /> <!--期望年薪-->
				<field name="expectPay" type="int" indexed="true" stored="true" /> <!--期望月薪-->
				<field name="curYearPay" type="int" indexed="true" stored="true" /> <!--目前年薪-->
				<field name="expWorkQuality" type="string" indexed="true" stored="true" /> <!--期望工作性质-->
				<field name="expectProfession" type="string" indexed="true" stored="true" /> <!--期望从事职业-->
				<field name="expectTrades" type="string" indexed="true" stored="true" /> <!--期望从事行业-->
				<field name="expCmpQuality" type="string" indexed="true" stored="true" /> <!--期望单位性质-->


				<field name="isMpRcm" type="int" indexed="true" stored="true" /> <!--是否统招-->
				<field name="compQuality" type="string" indexed="true" stored="true" /> <!--企业性质-->
				<field name="compScale" type="int" indexed="true" stored="true" /> <!--企业规模-->
				<field name="department" type="string" indexed="true" stored="true" /> <!--部门-->
				<field name="leader" type="string" indexed="true" stored="true" /> <!--汇报对象-->
				<field name="underlingNum" type="int" indexed="true" stored="true" /> <!--下属人数-->
				<field name="professionType" type="string" indexed="true" stored="true" /> <!--职能类别-->
				<field name="certifier" type="string" indexed="true" stored="true" /> <!--证明人-->

				<field name="techName" type="string" indexed="true" stored="true" /> <!--外语语种/技能名称-->
				<field name="RRAbility" type="string" indexed="true" stored="true" /> <!--读写能力-->
				<field name="HSAbility" type="string" indexed="true" stored="true" /> <!--听说能力-->
				<field name="proficiency" type="string" indexed="true" stored="true" /> <!--掌握程度-->

				<field name="certificate" type="string" indexed="true" stored="true" /> <!--证书名称-->
				<field name="certOrg" type="string" indexed="true" stored="true" /> <!--证书机构-->
				<field name="projName" type="string" indexed="true" stored="true" /> <!--项目名称-->

				<field name="curPayMax" type="int" indexed="true" stored="true" /> <!--最大薪资待遇-->
				<field name="nedNum" type="int" indexed="true" stored="true" /> <!--招聘人数-->
				<field name="workTimeLongMax" type="tdouble" indexed="true" stored="true" /> <!--工作年限(max)-->

				<field name="area" type="string" indexed="true" stored="true" /> <!--区域-->
				<!--简历新增字段 end -->


				<!--口碑网新增字段 -->
				<field name="apperScore" type="tdouble" indexed="true" stored="true" /> <!-- 外观评分 -->
				<field name="decorateScore" type="tdouble" indexed="true" stored="true" /> <!-- 内饰评分 -->
				<field name="perfPriScale" type="tdouble" indexed="true" stored="true" /> <!-- 性价比评分 -->
				<!--口碑网新增字段 end -->


				<!-- 旅游新增字段 -->
				<field name="attachNum" type="string" indexed="true" stored="true" /> <!-- 附件编号 -->
				<field name="startTimeStr" type="string" indexed="true" stored="true" /> <!-- 开始时间字符串 -->
				<field name="endTimeStr" type="string" indexed="true" stored="true" /> <!-- 结束时间字符串 -->
				<field name="attachName" type="string" indexed="true" stored="true" /> <!-- 附件名称 -->
				<!-- 旅游新增字段 -->


				<!-- 电商论坛新增字段 -->
				<field name="integral" type="int" indexed="true" stored="true" /> <!--用户的积分数-->
				<field name="subj_article_count" type="int" indexed="true" stored="true" /> <!--主题帖子数-->
				<field name="article_count" type="int" indexed="true" stored="true" /> <!--帖子总数-->
				<field name="gold_count" type="int" indexed="true" stored="true" /> <!--金币数(元)-->
				<field name="favour_count" type="int" indexed="true" stored="true" /> <!--当前文章的点赞/收藏/支持数-->
				<field name="signin_days" type="int" indexed="true" stored="true" /> <!--当前用户签到天数-->
				<field name="conti_signin_days" type="int" indexed="true" stored="true" /> <!--当前用户连续签到天数-->
				<field name="integral_beans" type="int" indexed="true" stored="true" /> <!--智豆/京豆/钻石等-->
				<!-- 电商论坛新增字段 -->

				<!-- 电商新增字段 2.0 -->
				<field name="trading_count_m" type="int" indexed="true" stored="true" /> <!--月成交量-->
				<!-- 电商新增字段 2.0 end -->

				<!-- 通用字段 2016-11-07 -->
				<field name="save_time" type="long" indexed="true" stored="true" /> <!-- 数据入库时候的时间戳 -->
				<!-- 通用字段 2016-11-07 end -->

				<!-- 微博分析 传播轨迹 增加父id -->
					<field name="pid" type="int" indexed="true" stored="true" /> <!-- 微博的父id -->
				<!-- 微博分析 传播轨迹 增加父id -->

				<!--50个用户表字段-->
				<field name="users_id" type="string" indexed="true" stored="true" /> <!--用户UID, 需删除,由于多个来源是无法唯一标识一条记录-->
				<field name="users_screen_name" type="string" indexed="true" stored="true" termVectors="true" /> <!--微博昵称-->
				<field name="users_name" type="string" indexed="true" stored="true" /> <!--友好显示名称-->
				<field name="users_province" type="string" indexed="true" stored="true" /> <!--省份编码--> <!--user表中只存了前两位-->
				<field name="users_city" type="string" indexed="true" stored="true" /> <!--城市编码-->
				<field name="users_location" type="string" indexed="false" stored="true" /> <!--地址-->
				<field name="users_description" type="text_smart" indexed="true" stored="true" multiValued="true" termVectors="true" termPositions="true" termOffsets="true"/> <!--个人描述-->
				<field name="users_url" type="string" indexed="true" stored="true" /> <!--用户博客地址-->
				<field name="users_profile_image_url" type="string" indexed="false" stored="true" /> <!--自定义图像-->
				<field name="users_avatar_large" type="string" indexed="false" stored="true" /> <!--用户大头像-->
				<field name="users_domain" type="string" indexed="true" stored="true" /> <!--用户个性化URL-->
				<field name="users_gender" type="string" indexed="true" stored="true" /> <!--性别-->
				<field name="users_followers_count" type="int" indexed="true" stored="true" /> <!--粉丝数-->
				<field name="users_friends_count" type="int" indexed="true" stored="true" /> <!--关注数-->
				<field name="users_statuses_count" type="int" indexed="true" stored="true" /> <!--微博数-->
				<field name="users_favourites_count" type="int" indexed="true" stored="true" /> <!--收藏-->
				<field name="users_replys_count" type="int" indexed="true" stored="true" /> <!--回复数-->
				<field name="users_recommended_count" type="int" indexed="true" stored="true" /> <!--精华帖数-->
				<field name="users_level" type="int" indexed="true" stored="true"/> <!--用户级别-->
				<field name="users_bi_followers_count" type="int" indexed="true" stored="true" /> <!--互粉数-->
				<field name="users_created_at" type="int" indexed="true" stored="true" /> <!--创建时间-->
				<field name="users_allow_all_act_msg" type="string" indexed="true" stored="true" /> <!--是否允许所有人给我发私信-->
				<field name="users_allow_all_comment" type="string" indexed="true" stored="true" /> <!--是否允许所有人对我的微博进行评论-->
				<field name="users_geo_enabled" type="string" indexed="true" stored="true" /> <!--是否允许带有地理信息-->
				<field name="users_verified" type="int" indexed="true" stored="true" /> <!--加V标示，是否微博认证用户-->
				<field name="users_taginfo" type="string" indexed="true" stored="true" multiValued="true" /> <!--标签信息-->
				<field name="users_trendinfo" type="string" indexed="true" stored="true" multiValued="true" /> <!--话题信息-->
				<!--<field name="users_friend_cursor" type="int" indexed="false" stored="true" /> 下次获取关注人的初始位置-->
				<field name="users_user_updatetime" type="int" indexed="true" stored="true" /> <!--用户更新时间-->
				<!--<field name="users_weibo_updatetime" type="int" indexed="false" stored="true" /> 微博更新时间-->
				<!--<field name="users_trend_updatetime" type="int" indexed="false" stored="true" /> 话题更新时间-->
				<!--<field name="users_tag_updatetime" type="int" indexed="false" stored="true" /> 标签更新时间-->
				<!--<field name="users_follower_updatetime" type="int" indexed="false" stored="true" /> 粉丝更新时间-->
				<!--<field name="users_friend_updatetime" type="int" indexed="false" stored="true" /> 关注人更新时间-->
				<field name="users_is_celebrity_friend" type="int" indexed="true" stored="true" /> <!--是否是名人的关注人：1，是；0：不是-->
				<field name="users_is_celebrity_follower" type="int" indexed="true" stored="true" /> <!--是否是名人的粉丝：1，是；0：不是-->
				<field name="users_is_bridge_user" type="int" indexed="true" stored="true" /> <!--是否是桥接用户-->
				<field name="users_bridge_count" type="int" indexed="true" stored="true" /> <!--桥接微博的次数-->
				<field name="users_country_code" type="string" indexed="true" stored="true" /> <!--国家编码-->
				<field name="users_province_code" type="string" indexed="true" stored="true" /> <!--省编码-->
				<field name="users_city_code" type="string" indexed="true" stored="true" /> <!--城市编码-->
				<field name="users_district_code" type="string" indexed="true" stored="true" /> <!--区县编码-->
				<!--<field name="users_follower_level" type="int" indexed="true" stored="true" /> 根据粉丝数得到的用户级别-->
				<!--<field name="users_friend_level" type="int" indexed="true" stored="true" /> 根据关注数得到的用户级别-->
				<!--<field name="users_weibo_since_id" type="string" indexed="false" stored="true" /> 需要抓取的最小ID-->
				<!--<field name="users_weibo_max_id" type="string" indexed="false" stored="true" /> 需要抓取的最大ID-->
				<!--<field name="users_weibo_new_id" type="string" indexed="false" stored="true" /> 用户微博的最新ID-->
				<field name="users_get_type" type="string" indexed="false" stored="true" /> <!--获取用户的方式，api-->
				<field name="users_sourceid" type="string" indexed="true" stored="true" />  <!--源ID-->
				<field name="users_source_host" type="string" indexed="true" stored="true"/> <!--源HOST-->
				<field name="users_seeduser" type="int" indexed="true" stored="true" /> <!--是否种子用户，种子用户不能删除-->
				<field name="users_verified_reason" type="text_smart" indexed="true" stored="true" multiValued="true" termVectors="true" termPositions="true" termOffsets="true" /> <!--认证原因-->
				<field name="users_verified_type" type="string" indexed="true" stored="true" multiValued="true" /> <!--认证类型-->
				<!--<field name="users_interrupt_newtime" type="int" indexed="false" stored="true" /> 抓取用户的微博时，记录抓取到的微博最新时间-->
				<field name="users_friends_id" type="string" indexed="true" stored="true"  multiValued="true" /> <!--关注用户ID-->
				<field name="users_page_url" type="string" indexed="true" stored="true" /><!--用户页面链接地址-->*/

}
