package com.smartlibrary.backend.repository;

import com.smartlibrary.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
