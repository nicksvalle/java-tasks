package br.com.nicolasvalle.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ITaskRepository extends JpaRepository<TaskEntity, UUID>{
    List<TaskEntity> findByIdUser(UUID idUser);
}
