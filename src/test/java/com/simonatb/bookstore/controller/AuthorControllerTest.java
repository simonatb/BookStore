package com.simonatb.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonatb.bookstore.config.SecurityConfig;
import com.simonatb.bookstore.contoller.AuthorController;
import com.simonatb.bookstore.dto.author.AuthorCreateDto;
import com.simonatb.bookstore.dto.author.AuthorResponseDto;
import com.simonatb.bookstore.exceptions.AuthorNotFoundException;
import com.simonatb.bookstore.exceptions.GlobalExceptionHandler;
import com.simonatb.bookstore.service.AuthorService;
import com.simonatb.bookstore.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthorController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @WithMockUser
    void getByIdSuccessfully() throws Exception {
        AuthorResponseDto response = new AuthorResponseDto(1L, "Tolkien", "Bio");
        when(authorService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/authors/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Tolkien"));
    }

    @Test
    @WithMockUser
    void getByIdUnsuccessfully() throws Exception {
        when(authorService.getById(1L))
            .thenThrow(new AuthorNotFoundException("Author not found with id: 1"));

        mockMvc.perform(get("/api/authors/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAsUserShouldReturnForbidden() throws Exception {
        AuthorCreateDto dto = new AuthorCreateDto("New", "Bio");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAsAdminSuccessful() throws Exception {
        AuthorCreateDto dto = new AuthorCreateDto("New", "Bio");
        AuthorResponseDto response = new AuthorResponseDto(1L, "New", "Bio");

        when(authorService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAsUserShouldReturnForbidden() throws Exception {
        AuthorCreateDto updateDto = new AuthorCreateDto("New Name", "New Bio");

        mockMvc.perform(put("/api/authors/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isForbidden());

        verify(authorService, never()).update(any(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAsAdminShouldReturnOk() throws Exception {
        AuthorCreateDto updateDto = new AuthorCreateDto("New Name", "New Bio");
        AuthorResponseDto response = new AuthorResponseDto(1L, "New Name", "New Bio");

        when(authorService.update(updateDto, 1L)).thenReturn(response);

        mockMvc.perform(put("/api/authors/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAsUserShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/authors/1")
                .with(csrf()))
            .andExpect(status().isForbidden());

        verify(authorService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAsAdminShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/authors/1")
                .with(csrf()))
            .andExpect(status().isNoContent());

        verify(authorService, times(1)).delete(1L);
    }

}
