import React from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import { useEffect, useState } from 'react';
import { Platform } from 'react-native';
import { BleManager } from 'react-native-ble-plx';

const YourQRScreen = () => {
  const [deviceInfo, setDeviceInfo] = useState({
    deviceName: 'MyBLEDevice123',
    deviceId: 'UNKNOWN',
    userId: 'user_123',
    amount: 100.0,
  });

  useEffect(() => {
    const bleManager = new BleManager();

    const fetchDevice = async () => {
      const state = await bleManager.state();
      if (state === 'PoweredOn') {
        bleManager.onStateChange((state) => {
          if (state === 'PoweredOn') {
            bleManager.destroy();
          }
        });
      }

      const id = await bleManager
        .devices([])
        .then(() => bleManager.connectedDevices([]))
        .catch(() => []);

      setDeviceInfo((prev) => ({
        ...prev,
        deviceId: id[0]?.id || '123-456-789', // fallback for testing
      }));
    };

    fetchDevice();
  }, []);

  const qrData = JSON.stringify(deviceInfo);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Your QR Code</Text>
      <QRCode value={qrData} size={250} />
      <Text style={styles.infoText}>Scan this QR on sender's device</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  infoText: {
    marginTop: 20,
    color: 'gray',
  },
});

export default YourQRScreen;
