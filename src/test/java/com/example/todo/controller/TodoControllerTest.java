package com.example.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.TodoItem;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoItem todo1;
    private TodoItem todo2;

    @BeforeEach
    void setUp() {
        todo1 = new TodoItem("Buy groceries", "Milk, Eggs, Bread", false, LocalDate.now().plusDays(1));
        todo1.setId(1L);
        todo2 = new TodoItem("Complete coding homework", "Finish Maven project", true, LocalDate.now().plusDays(2));
        todo2.setId(2L);
    }

    @Test
    void getAllTodos_shouldReturnTodosList() throws Exception {
        when(todoService.getAllTodos()).thenReturn(Arrays.asList(todo1, todo2));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Buy groceries")))
                .andExpect(jsonPath("$[1].title", is("Complete coding homework")));

        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    void getTodoById_whenFound_shouldReturnTodo() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(todo1);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Buy groceries")));

        verify(todoService, times(1)).getTodoById(1L);
    }

    @Test
    void getTodoById_whenNotFound_shouldReturn404() throws Exception {
        when(todoService.getTodoById(3L)).thenThrow(new ResourceNotFoundException("Todo not found"));

        mockMvc.perform(get("/api/todos/3"))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).getTodoById(3L);
    }

    @Test
    void createTodo_withValidBody_shouldReturnCreatedTodo() throws Exception {
        TodoItem newTodo = new TodoItem("Learn Docker", "Run a Docker container", false, LocalDate.now().plusDays(3));
        TodoItem savedTodo = new TodoItem("Learn Docker", "Run a Docker container", false, LocalDate.now().plusDays(3));
        savedTodo.setId(3L);

        when(todoService.createTodo(any(TodoItem.class))).thenReturn(savedTodo);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Learn Docker")));

        verify(todoService, times(1)).createTodo(any(TodoItem.class));
    }

    @Test
    void createTodo_withInvalidBody_shouldReturn400() throws Exception {
        TodoItem invalidTodo = new TodoItem("", "No Title", false, LocalDate.now()); // Blank title violates validation

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any(TodoItem.class));
    }

    @Test
    void updateTodo_shouldReturnUpdatedTodo() throws Exception {
        TodoItem updateDetails = new TodoItem("Buy groceries updated", "Include orange juice", true, LocalDate.now().plusDays(1));
        TodoItem updatedTodo = new TodoItem("Buy groceries updated", "Include orange juice", true, LocalDate.now().plusDays(1));
        updatedTodo.setId(1L);

        when(todoService.updateTodo(eq(1L), any(TodoItem.class))).thenReturn(updatedTodo);

        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Buy groceries updated")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(todoService, times(1)).updateTodo(eq(1L), any(TodoItem.class));
    }

    @Test
    void deleteTodo_shouldReturn24NoContent() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo(1L);
    }
}
