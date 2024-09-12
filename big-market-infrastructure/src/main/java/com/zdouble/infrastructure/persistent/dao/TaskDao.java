package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.zdouble.domain.award.model.entity.TaskEntity;
import com.zdouble.infrastructure.persistent.po.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskDao {
    void insertTask(Task task);
    List<Task> queryNoSendMessageTaskList();
    @DBRouter
    void updateTaskStateCompleted(Task task);
    @DBRouter
    void updateTaskStateFail(Task task);
}
