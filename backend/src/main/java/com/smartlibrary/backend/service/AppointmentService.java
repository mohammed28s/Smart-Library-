package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.AppointmentDto;
import com.smartlibrary.backend.entity.Appointment;
import com.smartlibrary.backend.entity.ReadingRoom;
import com.smartlibrary.backend.repository.AppointmentRepository;
import com.smartlibrary.backend.repository.ReadingRoomRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ReadingRoomRepository readingRoomRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ReadingRoomRepository readingRoomRepository) {
        this.appointmentRepository = appointmentRepository;
        this.readingRoomRepository = readingRoomRepository;
    }

    public List<AppointmentDto> findUpcoming() {
        return appointmentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDto create(AppointmentDto dto) {
        ReadingRoom room = readingRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.getRoomId()));
        Appointment appointment = new Appointment();
        appointment.setVisitorName(dto.getVisitorName());
        appointment.setVisitorEmail(dto.getVisitorEmail());
        appointment.setRoom(room);
        appointment.setPurpose(dto.getPurpose());
        appointment.setNotes(dto.getNotes());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());

        Appointment saved = appointmentRepository.save(appointment);
        return toDto(saved);
    }

    private AppointmentDto toDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setVisitorName(appointment.getVisitorName());
        dto.setVisitorEmail(appointment.getVisitorEmail());
        dto.setRoomId(appointment.getRoom().getId());
        dto.setRoomName(appointment.getRoom().getName());
        dto.setPurpose(appointment.getPurpose());
        dto.setNotes(appointment.getNotes());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setCreatedAt(appointment.getCreatedAt());
        return dto;
    }
}
