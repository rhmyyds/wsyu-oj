package com.rhm.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.TableDataInfo;

import java.util.List;

public class BaseController {

    public R<Void> toR(int rows) {
        if(rows > 0){
            return R.ok();
        }
        return R.fail();
    }

    public R<Void> toR(boolean result) {
        if(result){
            return R.ok();
        }
        return R.fail();
    }

    public TableDataInfo getTableDataInfo(List<?> list){
        if(CollectionUtil.isEmpty(list)){
            return TableDataInfo.empty();
        }
        long total = new PageInfo(list).getTotal();   // 获取符合查询条件的数据总数
        // questionVOList.size() 的长度就是要求查的第多少页的长度
        return TableDataInfo.success(list,total);
    }
}
