package com.example.todo.service;

import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.TodoItem;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoItem todo1;
    private TodoItem todo2;

    @BeforeEach
    void setUp() {
        todo1 = new TodoItem("Task 1", "Description 1", false, LocalDate.now().plusDays(2));
        todo1.setId(1L);
        todo2 = new TodoItem("Task 2", "Description 2", true, LocalDate.now().plusDays(5));
        todo2.setId(2L);
    }

    @Test
    void getAllTodos_shouldReturnList() {
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo1, todo2));

        List<TodoItem> result = todoService.getAllTodos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getTodoById_whenFound_shouldReturnTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));

        TodoItem result = todoService.getTodoById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Task 1");
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void getTodoById_whenNotFound_shouldThrowException() {
        when(todoRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(3L));
        verify(todoRepository, times(1)).findById(3L);
    }

    @Test
    void createTodo_shouldSaveAndReturnTodo() {
        when(todoRepository.save(any(TodoItem.class))).thenReturn(todo1);

        TodoItem result = todoService.createTodo(todo1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void updateTodo_whenFound_shouldUpdateAndSave() {
        TodoItem updatedDetails = new TodoItem("Updated Task 1", "Updated Desc 1", true, LocalDate.now().plusDays(3));
        
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(TodoItem.class))).thenReturn(todo1);

        TodoItem result = todoService.updateTodo(1L, updatedDetails);

        assertThat(result.getTitle()).isEqualTo("Updated Task 1");
        assertThat(result.getDescription()).isEqualTo("Updated Desc 1");
        assertThat(result.isCompleted()).isTrue();
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void deleteTodo_whenFound_shouldDelete() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        doNothing().when(todoRepository).delete(todo1);

        todoService.deleteTodo(1L);

        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).delete(todo1);
    }
}
