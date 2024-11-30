# Ktor Blog API

A RESTful API built with [Ktor](https://ktor.io/) for managing blog posts, users, and comments. This project demonstrates best practices in Kotlin-based backend development, focusing on modular design, testing, and clean architecture.

## Features

- **User Management**: Create, update, and delete user accounts.
- **Blog Posts**: CRUD operations for blog posts with validation and tagging support.
- **Comments**: Add, edit, or delete comments on posts.
- **Authentication**: JWT-based authentication for secure access.
- **Database Integration**: Uses PostgreSQL for data persistence, managed with Exposed ORM.
- **Testing**: Comprehensive test coverage with Kotest and Testcontainers.

## Getting Started

### Prerequisites

To run this project locally, ensure you have the following:

- [Kotlin](https://kotlinlang.org/) (1.9+)
- [Gradle](https://gradle.org/) (7.6+)
- [Docker](https://www.docker.com/) (optional, for database setup)
- PostgreSQL (14+)

### Setup Instructions

1. **Clone the Repository**

   Clone the project to your local machine:

   ```bash
   git clone https://github.com/albertoadami/ktor-blog-api.git
   cd ktor-blog-api
