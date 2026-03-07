package com.smartlibrary.backend.controller;

import com.smartlibrary.backend.dto.LibraryOrderDto;
import com.smartlibrary.backend.service.LibraryOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class LibraryOrderController {

    private final LibraryOrderService libraryOrderService;

    public LibraryOrderController(LibraryOrderService libraryOrderService) {
        this.libraryOrderService = libraryOrderService;
    }

    @GetMapping
    public List<LibraryOrderDto> findAll() {
        return libraryOrderService.findAll();
    }

    @GetMapping("/{id}")
    public LibraryOrderDto findById(@PathVariable Long id) {
        return libraryOrderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryOrderDto create(@Valid @RequestBody LibraryOrderDto dto) {
        return libraryOrderService.create(dto);
    }

    @PutMapping("/{id}")
    public LibraryOrderDto update(@PathVariable Long id, @Valid @RequestBody LibraryOrderDto dto) {
        return libraryOrderService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        libraryOrderService.delete(id);
    }
}
