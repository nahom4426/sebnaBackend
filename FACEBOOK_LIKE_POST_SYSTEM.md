# Facebook-Like Post System Implementation

## Overview
A comprehensive Facebook-like social media feature for the Sebna Premium Financing platform with support for:
- **Posts** with image uploads (admin posts for all users, company admin posts for company users)
- **Comments** on posts (threaded discussions)
- **Likes** on posts (toggle like/unlike)
- **Pagination** for all list endpoints
- **Role-based access control** for all operations

## Architecture

### Entities

#### Post Entity
- **Location**: `Entity/Post/Post.java`
- **Features**:
  - Supports global posts (institution_id = null) for super admins
  - Supports institution-specific posts for company admins
  - Image storage as compressed byte array (LOB)
  - Base64 transient field for API responses
  - Relationships: OneToMany with Comments and PostLikes (cascading delete)

#### Comment Entity
- **Location**: `Entity/Post/Comment.java`
- **Features**:
  - Links to Post and User
  - Supports comment creation, update, and deletion
  - Ordered by creation time (ascending)

#### PostLike Entity
- **Location**: `Entity/Post/PostLike.java`
- **Features**:
  - Unique constraint on (post_id, user_id) to prevent duplicate likes
  - Toggle like/unlike functionality
  - Ordered by creation time (descending)

### Repositories

#### PostRepository
- `findByInstitutionIsNullOrderByCreatedAtDesc(Pageable)` - Global posts with pagination
- `findByInstitutionOrderByCreatedAtDesc(Institution, Pageable)` - Institution posts with pagination
- `findByUserOrderByCreatedAtDesc(UserEntity, Pageable)` - User's posts
- `findAllAccessiblePosts(Institution, Pageable)` - Posts accessible to user (global + their institution)
- `countByInstitutionIsNull()` - Count global posts
- `countByInstitution(Institution)` - Count institution posts

#### CommentRepository
- `findByPostOrderByCreatedAtAsc(Post, Pageable)` - Comments with pagination
- `countByPost(Post)` - Count comments on a post
- `findByUserOrderByCreatedAtDesc(UserEntity)` - User's comments

#### PostLikeRepository
- `findByPostAndUser(Post, UserEntity)` - Check if user liked post
- `findByPostOrderByCreatedAtDesc(Post, Pageable)` - Likes with pagination
- `countByPost(Post)` - Count likes on a post
- `findByUserOrderByCreatedAtDesc(UserEntity)` - User's likes

### DTOs

#### Request DTOs
- **PostCreateRequest**: title, content, institutionId (optional)
- **PostUpdateRequest**: title, content
- **CommentRequest**: comment text

#### Response DTOs
- **PostResponse**: 
  - id, title, content, imageBase64
  - user (UserResponse), institution info
  - likeCount, commentCount, userLiked (boolean)
  - createdAt, updatedAt
  
- **CommentResponse**:
  - id, postId, comment text
  - user (UserResponse)
  - createdAt, updatedAt
  
- **PostLikeResponse**:
  - id, postId
  - user (UserResponse)
  - createdAt

### Services

#### PostService
- `landing(Pageable, userUuid)` - Get global posts with pagination
- `listByInstitution(institutionId, Pageable, userUuid)` - Get institution posts
- `listByUser(userId, Pageable)` - Get user's posts
- `create(PostCreateRequest, image, userUuid)` - Create post with optional image
- `update(postId, PostUpdateRequest, image, userUuid)` - Update post (creator only)
- `delete(postId, userUuid)` - Delete post (creator only)
- `getById(postId, userUuid)` - Get single post with like/comment counts

#### CommentService
- `create(postId, CommentRequest, userUuid)` - Create comment
- `update(commentId, CommentRequest, userUuid)` - Update comment (creator only)
- `delete(commentId, userUuid)` - Delete comment (creator only)
- `getByPost(postId, Pageable)` - Get comments with pagination
- `getById(commentId)` - Get single comment

#### PostLikeService
- `toggleLike(postId, userUuid)` - Like/unlike post
- `getByPost(postId, Pageable)` - Get likes with pagination
- `countLikes(postId)` - Get like count
- `isLikedByUser(postId, userUuid)` - Check if user liked post

## API Endpoints

### Posts

#### GET /api/sebna/posts/landing
- **Description**: Get global posts (admin posts for all users)
- **Pagination**: page (default: 1), limit (default: 10)
- **Response**: Page<PostResponse>
- **Auth**: Required

