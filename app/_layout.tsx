import { useEffect, useState } from "react";
import { Stack, useNavigation } from "expo-router";
import { View, Text, ActivityIndicator } from "react-native";
import { Appbar } from "react-native-paper";
import { useFonts } from "expo-font";

function AppBar() {
  return (
    <Appbar.Header>
      <Appbar.Content title="ArcPay" 
        titleStyle={{ fontFamily: "DMSansBold", fontSize: 22 }}
      />
    </Appbar.Header>
  );
}

export default function RootLayout() {
  // Load custom fonts
  const [fontsLoaded] = useFonts({
    "DMSans": require("../assets/fonts/DMSans.ttf"),
    "DMSansBold": require("../assets/fonts/DMSansBold.ttf")
  });

  // Show loading indicator while fonts are loading
  if (!fontsLoaded) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator size="large" />
        <Text>Loading fonts...</Text>
      </View>
    );
  }

  return (
    <Stack
      screenOptions={{
        header: () => <AppBar />,
        headerTitleStyle: { fontFamily: "DMSansBold", fontSize: 20 }, // Apply font to headers
      }}
    />
  );
}