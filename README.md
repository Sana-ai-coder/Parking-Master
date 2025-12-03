# Parking Master — Smart Parking Locator App

Parking Master is an intelligent Android application that helps users find nearby parking spots effortlessly. Built with Java and powered by Google Maps Platform, it provides real-time parking location discovery, route navigation with multiple path options, and an intuitive user interface for seamless parking spot hunting.

---

## Table of Contents

- [Screenshots](#screenshots)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [How to Run the Project](#how-to-run-the-project)
- [Project Structure](#project-structure)
- [API Configuration](#api-configuration)
- [App Architecture](#app-architecture)
- [Developer Notes](#developer-notes)
- [Contributing](#contributing)
- [Developed By](#developed-by)

---

<h2 id="screenshots">📸 Screenshots</h2>

_Add screenshots of your app here_

---

<h2 id="key-features">🌟 Key Features</h2>

### 📍 Location Selection

- **Current Location**: Automatically detects user's GPS location using FusedLocationProviderClient.
- **Custom Location**: Enter coordinates (latitude, longitude) or predefined landmarks (Taj Mahal, Taj Hotel, Statue of Unity).
- **Smart Input Validation**: Regex-based validation for coordinate formats.
- **Switch Toggle**: Seamlessly switch between current and custom location modes.

### 🅿️ Parking Discovery

- **Nearby Search**: Finds parking spots within 2.5km radius using Google Places API.
- **Visual Markers**: Parking locations displayed as blue markers on Google Maps.
- **Interactive Map**: Click markers to view parking details and get directions.
- **Real-time Updates**: Dynamic marker placement based on API responses.

### 🗺️ Navigation & Routes

- **Multiple Route Options**: Shows all possible paths from user location to selected parking spot.
- **Visual Route Differentiation**: Primary route in blue (thick line), alternatives in gray.
- **Turn-by-Turn Directions**: Integrated Google Directions API for accurate pathfinding.
- **Polyline Drawing**: Clear visual representation of routes on the map.

### 🎨 User Interface

- **Vehicle Type Selection**: Dropdown spinner for different vehicle types.
- **Custom Map Controls**: Zoom in/out buttons for easy map navigation.
- **Find Parking Button**: One-tap search for nearby parking spots.
- **Location Marker**: Red marker showing user's current position.
- **Responsive Design**: Optimized layouts for different screen sizes.

### 🔐 Permissions & Security

- **Runtime Permissions**: Requests location access only when needed.
- **Permission Handling**: Graceful fallback if permissions denied.
- **Secure API Key Management**: Uses Gradle Secrets plugin for API key storage.

---

<h2 id="tech-stack">🛠️ Tech Stack</h2>

- **Language**: Java (JDK 8)
- **Platform**: Android (API 26+)
- **Build System**: Gradle (Kotlin DSL)
- **Maps SDK**: Google Maps Android API v18.1.0
- **Location Services**: Google Play Services Location v18.0.0
- **Directions API**: Google Maps Services v2.2.0
- **Networking**: Volley v1.2.1 (for REST API calls)
- **UI Components**: Material Design v1.9.0
- **Permissions**: Android Runtime Permissions

---

<h2 id="how-to-run-the-project">🚀 How to Run the Project</h2>

Follow these steps to set up and run Parking Master on your Android device or emulator.

### ✅ 1. Prerequisites

- **Android Studio**: Latest stable version (Electric Eel or newer recommended)
- **Java Development Kit (JDK)**: Version 8 or higher
- **Android SDK**: API Level 26 (Android 8.0) or higher
- **Google Maps API Key**: Obtain from [Google Cloud Console](https://console.cloud.google.com/)
- **Physical Device or Emulator**: With GPS and Google Play Services

### ✅ 2. Clone the Repository

```cmd
git clone https://github.com/Sana-ai-coder/Parking-Master.git
cd Parking-Master
```

### ✅ 3. Configure Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the following APIs:
   - Maps SDK for Android
   - Places API
   - Directions API
4. Create credentials (API Key)
5. Create a `local.properties` file in the root directory:

```properties
MAPS_API_KEY=YOUR_API_KEY_HERE
```

6. Update `app/src/main/res/values/google_maps_api.xml`:

```xml
<string name="google_maps_key">YOUR_API_KEY_HERE</string>
```

### ✅ 4. Open the Project in Android Studio

- Launch Android Studio
- Select **File → Open**
- Navigate to the cloned `Parking-Master` folder
- Wait for Gradle sync to complete

### ✅ 5. Build and Run

- Connect an Android device via USB (with USB Debugging enabled) or start an emulator
- Click the **Run ▶️** button in Android Studio
- Select your target device
- Grant location permissions when prompted

### ✅ 6. Testing the App

1. Launch the app
2. Select a vehicle type from the dropdown
3. Choose between current location or custom location
4. Tap **Find Parking Spot** button
5. On the map screen, tap the **P** icon to find nearby parking
6. Click on any blue parking marker to view routes
7. Navigate using the displayed path

---

<h2 id="project-structure">📂 Project Structure</h2>

```text
Parking-Master/
├── app/
│   ├── build.gradle.kts           # App-level Gradle build configuration
│   ├── proguard-rules.pro         # ProGuard rules for code obfuscation
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml           # App manifest (permissions, activities)
│       │   ├── java/com/projects/parkingapplication/
│       │   │   ├── MainActivity.java         # Main screen (location selection)
│       │   │   └── MapsActivity.java         # Maps screen (parking search & navigation)
│       │   └── res/
│       │       ├── drawable/                 # Icons and custom drawables
│       │       ├── layout/
│       │       │   ├── activity_main.xml     # Main screen layout
│       │       │   └── activity_maps.xml     # Maps screen layout
│       │       ├── menu/                     # Menu resources
│       │       ├── values/
│       │       │   ├── arrays.xml            # Vehicle types array
│       │       │   ├── colors.xml            # Color palette
│       │       │   ├── strings.xml           # String resources
│       │       │   ├── google_maps_api.xml   # Maps API key storage
│       │       │   └── themes.xml            # App themes
│       │       └── xml/                      # Backup and data extraction rules
│       ├── androidTest/                      # Instrumented tests
│       └── test/                             # Unit tests
├── gradle/                                   # Gradle wrapper files
├── build.gradle.kts                          # Project-level Gradle configuration
├── settings.gradle.kts                       # Gradle settings
├── local.properties                          # Local config (API keys) - NOT committed
└── README.md                                 # Project documentation
```

---

<h2 id="api-configuration">🔑 API Configuration</h2>

### Google Places Nearby Search API

The app uses this API to find parking spots near a given location:

```java
String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=LAT,LNG&radius=2500&types=parking&sensor=false&key=YOUR_API_KEY";
```

- **Radius**: 2500 meters (2.5 km)
- **Type**: parking
- **Response**: JSON with parking location details

### Google Directions API

Used for calculating routes between user location and selected parking spot:

```java
DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
directions.alternatives(true);  // Returns multiple route options
directions.origin(userLocation);
directions.destination(parkingLocation);
```

---

<h2 id="app-architecture">🏗️ App Architecture</h2>

### MainActivity (First Screen)

**Purpose**: Location selection and vehicle type input

**Key Components**:
- `SwitchCompat`: Toggle between current/custom location
- `EditText`: Custom location input (coordinates or landmarks)
- `Spinner`: Vehicle type selector
- `Button`: Navigate to maps screen

**Logic**:
- Validates coordinate format using regex: `^-?\d{1,2}\.\d{2,},-?\d{1,3}\.\d{2,}$`
- Supports predefined landmarks (Taj Mahal, Taj Hotel, Statue of Unity)
- Passes location data to MapsActivity via Intent

### MapsActivity (Second Screen)

**Purpose**: Display map, find parking, show routes

**Key Components**:
- `MapView`: Google Maps display
- `FusedLocationProviderClient`: GPS location provider
- `ImageButton`: Zoom controls and parking search
- `RequestQueue`: Volley for API calls

**Logic Flow**:
1. Request location permissions
2. Display user's current/custom location
3. On "Find Parking" button click → Call Places API
4. Display parking spots as blue markers
5. On marker click → Calculate directions
6. Draw polylines showing routes (blue = primary, gray = alternatives)

**API Integration**:
- **Places API**: Fetches nearby parking spots
- **Directions API**: Calculates routes
- **Volley**: Handles REST API requests

---

<h2 id="developer-notes">📝 Developer Notes</h2>

### Predefined Locations

The app includes hardcoded famous locations for testing:

```java
Map<String, String> locations = new HashMap<String, String>() {{
    put("tajmahal", "27.173891,78.042068");
    put("tajhotel", "18.921729,72.833031");
    put("statueofunity", "21.8380,73.7191");
}};
```

### Location Input Formats

Supported input formats:
- **Coordinates**: `28.6139,77.2090` (New Delhi)
- **Landmarks**: `tajmahal`, `tajhotel`, `statueofunity`
- **Default**: Falls back to Taj Mahal if invalid

### API Key Security

- Store API keys in `local.properties` (not version controlled)
- Use Gradle Secrets Plugin for secure injection
- Never commit `local.properties` to Git

### Permission Handling

The app requests these permissions at runtime:
- `ACCESS_FINE_LOCATION`: For precise GPS location
- `ACCESS_COARSE_LOCATION`: For approximate location
- `INTERNET`: For API calls
- `WRITE_EXTERNAL_STORAGE`: For map caching

### Memory Management

Lifecycle methods properly handle map resources:
- `onResume()`: Resume map rendering
- `onPause()`: Pause map updates
- `onDestroy()`: Clean up map resources
- `onLowMemory()`: Handle low memory scenarios

---

<h2 id="contributing">🤝 Contributing</h2>

Contributions are welcome! Follow these steps:

1. **Fork the repository**

```cmd
git fork https://github.com/Sana-ai-coder/Parking-Master.git
```

2. **Create a feature branch**

```cmd
git checkout -b feature/AmazingFeature
```

3. **Make your changes**
   - Follow Java coding conventions
   - Add comments for complex logic
   - Test on multiple devices

4. **Commit your changes**

```cmd
git commit -m "Add: Implemented amazing feature"
```

5. **Push to your branch**

```cmd
git push origin feature/AmazingFeature
```

6. **Open a Pull Request**
   - Provide a clear description of changes
   - Reference any related issues

---

<h2 id="developed-by">👨‍💻 Developed By</h2>

**Sana Girish**
