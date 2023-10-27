# Android Weather and Webcam App

## Project Description

This Android application is the result of a university project that showcases a variety of Android features, including location-based services, API integration, and responsive UI design. The app enables users to search for a destination, retrieve weather information, and access web camera feeds in the selected location.

## Features

### Introduction Screen
When the user opens the application, they are greeted with an introduction screen.

### Location Search
After clicking "start," the app opens a new Activity featuring a Google Places autocomplete search bar and a map centered on the user's current location.

### Weather Information
The app retrieves weather data and displays it on the map with a weather marker. Clicking the weather marker expands to show detailed weather information.

### Web Cameras
The app also displays markers for the nearest 5 web cameras on the map and in a ListView. Clicking a camera marker or a camera ListView item opens a new Activity with a thumbnail of the webcam image and various details.

## Usage

To use this application, you will need to obtain the necessary API keys:

- Google API key: [Get it here](https://developers.google.com/maps/documentation/places/web-service/get-api-key).
- OpenWeatherMap API key: [Get it here](https://openweathermap.org).
- Windy API key: [Get it here](https://api.windy.com/webcams).

To view the application, you will need to install Android Studio and use version control to import a cloned version of the app.

## Technologies Used

- Android Studio
- Java
- Google Maps API
- Google Places API
- Volley
- External APIs (OpenWeatherApp API and Windy API)
