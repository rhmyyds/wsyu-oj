package com.rhm.judge.service;

import com.rhm.judge.domain.SandBoxExecuteResult;
import java.util.List;

public interface ISandboxPoolService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);
}