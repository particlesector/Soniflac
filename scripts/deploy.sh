#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────────────
# deploy.sh — Build and install SoniFlac on a connected device
#
# Usage:
#   ./scripts/deploy.sh              # fossDebug (default)
#   ./scripts/deploy.sh foss         # fossDebug
#   ./scripts/deploy.sh gplay        # gplayDebug
#   ./scripts/deploy.sh --apk-only   # build APK but don't install
# ──────────────────────────────────────────────────────────────────────
set -euo pipefail

# Always run from the project root, regardless of where the script is called from.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

FLAVOR="${1:-foss}"
APK_ONLY=false

if [[ "$FLAVOR" == "--apk-only" ]]; then
    APK_ONLY=true
    FLAVOR="foss"
fi

case "$FLAVOR" in
    foss)
        BUILD_TASK="assembleFossDebug"
        INSTALL_TASK="installFossDebug"
        APK_PATH="app/build/outputs/apk/foss/debug/app-foss-debug.apk"
        ;;
    gplay)
        BUILD_TASK="assembleGplayDebug"
        INSTALL_TASK="installGplayDebug"
        APK_PATH="app/build/outputs/apk/gplay/debug/app-gplay-debug.apk"
        ;;
    *)
        echo "Unknown flavor: $FLAVOR"
        echo "Usage: ./scripts/deploy.sh [foss|gplay|--apk-only]"
        exit 1
        ;;
esac

echo "=== SoniFlac Deploy — ${FLAVOR}Debug ==="

# Check for connected device (unless apk-only)
if [[ "$APK_ONLY" == false ]]; then
    if ! adb devices | grep -q "device$"; then
        echo ""
        echo "No device connected!"
        echo "Run ./scripts/adb-pair.sh first to pair your Pixel 6."
        exit 1
    fi

    DEVICE_MODEL=$(adb shell getprop ro.product.model 2>/dev/null || echo "unknown")
    echo "Target device: $DEVICE_MODEL"
fi

echo ""
echo "Building ${FLAVOR}Debug..."
./gradlew "$BUILD_TASK"

if [[ "$APK_ONLY" == true ]]; then
    echo ""
    echo "APK built: $APK_PATH"
    echo "To install manually:  adb install -r $APK_PATH"
else
    echo ""
    echo "Installing on device..."
    ./gradlew "$INSTALL_TASK"

    echo ""
    echo "Launching app..."
    adb shell am start -n "com.particlesector.soniflac/.MainActivity" 2>/dev/null || \
        echo "(Could not auto-launch — open SoniFlac manually on the device)"
fi

echo ""
echo "Done!"
