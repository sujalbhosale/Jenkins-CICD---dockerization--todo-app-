package com.example.todo.service;

import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.TodoItem;
import com.example.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoItem> getAllTodos() {
        return todoRepository.findAll();
    }

    public TodoItem getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
    }

    public TodoItem createTodo(TodoItem todoItem) {
        return todoRepository.save(todoItem);
    }

    public TodoItem updateTodo(Long id, TodoItem todoDetails) {
        TodoItem todoItem = getTodoById(id);
        
        todoItem.setTitle(todoDetails.getTitle());
        todoItem.setDescription(todoDetails.getDescription());
        todoItem.setCompleted(todoDetails.isCompleted());
        todoItem.setTargetDate(todoDetails.getTargetDate());
        
        return todoRepository.save(todoItem);
    }

    public void deleteTodo(Long id) {
        TodoItem todoItem = getTodoById(id);
        todoRepository.delete(todoItem);
    }
}
