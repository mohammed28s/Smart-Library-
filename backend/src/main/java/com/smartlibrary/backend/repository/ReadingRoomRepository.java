package com.smartlibrary.backend.repository;

import com.smartlibrary.backend.entity.ReadingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRoomRepository extends JpaRepository<ReadingRoom, Long> {
    boolean existsByName(String name);
}
