package com.elearning.platform.controller;

import com.elearning.platform.dto.BookmarkDTO;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.BookmarkService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public List<BookmarkDTO> list(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return bookmarkService.getBookmarks(userDetails.getId());
    }

    @PostMapping("/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookmarkDTO add(@PathVariable Long courseId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return bookmarkService.addBookmark(userDetails.getId(), courseId);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long courseId,
                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        bookmarkService.removeBookmark(userDetails.getId(), courseId);
    }
}
