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

### Pins

- `POST /api/content/pins` - Create a new pin
- `GET /api/content/pins/{id}` - Get a pin by ID
- `PUT /api/content/pins/{id}` - Update a pin
- `DELETE /api/content/pins/{id}` - Delete a pin
- `GET /api/content/pins/user/{userId}` - Get all pins by user ID
- `GET /api/content/pins/user/{userId}/page` - Get paginated pins by user ID
- `GET /api/content/pins/board/{boardId}` - Get all pins by board ID
- `GET /api/content/pins/board/{boardId}/page` - Get paginated pins by board ID
- `GET /api/content/pins/search` - Search pins by keyword
- `GET /api/content/pins/drafts/{userId}` - Get draft pins by user ID
- `PUT /api/content/pins/{id}/publish` - Publish a draft pin
- `PUT /api/content/pins/{id}/board/{boardId}` - Save a pin to a board

### Boards

- `POST /api/content/boards` - Create a new board
- `GET /api/content/boards/{id}` - Get a board by ID
- `PUT /api/content/boards/{id}` - Update a board
- `DELETE /api/content/boards/{id}` - Delete a board
- `GET /api/content/boards/user/{userId}` - Get all boards by user ID
- `GET /api/content/boards/user/{userId}/page` - Get paginated boards by user ID
- `GET /api/content/boards/search` - Search boards by keyword
- `GET /api/content/boards/category/{category}` - Get boards by category
- `PUT /api/content/boards/{id}/display-order` - Update board display order
- `GET /api/content/boards/collaborative/{userId}` - Get collaborative boards for a user

### Keywords

- `POST /api/content/keywords` - Create a new keyword
- `GET /api/content/keywords/{id}` - Get a keyword by ID
- `GET /api/content/keywords/name/{name}` - Get a keyword by name
- `GET /api/content/keywords` - Get all keywords
- `GET /api/content/keywords/search` - Search keywords by name
- `GET /api/content/keywords/popular` - Get most used keywords
- `GET /api/content/keywords/pin/{pinId}` - Get keywords for a pin

### Files

- `POST /api/content/files/upload` - Upload a file
- `GET /api/content/files/{fileName}` - Download a file
- `DELETE /api/content/files/{fileName}` - Delete a file

## Future Improvements

- Implement caching for frequently accessed Pins and Boards
- Add support for video content
- Integrate with a cloud storage service for file storage
- Implement rate limiting for API endpoints
- Add analytics for Pin and Board engagement