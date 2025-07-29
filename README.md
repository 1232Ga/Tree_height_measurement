# 🌲 Tree Height Measurement in Forestry (Camera-Based Android App)

## 📱 Overview

**Tree Height Measurement App** is an Android mobile application designed for **forestry and environmental researchers** to measure tree height using photos captured via the phone’s camera. The app simplifies traditional manual methods by providing a semi-automated approach for capturing, annotating, and calculating the approximate height of a tree based on perspective and scale references.

---

## 🎯 Objective

To enable field officers, surveyors, and researchers to:
- Capture photos of trees from the ground
- Measure approximate tree height using reference scale and angles
- Store and export measurement data for research and analysis
- Work in remote areas with **offline capabilities**

---

## 🔧 Features

- 📷 **Capture Tree Image**: Use the device camera to click a photo of the tree.
- 📏 **Height Calculation Tool**:
    - Mark base and top of the tree on image
    - Use a known object (e.g., person or pole) as a reference for scaling
- 📍 **GPS Location Tagging**: Automatically record the latitude and longitude
- 🧾 **Data Logging**:
    - Tree species (optional)
    - Height (in meters/feet)
    - Date, Time, and Location
- 🗃️ **Export Reports** in CSV or PDF format
- 🔄 **Offline Save & Sync**: Work offline and sync when internet is available

---

## 🏗️ Architecture

- **Language**: Kotlin
- **Pattern**: MVVM (Model-View-ViewModel)
- **CameraX**: For camera integration
- **OpenCV / Image Overlay**: For point selection and scaling
- **Room DB**: For local storage
- **Location API**: FusedLocationProviderClient
- **Permissions**: Camera, Location, Storage

---

## 📸 Usage Workflow

1. **User opens app → taps "Capture Tree"**
2. **Camera opens** → captures full tree image
3. **User marks two points on image**:
    - Bottom of the tree
    - Top of the tree
4. **User marks a known reference (e.g., 1.8m tall person)**
5. **App calculates approximate height** using pixel-to-meter ratio
6. **Stores result** with location and timestamp
7. **User can export data** or view it in list form

