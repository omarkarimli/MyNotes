# MyNotes

MyNotes is a note-taking application designed using the MVVM (Model-View-ViewModel) architecture pattern. The application allows users to create, edit, and delete notes, providing a seamless and intuitive experience for managing personal notes.

## Features

- Create, edit, and delete notes.
- Organize notes with categories or tags.
- Search functionality to find notes quickly.
- User-friendly interface designed for ease of use.
- Data persistence using a local database.

## Architecture

MyNotes follows the MVVM architecture pattern:

- **Model**: Represents the data and business logic. It handles data operations and communicates with the local database.
- **View**: The user interface that displays the data and allows user interaction.
- **ViewModel**: Acts as a bridge between the Model and the View. It provides data to the View and responds to user interactions by updating the Model.

## Technology Stack

- **Android SDK**: For building the application.
- **Kotlin**: The primary programming language.
- **Room**: For local database management.
- **LiveData**: To observe data changes in the UI.
- **ViewModel**: To manage UI-related data in a lifecycle-conscious way.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/omarkarimli/MyNotes.git

2. Open the project in Android Studio.

3. Sync the project with Gradle files.

4. Build and run the application on your Android device or emulator.

## Usage

1. Open the MyNotes application.
2. Create a new note by tapping the "Add Note" button.
3. Enter your note's title and content.
4. Save your note to view it in the list.
5. Edit or delete notes by selecting the respective options.

## Contributing

Contributions are welcome! If you have suggestions or improvements, please fork the repository and create a pull request.

## License

This project is licensed under the [MIT License](LICENSE). See the LICENSE file for details.

## Screenshots

![Home Page](screenshots/screenshots_home.png)
