package com.example.repositories;

import com.example.entities.Task;
import com.example.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Transactional
    @Modifying
    @Query("update Task t set t.assignee = ?1 where t.id = ?2")
    void updateAssigneeById(User assignee, String id);

    Page<Task> findByAuthor_Id(String id, Pageable pageable);

    Page<Task> findByAssignee_Id(String id, Pageable pageable);
}
