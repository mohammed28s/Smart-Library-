package com.smartlibrary.backend.repository;

import com.smartlibrary.backend.entity.LibraryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryOrderRepository extends JpaRepository<LibraryOrder, Long> {
}