#### GET /api/sebna/posts/institution/{institutionId}
- **Description**: Get institution-specific posts (company admin posts for company users)
- **Pagination**: page (default: 1), limit (default: 10)
- **Response**: Page<PostResponse>
- **Auth**: Required (ROLE_read_posts or ROLE_read_post)

#### GET /api/sebna/posts/user/{userId}
- **Description**: Get user's posts
- **Pagination**: page (default: 1), limit (default: 10)
- **Response**: Page<PostResponse>
- **Auth**: Public

#### GET /api/sebna/posts/{postId}
- **Description**: Get single post with all details
- **Response**: PostResponse
- **Auth**: Required

#### POST /api/sebna/posts
- **Description**: Create new post with optional image
- **Content-Type**: multipart/form-data
- **Parameters**:
  - title (required): String, 3-200 characters
  - content (required): String, 5-5000 characters
  - institutionId (optional): UUID
  - image (optional): File, max 5MB
- **Response**: PostResponse
- **Auth**: Required (ROLE_create_post)
- **Rules**:
  - Global posts (institutionId=null) only for super_admin
  - Company admins can only post to their institution

#### PUT /api/sebna/posts/{postId}
- **Description**: Update post (creator only)
- **Content-Type**: multipart/form-data
- **Parameters**:
  - title (required): String, 3-200 characters
  - content (required): String, 5-5000 characters
  - image (optional): File, max 5MB
- **Response**: PostResponse
- **Auth**: Required (ROLE_update_post)

#### DELETE /api/sebna/posts/{postId}
- **Description**: Delete post (creator only)
- **Auth**: Required (ROLE_delete_post)
- **Response**: 204 No Content

### Comments

#### GET /api/sebna/comments/post/{postId}
- **Description**: Get comments on a post
- **Pagination**: page (default: 1), limit (default: 20)
- **Response**: Page<CommentResponse>
- **Auth**: Public

#### GET /api/sebna/comments/{commentId}
- **Description**: Get single comment
- **Response**: CommentResponse
- **Auth**: Public

#### POST /api/sebna/comments/{postId}
- **Description**: Create comment on post
- **Body**: CommentRequest { comment: String }
- **Response**: CommentResponse
- **Auth**: Required (ROLE_create_comment)

#### PUT /api/sebna/comments/{commentId}
- **Description**: Update comment (creator only)
- **Body**: CommentRequest { comment: String }
- **Response**: CommentResponse
- **Auth**: Required (ROLE_update_comment)

#### DELETE /api/sebna/comments/{commentId}
- **Description**: Delete comment (creator only)
- **Auth**: Required (ROLE_delete_comment)
- **Response**: 204 No Content

### Likes

#### GET /api/sebna/likes/post/{postId}
- **Description**: Get users who liked a post
- **Pagination**: page (default: 1), limit (default: 20)
- **Response**: Page<PostLikeResponse>
- **Auth**: Public

#### GET /api/sebna/likes/post/{postId}/count
- **Description**: Get like count for a post
- **Response**: { "likeCount": number }
- **Auth**: Public

#### GET /api/sebna/likes/post/{postId}/user-liked
- **Description**: Check if current user liked a post
- **Response**: { "liked": boolean }
- **Auth**: Required (ROLE_read_post)

#### POST /api/sebna/likes/{postId}/toggle
- **Description**: Toggle like on post (like if not liked, unlike if liked)
- **Response**: PostLikeResponse (on like) or { "message": "Like removed" } (on unlike)
- **Auth**: Required (ROLE_create_like)

## Image Handling

### Features
- **Compression**: Images are compressed using Deflater (BEST_COMPRESSION)
- **Storage**: Stored as byte array (LOB) in database
- **Size Limit**: 5MB maximum per image
- **Format**: Returned as base64 data URI in responses
- **Decompression**: Automatic decompression on retrieval

### Image Processing Flow
1. User uploads image via multipart/form-data
2. Image bytes are validated (size check)
3. Image is compressed using ImageUtils.compressImage()
4. Compressed bytes stored in Post.image field
5. On retrieval, bytes are decompressed and converted to base64
6. Base64 returned as "data:image/jpeg;base64,..." in response

## Access Control

### Role-Based Permissions
- **ROLE_create_post**: Create posts
- **ROLE_update_post**: Update posts
- **ROLE_delete_post**: Delete posts
- **ROLE_read_posts**: Read institution posts
- **ROLE_read_post**: Read posts
- **ROLE_create_comment**: Create comments
- **ROLE_update_comment**: Update comments
- **ROLE_delete_comment**: Delete comments
- **ROLE_create_like**: Like/unlike posts

