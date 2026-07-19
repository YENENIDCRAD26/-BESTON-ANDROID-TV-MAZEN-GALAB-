#!/bin/bash
# scripts/termux-build.sh
# Termux Build Script for Smart Screen OS

echo "Updating Termux packages..."
pkg update && pkg upgrade -y

echo "Installing necessary build tools (OpenJDK 17 and Gradle)..."
pkg install -y openjdk-17 gradle

echo "Setting up environment variables..."
export JAVA_HOME=$PREFIX/opt/openjdk
export PATH=$PATH:$JAVA_HOME/bin

echo "Starting Gradle Build process..."
echo "Generating APK for Smart Screen OS..."

# Give execute permissions to gradlew if it exists (though we use system gradle)
if [ -f "gradlew" ]; then
    chmod +x gradlew
fi

# Run Gradle build for local Android APK generation
gradle assembleDebug

echo "Build process completed."
echo "You can find your APK in: app/build/outputs/apk/debug/"
