package Medco.PremuimFinancing.Controller.Post;

import Medco.PremuimFinancing.Dto.Request.Post.PostCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Post.PostUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Post.PostResponse;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import Medco.PremuimFinancing.Service.Post.PostService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/landing")
    public ResponseEntity<Page<PostResponse>> landing(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "category", required = false) String category) {
        var auth = SecurityUtils.getAuthenticatedUserOrNull();
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "desc");
        Page<PostResponse> posts = postService.landing(pageable, auth == null ? null : auth.getUserUuid(), parseCategoryOrNull(category));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> listByUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "desc");
        Page<PostResponse> posts = postService.listByUser(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getById(@PathVariable UUID postId) {
        var auth = SecurityUtils.getAuthenticatedUserOrNull();
        PostResponse post = postService.getById(postId, auth == null ? null : auth.getUserUuid());
        return ResponseEntity.ok(post);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_create_post')")
    public ResponseEntity<PostResponse> create(
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "content", required = true) String content,
            @RequestParam(value = "category", required = true) String category,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        var auth = SecurityUtils.getAuthenticatedUser();

        PostCreateRequest request = new PostCreateRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setCategory(parseCategoryRequired(category));

        PostResponse created = postService.create(request, image, auth.getUserUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_update_post')")
    public ResponseEntity<PostResponse> update(
            @PathVariable UUID postId,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "content", required = true) String content,
            @RequestParam(value = "category", required = true) String category,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        var auth = SecurityUtils.getAuthenticatedUser();

        PostUpdateRequest request = new PostUpdateRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setCategory(parseCategoryRequired(category));

        PostResponse updated = postService.update(postId, request, image, auth.getUserUuid());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('ROLE_delete_post')")
    public ResponseEntity<Void> delete(@PathVariable UUID postId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        postService.delete(postId, auth.getUserUuid());
        return ResponseEntity.noContent().build();
    }

    private PostCategory parseCategoryRequired(String raw) {
        PostCategory category = parseCategoryOrNull(raw);
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        return category;
    }

    private PostCategory parseCategoryOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String normalized = raw.trim().toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');

        if ("ALL".equals(normalized) || "ALL_NEWS".equals(normalized)) {
            return null;
        }
        if ("INVESTMENT".equals(normalized) || "INVESTMENT_NEWS".equals(normalized)) {
            return PostCategory.INVESTMENT_NEWS;
        }
        if ("MARKET".equals(normalized) || "MARKET_NEWS".equals(normalized)) {
            return PostCategory.MARKET_NEWS;
        }
        if ("COMPANY".equals(normalized) || "COMPANY_NEWS".equals(normalized)) {
            return PostCategory.COMPANY_NEWS;
        }

        return PostCategory.valueOf(normalized);
    }
}