### Post Visibility Rules
- **Global Posts**: Only super_admin can create; visible to all authenticated users
- **Institution Posts**: Only company admins of that institution can create; visible to institution members
- **User Posts**: Visible to all authenticated users

## Database Schema

### posts table
```sql
CREATE TABLE posts (
    id UUID PRIMARY KEY,
    institution_id UUID REFERENCES institutions(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    image BYTEA,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### comments table
```sql
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    comment TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### likes table
```sql
CREATE TABLE likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP,
    UNIQUE(post_id, user_id)
);
```

## Error Handling

### Common Errors
- **400 Bad Request**: Invalid input (validation errors)
- **403 Forbidden**: User not authorized (e.g., non-super_admin creating global post)
- **404 Not Found**: Post/Comment/Like not found
- **409 Conflict**: Duplicate like attempt (handled by toggle)

### Validation Rules
- **Post Title**: 3-200 characters, required
- **Post Content**: 5-5000 characters, required
- **Comment**: Required, non-empty
- **Image**: Optional, max 5MB, common formats (JPEG, PNG, GIF, WEBP)

## Example Usage

### Create Post with Image
```bash
curl -X POST http://localhost:8080/api/sebna/posts \
  -H "Authorization: Bearer <token>" \
  -F "title=My First Post" \
  -F "content=This is my first post with an image" \
  -F "image=@/path/to/image.jpg"
```

### Get Landing Posts
```bash
curl http://localhost:8080/api/sebna/posts/landing?page=1&limit=10 \
  -H "Authorization: Bearer <token>"
```

### Create Comment
```bash
curl -X POST http://localhost:8080/api/sebna/comments/{postId} \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"comment": "Great post!"}'
```

### Toggle Like
```bash
curl -X POST http://localhost:8080/api/sebna/likes/{postId}/toggle \
  -H "Authorization: Bearer <token>"
```

## Files Modified/Created

### Modified Files
- `Entity/Post/Post.java` - Added image storage and relationships
- `Repository/Post/PostRepository.java` - Added pagination and query methods
- `Repository/Post/CommentRepository.java` - Added pagination methods
- `Repository/Post/PostLikeRepository.java` - Added query methods
- `Dto/Request/Post/PostRequest.java` - Renamed to PostCreateRequest with validation
- `Service/Post/PostService.java` - Updated interface with new methods
- `ServiceImpl/Post/PostServiceImpl.java` - Complete rewrite with image handling
- `Controller/Post/PostController.java` - Complete rewrite with multipart support

### Created Files
- `Dto/Request/Post/PostUpdateRequest.java`
- `Dto/Response/Post/PostResponse.java`
- `Dto/Response/Comment/CommentResponse.java`
- `Dto/Response/PostLike/PostLikeResponse.java`
- `Service/Post/CommentService.java`
- `Service/Post/PostLikeService.java`
- `ServiceImpl/Post/CommentServiceImpl.java`
- `ServiceImpl/Post/PostLikeServiceImpl.java`
- `Controller/Post/CommentController.java`
- `Controller/Post/PostLikeController.java`

## Features Summary

✅ **Post Management**
- Create posts with optional images (compressed storage)
- Update posts (creator only)
- Delete posts (creator only)
- Pagination support
- Global posts (super admin) and institution posts (company admin)

✅ **Comments**
- Create comments on posts
- Update comments (creator only)
- Delete comments (creator only)
- Pagination support
- Ordered by creation time

✅ **Likes**
- Toggle like/unlike on posts
- View who liked a post
- Like count per post
- Check if user liked a post

✅ **Image Support**
- Multipart form data upload
- Compression for storage efficiency
- Base64 encoding for API responses
- 5MB size limit
- Automatic decompression on retrieval

✅ **Modern & Functional**
- RESTful API design
- Pagination for all list endpoints
- Role-based access control
- Comprehensive error handling
- Transaction support for data consistency
- Efficient database queries with custom repository methods

## Next Steps (Optional Enhancements)

1. **Search**: Add full-text search for posts and comments
2. **Notifications**: Add real-time notifications for likes and comments (WebSocket)
3. **Trending**: Add trending posts endpoint
4. **Reactions**: Extend likes to support emoji reactions
5. **Mentions**: Add @mention support in comments
6. **Hashtags**: Add hashtag support for posts
7. **Media Gallery**: Support multiple images per post
8. **Drafts**: Add draft posts functionality
9. **Sharing**: Add post sharing feature
10. **Analytics**: Add post engagement analytics
