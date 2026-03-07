package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.BookDto;
import com.smartlibrary.backend.entity.Book;
import com.smartlibrary.backend.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BarcodeService barcodeService;

    public BookService(BookRepository bookRepository, BarcodeService barcodeService) {
        this.bookRepository = bookRepository;
        this.barcodeService = barcodeService;
    }

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(this::toDto).toList();
    }

    public BookDto findById(Long id) {
        return toDto(getBook(id));
    }

    public BookDto create(BookDto dto) {
        Book book = new Book();
        apply(book, dto);
        return toDto(bookRepository.save(book));
    }

    public BookDto update(Long id, BookDto dto) {
        Book book = getBook(id);
        apply(book, dto);
        return toDto(bookRepository.save(book));
    }

    public void delete(Long id) {
        bookRepository.delete(getBook(id));
    }

    public byte[] getBarcodeImage(Long id) {
        Book book = getBook(id);
        String barcodeValue = buildBarcodeValue(book);
        return barcodeService.generateCode128Png(barcodeValue);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
    }

    private void apply(Book book, BookDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPrice(dto.getPrice());
        book.setStock(dto.getStock());
        book.setDescription(dto.getDescription());
    }

    private BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setDescription(book.getDescription());
        dto.setBarcode(buildBarcodeValue(book));
        return dto;
    }

    private String buildBarcodeValue(Book book) {
        if (StringUtils.hasText(book.getIsbn())) {
            return book.getIsbn().trim();
        }
        return "BOOK-" + book.getId();
    }
}
