package com.zdouble.infrastructure.persistent.repository;

import com.zdouble.domain.task.model.TaskEntity;
import com.zdouble.domain.task.repository.ITaskRepository;
import com.zdouble.infrastructure.event.EventPublisher;
import com.zdouble.infrastructure.persistent.dao.TaskDao;
import com.zdouble.infrastructure.persistent.po.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class TaskRepository implements ITaskRepository {

    @Resource
    private TaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        List<Task> tasks = taskDao.queryNoSendMessageTaskList();
        return tasks.stream().map(task -> {
            return TaskEntity.builder()
                    .userId(task.getUserId())
                    .topic(task.getTopic())
                    .message(task.getMessage())
                    .messageId(task.getMessageId())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void sendMessage(String topic, String message) {
        eventPublisher.publish(topic, message);
    }

    @Override
    public void updateTaskStateCompleted(String userId, String messageId) {
        Task task = new Task();
        task.setMessageId(messageId);
        task.setUserId(userId);
        taskDao.updateTaskStateCompleted(task);
    }

    @Override
    public void updateTaskStateFail(String userId, String messageId) {
        Task task = new Task();
        task.setMessageId(messageId);
        task.setUserId(userId);
        taskDao.updateTaskStateFail(task);
    }
}

