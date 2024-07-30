package br.com.nicolasvalle.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nicolasvalle.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;
    
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskEntity taskEntity, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskEntity.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskEntity.getStartAt()) || currentDate.isAfter(taskEntity.getEndAt())){
            return ResponseEntity.status(400).body("A data de inicio / término deve ser maior que a atual");
        }
        if(taskEntity.getStartAt().isAfter(taskEntity.getEndAt())){
            return ResponseEntity.status(400).body("A data de inicio deve ser menor do que a de término");
        }

        var task = this.taskRepository.save(taskEntity);
        return ResponseEntity.status(200).body(task);
    }

    @GetMapping("/")
    public List<TaskEntity> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/")
    public ResponseEntity update(@RequestBody TaskEntity taskEntity, HttpServletRequest request, @PathVariable UUID id){
    
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
       
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(400).body("Usuário não tem permissão para alterar essa tarefa");  
        }
        Utils.copyNonNullProperties(taskEntity, task);

        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(this.taskRepository.save(taskUpdated));
    }
}
