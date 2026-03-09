package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.ContactMessageDto;
import com.smartlibrary.backend.entity.ContactMessage;
import com.smartlibrary.backend.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    public ContactMessageDto save(ContactMessageDto dto) {
        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());
        ContactMessage saved = repository.save(message);
        dto.setId(saved.getId());
        dto.setCreatedAt(saved.getCreatedAt());
        return dto;
    }
}
