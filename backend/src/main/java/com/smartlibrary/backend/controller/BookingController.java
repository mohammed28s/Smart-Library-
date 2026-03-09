package com.smartlibrary.backend.controller;

import com.smartlibrary.backend.dto.AppointmentDto;
import com.smartlibrary.backend.dto.ReadingRoomDto;
import com.smartlibrary.backend.service.AppointmentService;
import com.smartlibrary.backend.service.ReadingRoomService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final ReadingRoomService readingRoomService;
    private final AppointmentService appointmentService;

    public BookingController(ReadingRoomService readingRoomService, AppointmentService appointmentService) {
        this.readingRoomService = readingRoomService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/rooms")
    public List<ReadingRoomDto> rooms() {
        return readingRoomService.findAll();
    }

    @GetMapping("/appointments")
    public List<AppointmentDto> appointments() {
        return appointmentService.findUpcoming();
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto book(@Valid @RequestBody AppointmentDto dto) {
        return appointmentService.create(dto);
    }
}
