package com.rhm.common.core.constants;

public class CacheConstants {
    public final static String LOGIN_TOKEN_KEY = "loginToken";
    public final static Long EXP = 720L;
    public final static Long REFRESH_TIME = 180L;

    public final static String PHONE_CODE_KEY = "p:c:";  // phone:code:{手机号} | 手机验证码 , 记录手机验证码
    public final static String CODE_TIME_KEY = "c:t:";  // code:time:{手机号}  一天内发送验证码的次数 , 每天发送验证码的次数

    public final static String EXAM_UNFINISHED_LIST = "e:t:l"; // exam:time:list | listId , 未完成竞赛列表
    public final static String EXAM_HISTORY_LIST = "e:h:l"; // exam:history:list | listId , 历史竞赛列表
    public final static String EXAM_DETAIL = "e:d:"; // exam:detail:{examId} | json字符串存储详细信息 , 竞赛详情信息

    public final static String USER_EXAM_LIST = "u:e:l:"; // user:exam:list:{userId} | list , 用户参加的竞赛列表

    public final static String EXAM_QUESTION_LIST = "e:q:l:"; //  竞赛中用于查询上一题和下一题

    public final static Long USER_EXP = 10L;   // 用户详细信息的过期时间
    public final static String USER_DETAIL = "u:d:";  // 用户详情

    public final static String USER_UPLOAD_TIMES_KEY = "u:u:t";  // 用户上传图片的次数
    public final static String QUESTION_LIST = "q:l";  // 总题目列表id，用于查询下一题和上一题
    public final static String QUESTION_HOST_LIST = "q:h:l";  // 热榜题目id


    public static final String USER_MESSAGE_LIST = "u:m:l:";

    public static final String MESSAGE_DETAIL = "m:d:";

    public static final String EXAM_RANK_LIST = "e:r:l:";

    public static final long DEFAULT_START = 0;

    public static final long DEFAULT_END = -1;
}
