package com.rhm.friend.service.user;

import com.rhm.common.core.domain.PageQueryDTO;
import com.rhm.common.core.domain.TableDataInfo;

public interface IUserMessageService {
    TableDataInfo list(PageQueryDTO dto);
}
