# Quick Start Guide - Facebook-Like Post System

## Setup & Configuration

No additional configuration needed. The system uses existing Spring Boot, JPA, and security infrastructure.

## Key Classes Overview

### Entities
- `Post.java` - Main post entity with image support
- `Comment.java` - Comment on posts
- `PostLike.java` - Like tracking

### Services
- `PostService/PostServiceImpl` - Post CRUD and pagination
- `CommentService/CommentServiceImpl` - Comment management
- `PostLikeService/PostLikeServiceImpl` - Like toggle and tracking

### Controllers
- `PostController` - Post endpoints with image upload
- `CommentController` - Comment endpoints
- `PostLikeController` - Like endpoints

### DTOs
- Request: `PostCreateRequest`, `PostUpdateRequest`, `CommentRequest`
- Response: `PostResponse`, `CommentResponse`, `PostLikeResponse`

## Common Operations

### 1. Create a Post with Image
```bash
curl -X POST http://localhost:8080/api/sebna/posts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=My Post Title" \
  -F "content=This is my post content" \
  -F "institutionId=550e8400-e29b-41d4-a716-446655440000" \
  -F "image=@image.jpg"
```

### 2. Get Posts (with pagination)
```bash
# Global posts (admin posts for all users)
curl http://localhost:8080/api/sebna/posts/landing?page=1&limit=10 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Institution posts (company admin posts)
curl http://localhost:8080/api/sebna/posts/institution/{institutionId}?page=1&limit=10 \
  -H "Authorization: Bearer YOUR_TOKEN"

# User's posts
curl http://localhost:8080/api/sebna/posts/user/{userId}?page=1&limit=10 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Update a Post
```bash
curl -X PUT http://localhost:8080/api/sebna/posts/{postId} \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Updated Title" \
  -F "content=Updated content" \
  -F "image=@new_image.jpg"
```

### 4. Delete a Post
```bash
curl -X DELETE http://localhost:8080/api/sebna/posts/{postId} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. Add a Comment
```bash
curl -X POST http://localhost:8080/api/sebna/comments/{postId} \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"comment": "Great post!"}'
```

### 6. Get Comments on a Post
```bash
curl http://localhost:8080/api/sebna/comments/post/{postId}?page=1&limit=20 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 7. Update a Comment
```bash
curl -X PUT http://localhost:8080/api/sebna/comments/{commentId} \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"comment": "Updated comment"}'
```

### 8. Delete a Comment
```bash
curl -X DELETE http://localhost:8080/api/sebna/comments/{commentId} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 9. Toggle Like on a Post
```bash
curl -X POST http://localhost:8080/api/sebna/likes/{postId}/toggle \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 10. Get Like Count
```bash
curl http://localhost:8080/api/sebna/likes/post/{postId}/count \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 11. Check if User Liked a Post
```bash
curl http://localhost:8080/api/sebna/likes/post/{postId}/user-liked \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 12. Get Users Who Liked a Post
```bash
curl http://localhost:8080/api/sebna/likes/post/{postId}?page=1&limit=20 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Response Examples

### Post Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "My Post",
  "content": "Post content here",
  "imageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001",
  "institutionName": "My Institution",
  "user": {
    "id": 1,
    "userUuid": "user-uuid-123",
    "firstName": "John",
    "fatherName": "Doe",
    "grandFatherName": "Smith",
    "email": "john@example.com",
    "mobilePhone": "+251912345678"
  },
  "likeCount": 42,
  "commentCount": 15,
  "userLiked": true,
  "createdAt": "2025-12-14T20:30:00",
  "updatedAt": "2025-12-14T21:00:00"
}
```

### Comment Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "postId": "550e8400-e29b-41d4-a716-446655440000",
  "comment": "Great post!",
  "user": {
    "id": 2,
    "userUuid": "user-uuid-456",
    "firstName": "Jane",
    "fatherName": "Smith",
    "grandFatherName": "Johnson",
    "email": "jane@example.com",
    "mobilePhone": "+251987654321"
  },
  "createdAt": "2025-12-14T20:45:00",
  "updatedAt": "2025-12-14T20:45:00"
}
```

### Like Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "postId": "550e8400-e29b-41d4-a716-446655440000",
  "user": {
    "id": 3,
    "userUuid": "user-uuid-789",
    "firstName": "Bob",
    "fatherName": "Johnson",
    "grandFatherName": "Williams",
    "email": "bob@example.com",
    "mobilePhone": "+251911111111"
  },
  "createdAt": "2025-12-14T20:50:00"
}
```

## Permissions Required

| Operation | Role Required |
|-----------|---------------|
| Create Post | ROLE_create_post |
| Update Post | ROLE_update_post |
| Delete Post | ROLE_delete_post |
| Read Posts | ROLE_read_post |
| Read Institution Posts | ROLE_read_posts or ROLE_read_post |
| Create Comment | ROLE_create_comment |
| Update Comment | ROLE_update_comment |
| Delete Comment | ROLE_delete_comment |
| Create Like | ROLE_create_like |

## Important Notes

1. **Global Posts**: Only super_admin can create posts without institutionId
2. **Institution Posts**: Company admins can only post to their own institution
3. **Image Size**: Maximum 5MB per image
4. **Image Compression**: Images are automatically compressed for storage
5. **Like Toggle**: POST to toggle endpoint automatically likes/unlikes
6. **Creator Only**: Only post/comment creators can update or delete their own content
7. **Pagination**: Default page=1, default limit=10 (posts) or 20 (comments/likes)

## Testing with Postman

1. Set up Bearer token in Authorization header
2. Use multipart/form-data for POST/PUT requests with images
3. Use application/json for comment operations
4. Check response status codes:
   - 200: Success
   - 201: Created
   - 204: Deleted
   - 400: Bad request
   - 403: Forbidden
   - 404: Not found

## Troubleshooting

**Image not uploading?**
- Check file size (max 5MB)
- Verify Content-Type is multipart/form-data
- Ensure image parameter name matches

**Permission denied?**
- Verify user has required role
- Check token is valid and not expired
- For global posts, ensure user is super_admin

**Post not found?**
- Verify postId is correct UUID format
- Check post hasn't been deleted
- Ensure user has access to institution (if applicable)

**Comment/Like operations failing?**
- Verify post exists
- Check user is authenticated
- Ensure user has required role
