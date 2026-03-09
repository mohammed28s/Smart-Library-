package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.ReadingRoomDto;
import com.smartlibrary.backend.entity.ReadingRoom;
import com.smartlibrary.backend.repository.ReadingRoomRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReadingRoomService {

    private final ReadingRoomRepository readingRoomRepository;

    public ReadingRoomService(ReadingRoomRepository readingRoomRepository) {
        this.readingRoomRepository = readingRoomRepository;
    }

    public List<ReadingRoomDto> findAll() {
        return readingRoomRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ReadingRoomDto toDto(ReadingRoom room) {
        ReadingRoomDto dto = new ReadingRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setCapacity(room.getCapacity());
        return dto;
    }
}
