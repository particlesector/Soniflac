#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────────────
# adb-pair.sh — One-time wireless ADB pairing with your Pixel 6
#
# On your phone:
#   1. Settings → Developer options → Wireless debugging → ON
#   2. Tap "Pair device with pairing code"
#   3. Note the IP:PORT and 6-digit code shown on screen
#
# Then run this script from the devcontainer terminal.
# ──────────────────────────────────────────────────────────────────────
set -euo pipefail

echo "=== SoniFlac — Wireless ADB Pairing ==="
echo ""
echo "On your Pixel 6:"
echo "  Settings → Developer options → Wireless debugging"
echo "  Tap 'Pair device with pairing code'"
echo ""

read -rp "Pairing IP:PORT (e.g. 192.168.1.42:37015): " PAIR_ADDR
read -rp "6-digit pairing code: " PAIR_CODE

echo ""
echo "Pairing..."
adb pair "$PAIR_ADDR" "$PAIR_CODE"

echo ""
echo "Now enter the CONNECT address shown on the Wireless debugging screen"
echo "(this is different from the pairing address)."
echo ""
read -rp "Connect IP:PORT (e.g. 192.168.1.42:41235): " CONNECT_ADDR

echo ""
echo "Connecting..."
adb connect "$CONNECT_ADDR"

echo ""
echo "Verifying..."
adb devices

echo ""
echo "Done! Your Pixel 6 is connected."
echo "You can now run:  ./scripts/deploy.sh"
