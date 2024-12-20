# JavaFx
# What is this?

This is an assignment from the "Advanced Programming Methods" course at UBB Cluj-Napoca. It demonstrates a local social network using the **Observer**, **Repository**, and **Singleton** patterns. Additionally, the application implements **pagination** both at the **GUI** and **Repository** level.

## Database

The project uses a local PostgreSQL server for the database. To set it up and test the code locally, you can use the SQL file provided.

## Setup Instructions

1. **Populate the Database**  
   To populate your local database, you can run the SQL file located in `src/main/dbStart.sql`.

2. **Testing Locally**  
   Once the database is populated, you can test the code locally by running the application. Make sure the PostgreSQL server is running and accessible on your machine.

## Technologies Used

- PostgreSQL (local database)
- Java
- Design Patterns: Observer, Repository, Singleton

## Video Showcase
  
[YouTube](https://youtu.be/ljC1vVDEbQ4)

## Possible Improvements

1. **Refactor Friendships View Repository**  
   - Improve the repository design for the friendships view by adding relevant SQL joins to optimize data retrieval.

2. **Separate Views for Different Features**  
   - Split the views related to the search functionality, friend list, and friend requests into separate components.

3. **Implement Separate Thread for Rendering Messages**  
   - Create a separate thread dedicated to rendering certain message components.



 
