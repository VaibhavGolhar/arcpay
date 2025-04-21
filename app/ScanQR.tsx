import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, Button, Alert } from 'react-native';
import { BarCodeScanner } from 'expo-barcode-scanner';
import { BleManager } from 'react-native-ble-plx';

const bleManager = new BleManager();

const ScanQRScreen = () => {
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const [scanned, setScanned] = useState(false);

  useEffect(() => {
    (async () => {
      const { status } = await BarCodeScanner.requestPermissionsAsync();
      setHasPermission(status === 'granted');
    })();
  }, []);

  const handleBarCodeScanned = ({ data }: { data: string }) => {
    setScanned(true);

    try {
      const parsed = JSON.parse(data);
      const { deviceName, deviceId } = parsed;

      Alert.alert('QR Scanned', `Device: ${deviceName}`, [
        {
          text: 'Connect',
          onPress: () => connectToDevice(deviceName, deviceId),
        },
        {
          text: 'Cancel',
          onPress: () => setScanned(false),
          style: 'cancel',
        },
      ]);
    } catch (err) {
      Alert.alert('Invalid QR', 'Could not parse QR data.');
      setScanned(false);
    }
  };

  const connectToDevice = (deviceName: string, deviceId: string) => {
    bleManager.startDeviceScan(null, null, (error, device) => {
      if (error) {
        Alert.alert('Scan Error', error.message);
        return;
      }

      if (!device?.name && !device?.localName) return;

      if (
        device.name === deviceName ||
        device.localName === deviceName ||
        device.id === deviceId
      ) {
        bleManager.stopDeviceScan();

        device
          .connect()
          .then(() => {
            Alert.alert('Connected', `Connected to ${device.name}`);
          })
          .catch((err) => {
            Alert.alert('Error', 'Failed to connect');
            console.error(err);
          });
      }
    });

    setTimeout(() => {
      bleManager.stopDeviceScan();
    }, 10000);
  };

  if (hasPermission === null) {
    return <Text>Requesting camera permission...</Text>;
  }
  if (hasPermission === false) {
    return <Text>No access to camera</Text>;
  }

  return (
    <View style={styles.container}>
      <BarCodeScanner
        onBarCodeScanned={scanned ? undefined : handleBarCodeScanned}
        style={StyleSheet.absoluteFillObject}
      />
      {scanned && <Button title="Tap to Scan Again" onPress={() => setScanned(false)} />}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default ScanQRScreen;
