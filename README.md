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

## App Preview

### Start Screen
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/3c7894f5-e39a-49ac-b750-98c436cf74af" width="300">

This is the start screen of the app.

### User Location Screen
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/a88e0a34-f3be-48e8-9470-9966dfbba25d" width="300">

After pressing the start screen, the map opens with the user's current location pinned on the map.

### Google Places Search
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/61ecf9f4-9d4d-4440-be53-2c80cedc7432" width="300">

The Google Places search bar allows the user to search for a location while providing autocomplete suggestions for places in New Zealand.

### Camera and Weather Screen
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/e8de6451-3d31-4dce-b233-73a60137ab7a" width="300">

Once a location has been selected, the map displays the location along with weather information and web cameras within a 10km radius.

#### Weather Description
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/e25dcabf-6363-4829-9e3f-1acf3fcd077a" width="300">

Clicking the weather marker expands to show detailed weather information.

#### Camera Location
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/2f246b5a-8098-4bd0-a949-66e7ba934571" width="300">

Markers for web cameras are also displayed on the map.

### WebCam Preview
<img src="https://github.com/JMillen/Android-Weather-and-Webcam-App/assets/66464271/a293dac9-bae8-4bc0-8d2c-d2edbf7f5087" width="300">

If a user selects a camera via the camera list below the map or clicks on a camera on the map, a preview of the camera will be shown, including details about that camera.
