package com.zdouble.domain.task.repository;


import com.zdouble.domain.task.model.TaskEntity;

import java.util.List;

public interface ITaskRepository {
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(String topic, String message);

    void updateTaskStateCompleted(String userId, String messageId);

    void updateTaskStateFail(String userId, String messageId);
}
