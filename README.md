# Pinterest Clone - Content Microservice

This is the Content Microservice for a Pinterest Clone application. It handles the creation, storage, and retrieval of Pins and Boards.

## Features

### Pin Management
- Create, read, update, and delete Pins
- Upload images for Pins
- Add metadata (title, description, keywords) to Pins
- Set privacy settings for Pins
- Save Pins as drafts
- Search Pins by keywords

### Board Management
- Create, read, update, and delete Boards
- Organize Pins into Boards
- Set privacy settings for Boards
- Reorder Boards
- Search Boards by title or description
- Support for collaborative Boards

## Technologies Used

- Spring Boot 3.3.12
- Spring Data JPA
- Spring Cloud Consul for service discovery
- MySQL for data storage
- Lombok for reducing boilerplate code
- Bean Validation for input validation

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- MySQL
- Consul (for service discovery)

### Setup

1. Clone the repository

2. Configure MySQL database in `application.yml`

3. Start Consul
   ```
   consul agent -dev
   ```

4. Build the application
   ```
   mvn clean install
   ```

5. Run the application
   ```
   mvn spring-boot:run
   ```

## API Endpoints

### Pin Endpoints

- `POST /api/content/pins` - Create a new Pin
- `GET /api/content/pins/{pinId}` - Get a Pin by ID
- `PUT /api/content/pins/{pinId}` - Update a Pin
- `DELETE /api/content/pins/{pinId}` - Delete a Pin
- `GET /api/content/pins/user/{userId}` - Get all Pins by user ID
- `GET /api/content/pins/board/{boardId}` - Get all Pins by board ID
- `GET /api/content/pins/search?query={searchTerm}` - Search Pins
- `GET /api/content/pins/user/{userId}/drafts` - Get draft Pins by user ID
- `PUT /api/content/pins/{pinId}/publish` - Publish a draft Pin
- `PUT /api/content/pins/{pinId}/board/{boardId}` - Save a Pin to a Board

### Board Endpoints

- `POST /api/content/boards` - Create a new Board
- `GET /api/content/boards/{boardId}` - Get a Board by ID
- `PUT /api/content/boards/{boardId}` - Update a Board
- `DELETE /api/content/boards/{boardId}` - Delete a Board
- `GET /api/content/boards/user/{userId}` - Get all Boards by user ID
- `GET /api/content/boards/search?query={searchTerm}` - Search Boards
- `GET /api/content/boards/category/{category}` - Get Boards by category
- `PUT /api/content/boards/user/{userId}/order` - Update Board display order
- `GET /api/content/boards/user/{userId}/collaborative` - Get collaborative Boards by user ID

### File Upload Endpoints

- `POST /api/content/files/upload` - Upload a file
- `GET /api/content/files/{fileName}` - Get a file
- `DELETE /api/content/files/{fileName}` - Delete a file

## Future Improvements

- Implement caching for frequently accessed Pins and Boards
- Add support for video content
- Integrate with a cloud storage service for file storage
- Implement rate limiting for API endpoints
- Add analytics for Pin and Board engagement