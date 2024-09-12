package com.zdouble.domain.task.service;

import com.zdouble.domain.task.model.TaskEntity;
import com.zdouble.domain.task.repository.ITaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class TaskService implements ITaskService {

    @Resource
    private ITaskRepository taskRepository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        return taskRepository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        String topic = taskEntity.getTopic();
        String message = taskEntity.getMessage();
        taskRepository.sendMessage(topic, message);
    }

    @Override
    public void updateTaskStateCompleted(String userId, String messageId) {
        taskRepository.updateTaskStateCompleted(userId, messageId);
    }

    @Override
    public void updateTaskStateFail(String userId, String messageId) {
        taskRepository.updateTaskStateFail(userId, messageId);
    }
}
