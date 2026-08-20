# Local Setup & Startup Guide

Follow these steps to run the Triple Store backend and Vue.js web application locally.

## Prerequisites
* **JDK 25**
* **Node.js** & **npm**

## 1. Start Backend (Spring Boot)

1. Navigate to the Backend Directory:
   ```
   cd semantics/app/backend/semantics
   ```
   
2. Launch the application via the Gradle wrapper:

    - Linux / macOS:
    ```
    ./gradlew bootRun
    ```

    - Windows:
    ```
    gradlew.bat bootRun
    ```

The Backend will be accessible at http://localhost:8080.

## 2. Start Frontend (Vue.js)

1. Navigate to the Frontend Directory:
   ```
   cd semantics/app/frontend
   ```
   
2. Install dependencies:

    ```
    npm install
    ```

3. Start the Vite development server:

    ```
    npm run dev
    ```

Open http://localhost:5173 in your Browser to access the Web-Interface.