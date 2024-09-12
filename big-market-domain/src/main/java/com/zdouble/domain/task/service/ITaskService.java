package com.zdouble.domain.task.service;

import com.zdouble.domain.task.model.TaskEntity;

import java.util.List;

public interface ITaskService {
    /**  查询没有发送消息的任务 */
    List<TaskEntity> queryNoSendMessageTaskList();
    /** 发送消息 */
    void sendMessage(TaskEntity taskEntity);
    /** 更新任务状态 -- 完成 */
    void updateTaskStateCompleted(String userId, String messageId);
    /** 更新任务状态 -- 失败 */
    void updateTaskStateFail(String userId, String messageId);
}
